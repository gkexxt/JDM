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

import javax.swing.JTextArea;
import javax.swing.JPanel;

import java.net.URL;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javadm.com.Download;
import javadm.data.Data;
import javadm.data.DataDaoSqlite;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
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
    private final MainFrame mainframe;
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
    public ToolBar(MainFrame mainframe) {
        super(new BorderLayout());
        this.mainframe = mainframe;
        toolBar = new JToolBar();
        addButtons(toolBar);
        toolBar.setFloatable(false);
        toolBar.setRollover(false);
        toolBar.setBorderPainted(false);
        toolBar.setOpaque(false);
        add(toolBar, BorderLayout.PAGE_START);
        //mDialog = new ModalDialog(frm);
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
        popup.add(new JMenuItem(new AbstractAction("Option 1") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(mainframe, "Option 1 selected");
            }
        }));
        popup.add(new JMenuItem(new AbstractAction("Option 2") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(mainframe, "Option 2 selected");
            }
        }));

        //separator
        toolBar.addSeparator();

    }

    public void refreshToolBar() {
        //System.err.println(selectedDownload.getData().isComplete());
        try {

            selectedDownload = model.getRow(table.getSelectedRow());
            btnRemove.setVisible(true);
            btnSetting.setVisible(true);
            if (selectedDownload.getData().isComplete()) {
                System.err.println("blb");
                btnRestart.setVisible(true);
                btnStop.setVisible(false);
                btnStart.setVisible(false);

            } else if (selectedDownload.isStart()) {
                btnRestart.setVisible(false);
                btnStop.setVisible(true);
                btnStart.setVisible(false);
            } else {
                btnRestart.setVisible(false);
                btnStop.setVisible(false);
                btnStart.setVisible(true);
            }
            //toolBar.repaint();

        } catch (Exception e) {
            selectedDownload = null;
            btnRestart.setVisible(false);
            btnStop.setVisible(false);
            btnStart.setVisible(false);
            btnRemove.setVisible(false);
            btnSetting.setVisible(false);

        }

        //btn
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
                    popup.show(btnMenu, btnMenu.getLocation().x, btnMenu.getLocation().y + btnMenu.getHeight());
                    break;
                case ADD:
                    // second button clicked
                    Download newDownload = new Download();
                    newDownload.setData(new Data());
                    newDownload.setDownloadControl(new DownloadControl());
                    OptionDialog newDwn = new OptionDialog(mainframe, model, true, newDownload, true);
                    newDwn.setTitle("Add New download");
                    newDwn.setLocation(mainframe.getLocation().x - (newDwn.getWidth() - mainframe.getWidth()) / 2, mainframe.getLocation().y - (newDwn.getHeight() - mainframe.getHeight()) / 2);
                    newDwn.setVisible(true);
                    break;
                case REMOVE:

                        mainframe.removeDownload();

                    break;
                case START:
                    selectedDownload.setStart(true);
                    break;
                case STOP:
                    selectedDownload.setStart(false);
                    break;
                case RESTART:
                    selectedDownload.getData().setComplete(false);
                    selectedDownload.getData().setDoneSize(0);
                    selectedDownload.setStart(true);
                    break;
                case SETTING:
                    OptionDialog xxx = new OptionDialog(mainframe, model, true, selectedDownload, false);
                    xxx.setTitle("Download Options- " + selectedDownload.getData().getName());
                    xxx.setLocation(mainframe.getLocation().x - (xxx.getWidth() - mainframe.getWidth()) / 2, mainframe.getLocation().y - (xxx.getHeight() - mainframe.getHeight()) / 2);
                    xxx.setVisible(true);
                    break;
                case SCHEDULE:
                    System.err.println(showOptionPane("wannaa bla bla", SCHEDULE));
                    break;
                case GRAPH:
                    mainframe.showDetails();
                    break;
                default:
                    break;
            }
        }

    }

}
