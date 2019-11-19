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
import javadm.misc.DataManager;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javadm.app.App;
import javadm.com.Download;
import javadm.com.Setting;
import javadm.com.SettingDaoSqlite;
import javadm.data.Data;
import javadm.data.DataDaoSqlite;
import javax.swing.*;
import javax.swing.event.*;

public class DownloadManager extends JFrame
        implements ListSelectionListener, PropertyChangeListener {

    private DataManager dm = new DataManager();//data manager handle items data
    private JSplitPane splitPaneHortizontal;
    private JSplitPane splitPaneVertical;
    private JList list = new JList(); //hold download items data
    private DownloadTable table;
    private TableModel model;
    private JSplitPane mainsplitpane;
    private ToolBar toolbar;
    private StatusPane statusPane;
    private ClipboardTextListener clplstn;
    private Setting curSetting;

    public DownloadManager() {

    }

    //Listens to the list
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }

        dm.addlist("tttttt");
    }

    public void upateToolbar() {
        toolbar.refreshToolBar();
    }

    /**
     * Create the DownloadManager and show it.
     */
    public void createAndShowGUI() {

        list.setModel(dm.getDownloadList());
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

        JScrollPane downloadPane = new JScrollPane(table);
        splitPaneVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                downloadPane, statusPane);
        splitPaneVertical.setBorder(null);//need for nesting panel - remove double border
        splitPaneVertical.setOpaque(false);
        //HORIZONTAL_SPLIT panels - categorypane+splitPaneVertical(nested)
        JScrollPane categoryPane = new JScrollPane(list);
        splitPaneHortizontal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                categoryPane, splitPaneVertical);

        mainsplitpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, toolbar, splitPaneHortizontal);
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

        table.getSelectionModel().addListSelectionListener((ListSelectionEvent event) -> {
            toolbar.refreshToolBar();
            statusPane.updateErrorView();
        });

        //Create and set up the stage.
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Display stage.
        this.getContentPane().add(mainsplitpane);
        this.setLocationByPlatform(true);
        this.pack();
        this.setVisible(true);
        //add lisner for the selection list --ide compalining when doit in constructor why?
        this.list.addListSelectionListener(this);
        //set div for split panes -for ratio 0-1 must be done after panes are realized?        
        statusPane.setVisible(false);
        splitPaneVertical.setDividerLocation(0.6);
        splitPaneHortizontal.setDividerLocation(0.3);
        URL imageURL = getClass().getResource("/jdm24.png");
        ImageIcon img = new ImageIcon(imageURL);
        this.setIconImage(img.getImage());
        this.setTitle("JDM");
        checkDB();
        //SettingDaoSqlite dbx = new SettingDaoSqlite();
        //this.refreshTable();
        //ClipboardTextListener xxx = new ClipboardTextListener();
    }

    private void checkDB() {
        SettingDaoSqlite dbs = new SettingDaoSqlite();
        boolean settingok = dbs.isTableExists("setting");
        boolean dataok = dbs.isTableExists("downloaddata");
        if (!dataok) {
            if (showChoice("Rebuild data table?\nClicking no will exit the app",
                    "Data table error", JOptionPane.ERROR_MESSAGE) < 1) {
                newDataTable();
            } else {
                System.exit(1);
            }

        }

        if (!settingok) {
            if (showChoice("Rebuild data table?\nClicking no will exit the app",
                    "Data table error", JOptionPane.ERROR_MESSAGE) < 1) {
                newSettingTable();
            } else {
                System.exit(1);
            }
        }
        
        showInfo("Tables Rebuilded\nPlease restart the app", "JDM - Restart", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);

    }

    public void restartApplication() throws IOException, URISyntaxException {
        final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        final File currentJar = new File(App.class.getProtectionDomain().getCodeSource().getLocation().toURI());

        /* is it a jar file? */
        if (!currentJar.getName().endsWith(".jar")) {
            return;
        }

        /* Build command: java -jar application.jar */
        final ArrayList<String> command = new ArrayList<String>();
        command.add(javaBin);
        command.add("-jar");
        command.add(currentJar.getPath());

        final ProcessBuilder builder = new ProcessBuilder(command);
        builder.start();
        System.out.println("javadm.ui.DownloadManager.restartApplication()");
        System.exit(0);
    }

    private void newSettingTable() {
        System.out.println("javadm.ui.DownloadManager.newSettingTable()");
    }

    private void newDataTable() {
        System.out.println("javadm.ui.DownloadManager.newDataTable()");
    }

    public void loadall() {
        DataDaoSqlite db = new DataDaoSqlite();
        java.util.List<Data> datas = new ArrayList<>();
        datas.addAll(db.getAllDownloadData());

        for (int i = 0; i < datas.size(); i++) {
            Data data = datas.get(i);
            Download download = new Download();
            download.setData(data);
            download.setProgress(download.getData().getDoneSize());
            model.addRow(download);
            download.addPropertyChangeListener(model);
            download.addPropertyChangeListener(this);
        }

        SettingDaoSqlite dbs = new SettingDaoSqlite();

        curSetting = dbs.getSetting();

        if (curSetting.getMonitorMode() > 0) {
            startClipListner();
        }

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
                getSelectedDownload();
            }
        };
        t.scheduleAtFixedRate(tt, 0, 100);
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
     */
    public void removeDownload() {
        if (showChoice("Remove this download?", "Removing", JOptionPane.QUESTION_MESSAGE) > 0) {
            return;
        }
        Download rmDownload = getSelectedDownload();
        model.removeTableModelListener(table);//remove table listner before delete from model
        model.removeRows(table.getSelectedRow());
        model.addTableModelListener(table);//add it back
        DataDaoSqlite db = new DataDaoSqlite();
        db.deleteDownloadData(rmDownload.getData().getId());
        toolbar.refreshToolBar();
        statusPane.setVisible(false);
        model.fireTableDataChanged();
    }

    /**
     * start selected download
     */
    public void startDownload() {
        getSelectedDownload().setStart(true);
    }

    /**
     * stop selected download
     */
    public void stopDownload() {
        getSelectedDownload().setStart(false);
    }

    /**
     * remove data and reboot the download
     */
    public void restartDownload() {
        if (showChoice("Are you sure !!! \n"
                + "This Download is complete this will destroy all data and start again",
                "Redownload", JOptionPane.QUESTION_MESSAGE) > 0) {
            return;
        }
        getSelectedDownload().getData().setComplete(false);
        getSelectedDownload().getData().setDoneSize(0);
        getSelectedDownload().setStart(true);
    }

    /**
     * add new download to the list/table
     *
     * @param download
     */
    public void addDownload(Download download) {
        try {
            Download tempd = download;
            DataDaoSqlite db = new DataDaoSqlite();
            db.insertDownloadData(tempd.getData());
            tempd.setData(db.getLastDownloadData());
            model.addRow(tempd);
            tempd.addPropertyChangeListener(this);
        } catch (Exception e) {
        }
    }

    /**
     * update existing download
     *
     * @param download
     */
    public void updateDownload(Download download) {
        model.getRow(table.getSelectedRow()).setData(download.getData());
        DataDaoSqlite db = new DataDaoSqlite();
        db.updateDownloadData(download.getData());
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
                frame.createAndShowGUI();
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
        return curSetting;

    }

    public String chooseFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Save to");

        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setFileHidingEnabled(true);
        //
        // disable the "All files" option.
        //
        chooser.setAcceptAllFileFilterUsed(false);
        //    
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {

            return (chooser.getSelectedFile().toString());
            //System.out.println("getSelectedFile() : "
            //  + chooser.getSelectedFile());
        }
        return "";
    }

    public void setSetting(Setting setting) {
        this.curSetting = setting;
        SettingDaoSqlite db = new SettingDaoSqlite();
        db.setSetting(setting);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        model.fireTableRowsUpdated(0, model.getRowCount());
        toolbar.refreshToolBar();
        if (evt.getPropertyName().equals("addErrorMessage")) {
            statusPane.updateErrorView();
        } else if (evt.getPropertyName().equals("ClipboardUpdate")) {
            System.err.println(evt.getNewValue().toString());
        }
    }

}
