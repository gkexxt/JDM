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
 * Java Download manager GUI class
 *
 * @author gk
 */
import javadm.util.ClipboardTextListener;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import static java.time.LocalDateTime.now;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimerTask;
import javadm.com.Setting;
import javadm.com.Download;
import javadm.com.DaoSqlite;
import javadm.com.Part;
import javax.swing.*;
import javax.swing.event.*;

public class DownloadManager extends JFrame
        implements ListSelectionListener, PropertyChangeListener {

    //private final DataManager dm = new DataManager();//data manager handle items data
    private JSplitPane splitPaneHortizontal;
    private JSplitPane splitPaneVertical;
    private final JList list = new JList(); //hold download items data
    private DownloadTable table;
    private TableModel model;
    private JSplitPane mainsplitpane;
    private ToolBar toolbar;
    private StatusPane statusPane;
    private ClipboardTextListener clplstn;
    private Setting setting;
    private String clipedUrl;
    private java.util.List<Download> downloads;

    public DownloadManager() {

    }

    //Listens to the list
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }

        //dm.addlist("tttttt");
    }

    /**
     * Create the DownloadManager and show it.
     */
    public void showMe() {
        DefaultListModel listModel = new DefaultListModel();
        String[] category = {"All", "Active", "Inactive", "Completed"};
        for (String item : category) {
            listModel.addElement(item);
        }

        list.setModel(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        clplstn = new ClipboardTextListener();

        model = new TableModel(this);
        table = new DownloadTable(model);

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        toolbar = new ToolBar(this);

        //Vertical panels - downloadpane+StatusPane
        statusPane = new StatusPane(this);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //table.setRowSelectionInterval(0, 0);
        table.setOpaque(false);
        JScrollPane categoryPane = new JScrollPane(list);
        JScrollPane downloadPane = new JScrollPane(table);

        splitPaneHortizontal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                categoryPane, downloadPane);

        splitPaneVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                splitPaneHortizontal, statusPane);

        splitPaneHortizontal.setBorder(null);//need for nesting panel - remove double border
        splitPaneHortizontal.setOpaque(false);
        //HORIZONTAL_SPLIT panels - categorypane+splitPaneVertical(nested)

        mainsplitpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, toolbar, splitPaneVertical);
        mainsplitpane.setEnabled(false);
        mainsplitpane.setDividerSize(1);
        mainsplitpane.setBackground(Color.WHITE);
        //Provide minimum sizes for components in the split pane.
        Dimension minimumSize = new Dimension(100, 100);
        categoryPane.setMinimumSize(minimumSize);
        downloadPane.setPreferredSize(new Dimension(300, 500));
        downloadPane.setBackground(Color.white);
        downloadPane.setForeground(Color.white);
        statusPane.setMinimumSize(minimumSize);
        statusPane.setOpaque(false);
        //Provide a preferred size for the split pane.
        splitPaneHortizontal.setPreferredSize(new Dimension(600, 400));
        splitPaneHortizontal.setDividerSize(6);
        splitPaneVertical.setDividerSize(6);
        splitPaneVertical.setDividerLocation(400);
        splitPaneVertical.setPreferredSize(new Dimension(600, 400));

        table.getSelectionModel().addListSelectionListener((ListSelectionEvent event) -> {
            toolbar.refreshToolBar();
            statusPane.updateErrorView();
        });

        //Create and set up the stage.
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        //Display stage.
        this.getContentPane().add(mainsplitpane);
        this.setLocationByPlatform(true);
        this.pack();

        final Graphics2D gx = (Graphics2D) toolbar.getGraphics();
        final AffineTransform t = gx.getTransform();
        final double scaling = t.getScaleX(); // Assuming square pixels :P
        System.err.println(scaling);
        t.setToScale(1, 1);
        gx.setTransform(t);
        toolbar.repaint();
        this.setVisible(true);
        //add lisner for the selection list --ide compalining when doit in constructor why?
        list.addListSelectionListener(this);
        //set div for split panes -for ratio 0-1 must be done after panes are realized?        
        statusPane.setVisible(false);
        splitPaneVertical.setDividerLocation(0.6);
        splitPaneHortizontal.setDividerLocation(0.3);
        URL imageURL = getClass().getResource("/jdm24.png");
        ImageIcon img = new ImageIcon(imageURL);
        this.setIconImage(img.getImage());
        this.setTitle("JDM");
        checkDB();
        toolbar.setScale();
        //SettingDaoSqlite dbx = new SettingDaoSqlite();
        //this.refreshTable();
        //ClipboardTextListener xxx = new ClipboardTextListener();
    }

    private void checkDB() {
        DaoSqlite db = new DaoSqlite();
        boolean settingok = db.isTableExists("setting");
        boolean dataok = db.isTableExists("downloaddata");
        boolean partok = db.isTableExists("downloadpart");
        if (!dataok || !settingok || !partok) {

            if (!dataok) {
                if (showChoice("Initialize new Data Table?\nClicking No will exit the app",
                        "Data Table Error", JOptionPane.ERROR_MESSAGE) < 1) {
                    newDataTable();
                } else {
                    System.exit(0);
                }
            }
            if (!settingok) {
                if (showChoice("Initalize new Setting Table?\nClicking No will exit the app",
                        "Setting Table Error", JOptionPane.ERROR_MESSAGE) < 1) {
                    newSettingTable();
                } else {
                    System.exit(0);
                }
            }

            if (!partok) {
                if (showChoice("Initalize new Part Table?\nClicking No will exit the app",
                        "Setting Table Error", JOptionPane.ERROR_MESSAGE) < 1) {
                    newSettingTable();
                } else {
                    System.exit(0);
                }
            }
            //here after tables are repaired
            settingok = db.isTableExists("setting");
            dataok = db.isTableExists("downloaddata");
            partok = db.isTableExists("downloadpart");
            if (settingok && dataok && partok) {
                showInfo("Tables Initialized", "JDM", JOptionPane.INFORMATION_MESSAGE);
                setupDownloads();
            } else {
                showInfo("Error Initializing Tables\nJDM will Exit", "JDM Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }

        } else if (settingok && dataok) {
            setupDownloads();
        }
    }

    private void newSettingTable() {
        System.out.println("javadm.ui.DownloadManager.newSettingTable()");
    }

    private void newDataTable() {
        System.out.println("javadm.ui.DownloadManager.newDataTable()");
    }

    private void scheduller() {
        for (Download download : downloads) {
            //System.out.println("javadm.ui.DownloadManager.scheduller() : " + download.getName());
            if (!download.isRunning() && download.getScheduleStart() != null && download.getScheduleStart().compareTo(new Date()) < 0) {
                System.out.println("javadm.ui.DownloadManager.scheduller() : " + download.getName());
            }

            if (!download.isRunning() && download.getScheduleStop() != null && download.getScheduleStop().compareTo(new Date()) < 0) {
                System.out.println("javadm.ui.DownloadManager.scheduller() : " + download.getName());
            }

        }
    }

    public void setupDownloads() {
        DaoSqlite db = new DaoSqlite();
        downloads = db.getAllDownload();
        for (Download download : downloads) {
            download.setParts(db.getParts(download.getId()));
            download.setProgress(0);
            model.addRow(download);
            download.addPropertyChangeListener(model);
            download.addPropertyChangeListener(this);
        }

        setting = db.getSetting();

        if (setting.getMonitorMode() > 0) {
            startClipListner();
        }

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            boolean alldownloadsStoped = true;

            @Override
            //stopping running download beforw closing
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                while (true) {
                    alldownloadsStoped = true;
                    for (Download download : downloads) {
                        if (download.isRunning() || download.isNeedupdate()) {
                            alldownloadsStoped = false;
                            download.stopDownload();
                        }
                    }
                    if (alldownloadsStoped) {
                        System.exit(0);
                    }

                }

            }
        });
       // refreshTable();

    }

    /**
     * refresh download table by timer
     */
    public void refreshTable() {
        java.util.Timer t = new java.util.Timer();
        TimerTask tt;
        tt = new TimerTask() {
            @Override
            public void run() {
                //updateUI();
                //model.fireTableRowsUpdated(0, model.getRowCount() - 1);
                //toolbar.refreshToolBar();
                scheduller();
            }
        };
        t.scheduleAtFixedRate(tt, 0, 1000);
    }

    /**
     * show option pane with OK & Cancel message on mainframe.
     *
     * @param message
     * @param title
     * @param type
     * @return
     */
    public int showChoice(String message, String title, int type) {
        int result = JOptionPane.showConfirmDialog(this, message,
                title, JOptionPane.YES_NO_OPTION, type);
        return result;
    }

    /**
     * option pane OK
     *
     * @param message
     * @param title
     * @param type
     */
    public void showInfo(String message, String title, int type) {
        JOptionPane.showConfirmDialog(this, message,
                title, JOptionPane.CLOSED_OPTION, type);
        System.out.println("javadm.gui.MainFrame.showOptionPaneOK()");

    }

    /**
     * return reference of currently selected download object on list
     *
     * @return Download
     */
    public Download getSelectedDownload() {
        try {
            Download seldwn = model.getRow(table.getSelectedRow());
            return seldwn;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * remove selected download from the list
     *
     * @param trash
     */
    public void removeDownload(boolean trash) {

        Download download = getSelectedDownload();
        //removing from download list
        for (int i = 0; i < downloads.size(); i++) {
            if (downloads.get(i).getId() == download.getId()) {
                downloads.remove(i);
            }
        }
        model.removeTableModelListener(table);//remove table listner before delete from model
        model.removeRows(table.getSelectedRow());
        model.addTableModelListener(table);//add it back

        //remove file
        if (trash) {
            try {
                File file = new File(download.getDirectory() + "/" + download.getName());
                file.delete();
            } catch (Exception ex) {
            }
        }

        //remove parts
        for (Part part : download.getParts()) {
            File file = new File(download.getDirectory() + "/" + part.getPartFileName());
            file.delete();
        }

        //remove from db
        DaoSqlite db = new DaoSqlite();
        db.deleteDownload(download.getId());
        db.deleteParts(download.getId());
        toolbar.refreshToolBar();
        statusPane.setVisible(false);
        model.fireTableDataChanged();
    }

    /**
     * start selected download
     */
    public void startDownload() {
        getSelectedDownload().startDownload();
    }

    /**
     * stop selected download
     */
    public void stopDownload() {
        getSelectedDownload().stopDownload();
    }

    /**
     * remove data and reboot the download
     *
     * @param trash
     * @param autoStart
     */
    public void restartDownload(boolean trash, boolean autoStart) {

        Download download = getSelectedDownload();
        if (trash) {
            try {
                File file = new File(download.getDirectory() + "/" + download.getName());
                file.delete();
            } catch (Exception ex) {
            }
        }

        for (Part part : download.getParts()) {
            File file = new File(download.getDirectory() + "/" + part.getPartFileName());
            file.delete();
        }
        download.setComplete(false);
        download.setCompleteDate(null);
        download.setDoneSize(0);
        download.setType(Download.UNKNOWN);
        download.setFileSize(0);
        download.setProgress(0);
        download.setParts(new ArrayList<>());
        DaoSqlite db = new DaoSqlite();
        db.updateDownload(download);
        db.deleteParts(download.getId());
        if (autoStart) {
            download.startDownload();
        }
    }

    /**
     * add new download to the list/table
     *
     * @param download
     * @param autoStart
     */
    public void addDownload(Download download, Boolean autoStart) {
        download.setName(download.getName().replaceAll("[^a-zA-Z0-9\\.\\-]", ""));
        DaoSqlite db = new DaoSqlite();
        db.insertDownload(download);
        download = db.getLastDownload();
        downloads.add(download);
        model.addRow(download);
        download.addPropertyChangeListener(this);
        if (autoStart) {
            download.startDownload();
        }
    }

    /**
     * update existing download
     *
     * @param download
     */
    public void updateDownload(Download download) {
        download.setName(download.getName().replaceAll("[^a-zA-Z0-9\\.\\-]", ""));
        DaoSqlite db = new DaoSqlite();
        db.updateDownload(download);
        model.fireTableRowsUpdated(0, model.getRowCount());
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {

        //listen to clipboard
        try {
            /* 
            -get os name and use proper laf               
            -getSystemLookAndFeelClassName return nothing in kde               \
            -metal laf is butt ugly keepit as last choice.
            -custom laf??? nah...too much work
             */
            String osname = System.getProperty("os.name").toLowerCase();
            if (osname.contains("linux")) {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } else if (System.getProperty("os.name").contains("window")) {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } else {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }

            //run ui/app
            javax.swing.SwingUtilities.invokeLater(() -> {
                DownloadManager frame = new DownloadManager();
                frame.showMe();
                //frame.refreshTable();

            });

        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    /**
     * show /hide the StatusPane
     */
    public void showStatusPane() {
        statusPane.setVisible(!statusPane.isVisible());
        if (statusPane.isVisible()) {
            splitPaneVertical.setDividerLocation(0.6);
        }
    }

    public void startClipListner() {
        clplstn.addPropertyChangeListener(this);
        clplstn.startMonitor();
    }

    public void stopClipListner() {
        clplstn.removePropertyChangeListener(this);
        clplstn.terminate();
    }

    public Setting getSetting() {
        return setting;
    }

    public String chooseFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Save to");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setFileHidingEnabled(true);
        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            return (chooser.getSelectedFile().toString());
        }
        return "";
    }

    public void setSetting(Setting setting) {
        this.setting = setting;
        DaoSqlite db = new DaoSqlite();
        db.setSetting(setting);
    }

    private void clipMonitorParse(String url) {
        if (Download.isUrlOK(url)) {
            Download dwn = new Download();
            dwn.setUrl(url);
            dwn.setName(Download.getUrl_name(url));
            dwn.setDirectory(setting.getDirectory());
            dwn.setConnections(setting.getConnectionCount());
            dwn.setUserAgent(setting.getUserAgent());
            if (setting.getMonitorMode() == 1) {
                OptionMenu newdwnmenu = new OptionMenu(this, dwn, true, true);
            } else if (setting.getMonitorMode() == 2) {
                addDownload(dwn, setting.isAutoStart());
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        model.fireTableRowsUpdated(0, model.getRowCount());
        toolbar.refreshToolBar();
        if (evt.getPropertyName().equals("addErrorMessage")) {
            statusPane.updateErrorView();
        } else if (evt.getPropertyName().equals("ClipboardUpdate") && setting.getMonitorMode() > 0
                && !evt.getNewValue().toString().equals(clipedUrl)) {
            clipedUrl = evt.getNewValue().toString();
            clipMonitorParse(evt.getNewValue().toString());

        }
    }

}
