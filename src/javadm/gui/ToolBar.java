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

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.net.URL;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javadm.com.Download;
import javax.swing.JFileChooser;
//import javax.swing.border.EmptyBorder;

public class ToolBar extends JPanel
        implements ActionListener {

    protected JTextArea textArea;
    protected String newline = "\n";

    static final private String MENU = "menu";
    static final private String ADD = "add";
    static final private String REMOVE = "remove";
    static final private String START = "start";
    static final private String STOP = "stop";
    static final private String SETTING = "setting";

    private Download download;
    private JToolBar toolBar;

    public ToolBar() {
        super(new BorderLayout());
        toolBar = new JToolBar();
        addButtons(toolBar);
        toolBar.setFloatable(false);
        toolBar.setRollover(false);
        toolBar.setBorderPainted(false);
        toolBar.setOpaque(false);
        add(toolBar, BorderLayout.PAGE_START);
    }

    public void setDownload(Download download) {
        this.download = download;
    }

    protected void addButtons(JToolBar toolBar) {
        JButton button = null;

        button = formatButton(MENU, "JDM Menu");
        toolBar.add(button);

        button = formatButton(ADD, "Add new download");
        toolBar.add(button);

        //second button
        button = formatButton(REMOVE, "Remove this download");
        toolBar.add(button);

        //third button
        button = formatButton(START, "Start selected download");
        toolBar.add(button);

        button = formatButton(STOP, "Stop selected download");
        toolBar.add(button);

        button = formatButton(SETTING, "Selected download option");
        toolBar.add(button);

        //separator
        toolBar.addSeparator();

    }

    protected JButton formatButton(String name, String toolTipText) {
        //Look for the image.
        String imgLocation = "/" + name + ".png";
        URL imageURL = getClass().getResource(imgLocation);

        //Create and initialize the button.
        JButton button = new JButton();
        button.setActionCommand(name);
        button.setToolTipText(toolTipText);
        button.addActionListener(this);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        if (imageURL != null) {                      //image found
            button.setIcon(new ImageIcon(imageURL, name));
        } else {
            Font font = new Font(button.getFont().toString(), Font.BOLD, 20);
            button.setFont(font);                                   //no image found
            button.setText(name);

            System.err.println("Resource not found: "
                    + imgLocation);
        }

        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        if (null != cmd) // Handle each button.
        {
            switch (cmd) {
                case MENU:
                    //first button clicked
                    chooseFolder();
                    break;
                case ADD:
                    // second button clicked
                    new ModalDialog((JFrame) SwingUtilities.getWindowAncestor(this), this.download);
                    break;
                case REMOVE:

                    break;
                case START:

                    break;
                case STOP:

                    break;
                case SETTING:
                    new ModalDialog((JFrame) SwingUtilities.getWindowAncestor(this), download);
                    break;
                default:
                    break;
            }
        }

    }

    public void chooseFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Save to");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //
        // disable the "All files" option.
        //
        chooser.setAcceptAllFileFilterUsed(false);
        //    
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            System.out.println("getCurrentDirectory(): "
                    + chooser.getCurrentDirectory());
            System.out.println("getSelectedFile() : "
                    + chooser.getSelectedFile());
        } else {
            System.out.println("No Selection ");
        }
    }

}
