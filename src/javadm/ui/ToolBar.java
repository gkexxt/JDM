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
package javadm.ui;

/**
 *
 * @author G.K #gkexxt@outlook.com
 */
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.ImageIcon;

import javax.swing.JTextArea;
import javax.swing.JPanel;

import java.net.URL;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import javadm.com.Download;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
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
    static final private String SCHEDULE = "schedule";
    static final private String GRAPH = "graph";
    private final DownloadManager dm;
    private Download selectedDownload;
    private final JToolBar toolBar;
    private JButton btnMenu;
    private JButton btnAdd;
    private JButton btnRemove;
    private JButton btnStart;
    private JButton btnStop;
    private JButton btnSetting;
    private JButton btnRestart;
    private JButton btnSchedule;
    private JButton btnGraph;
    private JPopupMenu popup;

    //private ModalDialog mDialog; 
    public ToolBar(DownloadManager downloadManager) {
        super(new BorderLayout());
        this.dm = downloadManager;
        toolBar = new JToolBar();
        addButtons(toolBar);
        toolBar.setFloatable(false);
        toolBar.setRollover(false);
        toolBar.setBorderPainted(false);
        toolBar.setOpaque(false);
        add(toolBar, BorderLayout.PAGE_START);
        //mDialog = new ModalDialog(frm);
        refreshToolBar();
    }

    protected void addButtons(JToolBar toolBar) {

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

        btnSchedule = formatButton(SCHEDULE, "Schedule download");
        toolBar.add(btnSchedule);

        btnGraph = formatButton(GRAPH, "Show download details");
        toolBar.add(btnGraph);

        popup = new JPopupMenu();
        popup.add(new JMenuItem(new AbstractAction("Settings") {
            @Override
            public void actionPerformed(ActionEvent e) {
                //JOptionPane.showMessageDialog(dm, "Option 1 selected");
                SettingMenu syssetting = new SettingMenu(dm, enabled);
                syssetting.setVisible(true);
                syssetting.setLocationRelativeTo(dm);

            }
        }));

        JMenu submenu = new JMenu("Help");

        submenu.add(new JMenuItem(new AbstractAction("Online Help") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("http://www.example.com"));
                } catch (Exception ex) {
                    dm.showInfo(ex.getMessage() + "\n" + "Please vist : http://www.example.com", "Online Help", JOptionPane.INFORMATION_MESSAGE);
                }

            }
        }));

        submenu.add(new JMenuItem(new AbstractAction("About") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(dm,
                        "JDM - Java Download Manager\n\n"
                        + "By :\ngkexxt@outlook.com\n\n"
                        + "Dedicated To :\n"
                        + "Tillaga\n"
                        + "Devna\n"
                        + "Divina",
                        "About",
                        JOptionPane.PLAIN_MESSAGE);
            }
        }));
        popup.add(submenu);

        popup.add(new JMenuItem(new AbstractAction("Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        }));
        //separator
        //toolBar.addSeparator();

    }

    public void refreshToolBar() {
        selectedDownload = dm.getSelectedDownload();
        if (selectedDownload == null) {
            btnRestart.setVisible(false);
            btnStop.setVisible(false);
            btnStart.setVisible(false);
            btnRemove.setVisible(false);
            btnSetting.setVisible(false);
            btnGraph.setVisible(false);
            btnSchedule.setVisible(false);
            return;
        } else if (selectedDownload.isComplete()) {
            btnRestart.setVisible(true);
            btnStop.setVisible(false);
            btnStart.setVisible(false);
            btnRemove.setVisible(true);

        } else if (selectedDownload.isStart()) {
            btnRestart.setVisible(false);
            btnStop.setVisible(true);
            btnStart.setVisible(false);
            btnRemove.setVisible(false);
        } else {
            
            btnRestart.setVisible(false);
            btnStop.setVisible(false);
            btnStart.setVisible(true);
            btnRemove.setVisible(true);
        }
        
        btnSetting.setVisible(true);
        btnGraph.setVisible(true);
        btnSchedule.setVisible(true);
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
                    popup.show(btnMenu, btnMenu.getLocation().x,
                            btnMenu.getLocation().y + btnMenu.getHeight());
                    break;
                case ADD:
                    // second button clicked 
                    Download dwn = new Download();
                    //dwn.setDirectory(dm.getSetting().getDirectory());
                    //System.err.println(dwn.getDirectory());
//                    if(true){
//                    return;}
                    //dwn.setConnections(dm.getSetting().getConnectionCount());
                    new OptionMenu(dm, dwn, true, true);

                    break;
                case REMOVE:
                    dm.removeDownload();
                    break;
                case START:
                    dm.startDownload();
                    break;
                case STOP:
                    dm.stopDownload();
                    break;
                case RESTART:
                    dm.restartDownload();
                    break;
                case SETTING:
                     new OptionMenu(dm, dm.getSelectedDownload(), true, false);
                    //seldownload.setTitle("Download Options- " + selectedDownload.getData().getName());
                    break;
                case SCHEDULE:
                    //System.err.println(showChoice("wannaa bla bla", SCHEDULE));
                    break;
                case GRAPH:
                    dm.showStatusPane();
                    break;
                default:
                    break;
            }
        }

    }

}
