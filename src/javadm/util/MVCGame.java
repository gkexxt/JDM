/* 
 * The MIT License
 *
 * Copyright 2019 G.K #gkexxt@outlook.com.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package javadm.util;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author gkalianan
 */


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MVCGame implements Runnable {
        public static void main(String[] args) {
        EventQueue.invokeLater(new MVCGame());
    }

    @Override
    public void run() {
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainPanel Window = new MainPanel();
        Window.AddComp();
        f.add(Window);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}

class MainPanel extends JPanel {
        View view ;
        Control control ;
        JLabel label  ;

    public MainPanel() {
        super(new BorderLayout());
        Model model = new Model();
        view = new View(model);
        control = new Control(model, view);
        label = new JLabel();
        
    }
    
    public void AddComp(){
        this.add(label, BorderLayout.NORTH);
        this.add(view, BorderLayout.CENTER);
        this.add(control, BorderLayout.SOUTH);
    }
}

/**
 * Control panel
 */
class Control extends JPanel {

    private Model model;
    private View view;
    private JButton reset = new JButton("Reset");

    public Control(Model model, View view) {
        this.model = model;
        //this.view = view;
        this.add(reset);
        reset.addActionListener(new ButtonHandler());
    }

    private class ButtonHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            if ("Reset".equals(cmd)) {
                model.reset();
            }
        }
    }
}

/**
 * View
 */
class View extends JPanel {

    private static final String s = "Click a button.";
    private Model model;
    private ColorIcon icon = new ColorIcon(80, Color.gray);
    private JLabel label = new JLabel(s, icon, JLabel.CENTER);

    public View(Model model) {
        super(new BorderLayout());
        this.model = model;
        label.setVerticalTextPosition(JLabel.BOTTOM);
        label.setHorizontalTextPosition(JLabel.CENTER);
        this.add(label, BorderLayout.CENTER);
        this.add(genButtonPanel(), BorderLayout.SOUTH);
        model.addObserver(new ModelObserver());
    }

    private JPanel genButtonPanel() {
        JPanel panel = new JPanel();
        for (Piece p : Piece.values()) {
            PieceButton pb = new PieceButton(p);
            pb.addActionListener(new ButtonHandler());
            panel.add(pb);
        }
        return panel;
    }

    private class ModelObserver implements Observer {

        @Override
        public void update(Observable o, Object arg) {
            if (arg == null) {
                label.setText(s);
                icon.color = Color.gray;
            } else {
                if ((Boolean) arg) {
                    label.setText("Win!");
                } else {
                    label.setText("Keep trying.");
                }
            }
        }
    }

    private class ButtonHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            PieceButton pb = (PieceButton) e.getSource();
            icon.color = pb.piece.color;
            label.repaint();
            model.check(pb.piece);
        }
    }

    private static class PieceButton extends JButton {

        Piece piece;

        public PieceButton(Piece piece) {
            this.piece = piece;
            this.setIcon(new ColorIcon(16, piece.color));
        }
    }

    private static class ColorIcon implements Icon {

        private int size;
        private Color color;

        public ColorIcon(int size, Color color) {
            this.size = size;
            this.color = color;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.fillOval(x, y, size, size);
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }
}

/**
 * Model
 */
class Model extends Observable {

    private static final Random rnd = new Random();
    private static final Piece[] pieces = Piece.values();
    private Piece hidden = init();

    private Piece init() {
        return pieces[rnd.nextInt(pieces.length)];
    }

    public void reset() {
        hidden = init();
        setChanged();
        notifyObservers();
    }

    public void check(Piece guess) {
        setChanged();
        notifyObservers(guess.equals(hidden));
    }
}

enum Piece {

    Red(Color.red), Green(Color.green), Blue(Color.blue);
    public Color color;

    private Piece(Color color) {
        this.color = color;
    }
}