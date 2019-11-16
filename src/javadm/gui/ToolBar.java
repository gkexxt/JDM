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
import javadm.data.Data;
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
    static final private String RESTART = "restart";
    static final private String SETTING = "setting";
    private MainFrame mframe;
    private Download currentDownload;
    private JToolBar toolBar;
    private JButton btnMenu;
    private JButton btnAdd;
    private JButton btnRemove;
    private JButton btnStart;
    private JButton btnStop;
    private JButton btnSetting;
    private JButton btnCtrl;
    private JButton btnRestart;
    private TableModel model;

    //private ModalDialog mDialog; 
    public ToolBar(MainFrame frm,TableModel model) {
        super(new BorderLayout());
        this.mframe = frm;
        this.model = model;
        toolBar = new JToolBar();
        addButtons(toolBar);
        toolBar.setFloatable(false);
        toolBar.setRollover(false);
        toolBar.setBorderPainted(false);
        toolBar.setOpaque(false);
        add(toolBar, BorderLayout.PAGE_START);
        //mDialog = new ModalDialog(frm);
    }

    public void setCurrentDownload(Download currentDownload) {
        this.currentDownload = currentDownload;
        updateToolBarState();
        toolBar.repaint();
    }

    protected void addButtons(JToolBar toolBar) {
        btnCtrl = new JButton();

        btnMenu = formatButton(MENU, "JDM Menu");
        toolBar.add(btnMenu);

        btnAdd = formatButton(ADD, "Add new download");
        toolBar.add(btnAdd);

        //second button
        btnRemove = formatButton(REMOVE, "Remove this download");
        toolBar.add(btnRemove);

        //third button
        btnStart = formatButton(START, "Start selected download");
        toolBar.add(btnStart);

        btnStop = formatButton(STOP, "Stop selected download");
        toolBar.add(btnStop);

        btnRestart = formatButton(RESTART, "Restart selected download");
        toolBar.add(btnRestart);

        btnSetting = formatButton(SETTING, "Selected download option");
        toolBar.add(btnSetting);

        //separator
        toolBar.addSeparator();

    }

    public void updateToolBarState() {
        //System.err.println(currentDownload.getData().isComplete());
        if (currentDownload.getData().isComplete()) {
            System.err.println("blb");
            btnRestart.setVisible(true);
            btnStop.setVisible(false);
            btnStart.setVisible(false);

        } else if (currentDownload.isStart()) {
            btnRestart.setVisible(false);
            btnStop.setVisible(true);
            btnStart.setVisible(false);
        } else {
            btnRestart.setVisible(false);
            btnStop.setVisible(false);
            btnStart.setVisible(true);
        }

        //btn
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
                    //chooseFolder();
                    break;
                case ADD:
                    // second button clicked
                    Download newDownload = new Download();
                    newDownload.setData(new Data());
                    newDownload.setDownloadControl(new DownloadControl());
                    OptionDialog newDwn = new OptionDialog(mframe,model, true, newDownload,true);
                    newDwn.setTitle("Add New download");
                    newDwn.setLocation(mframe.getLocation().x - (newDwn.getWidth() - mframe.getWidth()) / 2, mframe.getLocation().y - (newDwn.getHeight() - mframe.getHeight()) / 2);
                    newDwn.setVisible(true);
                    break;
                case REMOVE:

                    break;
                case START:
                    System.out.println("javadm.gui.ToolBar.actionPerformed() start");
                    break;
                case STOP:

                    break;
                case SETTING:
                    OptionDialog xxx = new OptionDialog(mframe,model, true, currentDownload,false);
                    xxx.setTitle("Download Options- " + currentDownload.getData().getName());
                    xxx.setLocation(mframe.getLocation().x - (xxx.getWidth() - mframe.getWidth()) / 2, mframe.getLocation().y - (xxx.getHeight() - mframe.getHeight()) / 2);
                    xxx.setVisible(true);
                    break;
                default:
                    break;
            }
        }

    }

}
