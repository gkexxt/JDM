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
package javadm.gui;

/**
 *
 * @author G.K #gkexxt@outlook.com
 */
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JTextField;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;

import java.net.URL;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javadm.com.Download;
import javax.swing.JLabel;
//import javax.swing.border.EmptyBorder;

public class ToolBar extends JPanel
        implements ActionListener, PropertyChangeListener {

    protected JTextArea textArea;
    protected String newline = "\n";
    static final private String PREVIOUS = "previous";
    static final private String UP = "up";
    static final private String NEXT = "next";
    static final private String SOMETHING_ELSE = "other";
    static final private String TEXT_ENTERED = "text";
    private Download download;
    private JToolBar toolBar;

    public ToolBar() {
        super(new BorderLayout());

        //Create the toolbar.
        toolBar = new JToolBar("Still draggable");
        addButtons(toolBar);
        toolBar.setFloatable(false);
        toolBar.setRollover(false);
        toolBar.setBorderPainted(false);
        toolBar.setOpaque(false);
        //toolBar.setBorder();
        //toolBar.setBackground(Color.white);
        

        //Create the text area used for output.  Request
        //enough space for 5 rows and 30 columns.
        //Lay out the main panel.
        //setPreferredSize(new Dimension(450, 130));
        add(toolBar, BorderLayout.PAGE_START);
        //add(scrollPane, BorderLayout.CENTER);

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println("Name      = " + evt.getPropertyName());
        System.out.println("Old Value = " + evt.getOldValue());
        System.out.println("New Value = " + evt.getNewValue());
    }

    protected void addButtons(JToolBar toolBar) {
        JButton button = null;

     
        
                button = makeNavigationButton("menu", PREVIOUS,
                "menu",
                "+");
        button.setForeground(Color.green);
        toolBar.add(button);
        
        button = makeNavigationButton("add", PREVIOUS,
                "Add new download",
                "+");
        button.setForeground(Color.green);
        toolBar.add(button);

        //second button
        button = makeNavigationButton("remove", UP,
                "Remove this download",
                "x");
        button.setForeground(Color.red);
        toolBar.add(button);

        //third button
        button = makeNavigationButton("setting", NEXT,
                "Forward to something-or-other",
                "Next");
        toolBar.add(button);

        button = makeNavigationButton("start", NEXT,
                "Forward to something-or-other",
                "Next");
        toolBar.add(button);

        button = makeNavigationButton("stop", NEXT,
                "Forward to something-or-other",
                "Next");
        toolBar.add(button);

        //separator
        toolBar.addSeparator();



    }

    protected JButton makeNavigationButton(String imageName,
            String actionCommand,
            String toolTipText,
            String altText) {
        //Look for the image.
        String imgLocation = "/"
                + imageName
                + ".png";
        URL imageURL = getClass().getResource(imgLocation);

        //Create and initialize the button.
        JButton button = new JButton();
        button.setActionCommand(actionCommand);
        button.setToolTipText(toolTipText);
        button.addActionListener(this);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        //Color xxx = button.getBackground();
        //toolBar.setBackground(xxx);
        if (imageURL != null) {                      //image found
            button.setIcon(new ImageIcon(imageURL, altText));
        } else {
            Font font = new Font(button.getFont().toString(), Font.BOLD, 20);
            button.setFont(font);                                   //no image found
            button.setText(altText);

            System.err.println("Resource not found: "
                    + imgLocation);
        }

        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        String description = null;

        if (null != cmd) // Handle each button.
        {
            switch (cmd) {
                case PREVIOUS:
                    //first button clicked
                    description = "taken you to the previous <something>.";
                    break;
                case UP:
                    // second button clicked
                    description = "taken you up one level to <something>.";
                    break;
                case NEXT:
                    // third button clicked
                    description = "taken you to the next <something>.";
                    break;
                case SOMETHING_ELSE:
                    // fourth button clicked
                    description = "done something else.";
                    break;
                case TEXT_ENTERED:
                    // text field
                    JTextField tf = (JTextField) e.getSource();
                    String text = tf.getText();
                    tf.setText("");
                    description = "done something with this text: "
                            + newline + "  \""
                            + text + "\"";
                    break;
                default:
                    break;
            }
        }

        displayResult("If this were a real app, it would have "
                + description);
    }

    protected void displayResult(String actionDescription) {
        textArea.append(actionDescription + newline);
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("ToolBarDemo2");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(new ToolBar());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(() -> {
            //Turn off metal's use of bold fonts
            UIManager.put("swing.boldMetal", Boolean.FALSE);
            createAndShowGUI();
        });
    }
}
