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
 * Java Download manager GUI class
 *
 * @author gk
 */
import javadm.misc.DataManager;
import java.awt.*;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.TimerTask;
import javadm.com.Download;
import javadm.data.Data;
import javadm.data.DataDaoSqlite;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;

public class MainFrame extends JFrame
        implements ListSelectionListener, PropertyChangeListener {

    //private JLabel dowloadTable = new JLabel();//will be a table later
    private final DataManager dm = new DataManager();//data manager handle items data
    private final JSplitPane splitPaneHortizontal;
    private final JSplitPane splitPaneVertical;
    private final JList list = new JList(); //hold downlad items data
    private final DownloadTable table;
    private static TableModel model;
    private final JSplitPane mainsplitpane;
    private Download selectedDownload;
    private ToolBar toolbar;
    private StatusPane statusPane;

    public MainFrame() {

        list.setModel(dm.getDownloadList());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);

        model = new TableModel();
        DataDaoSqlite db = new DataDaoSqlite();
        java.util.List<Data> datas = new ArrayList<>();
        datas.addAll(db.getAllDownloadData());

        for (int i = 0; i < datas.size(); i++) {
            Data data = datas.get(i);
            Download download = new Download();
            download.setData(data);
            download.setDownloadControl(new DownloadControl());
            download.setProgress(download.getData().getDoneSize());
            model.addRow(download);
            download.addPropertyChangeListener(model);
        }

        //default selected download from download list
        selectedDownload = model.getRow(0);

        table = new DownloadTable(model);
        //table.setLayout(new BorderLayout());
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getSelectionModel().addListSelectionListener((ListSelectionEvent event) -> {
            // do some actions here, for example
            // print first column value from selected row
            toolbar.refreshToolBar();
            System.err.println(selectedDownload.toString());
            //System.out.println(table.getValueAt(table.getSelectedRow(), 0).toString());
        });
        System.err.println(selectedDownload.toString());
        toolbar = new ToolBar(this, model, table);
        //frame.getContentPane().
        //Vertical panels - downloadpane+StatusPane
        statusPane = new StatusPane();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSelectionInterval(0, 0);
        //table.setOpaque(false);
        //table.setBackground(Color.WHITE);
        ((DefaultTableCellRenderer) table.getDefaultRenderer(Object.class)).setBackground(Color.white);
        JScrollPane downloadPane = new JScrollPane(table);
        splitPaneVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                downloadPane, statusPane);
        splitPaneVertical.setBorder(null);//need for nesting panel - remove double border

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
        //Provide a preferred size for the split pane.
        splitPaneHortizontal.setPreferredSize(new Dimension(600, 400));
        splitPaneHortizontal.setDividerSize(6);
        splitPaneVertical.setDividerSize(6);
        splitPaneVertical.setDividerLocation(400);
        splitPaneVertical.setBackground(Color.white);
        splitPaneHortizontal.setBackground(Color.red);
        statusPane.setBackground(Color.WHITE);
    }

    //Listens to the list
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }

        dm.addlist("tttttt");
    }

    /**
     * set split panes division position by ratio call after panel/frame after
     * realized
     *
     */
    public void setDiv() { //set panes divisor position
        splitPaneVertical.setDividerLocation(0.6);
        splitPaneHortizontal.setDividerLocation(0.3);

    }

    /**
     * Create the MainFrame and show it.
     */
    public void createAndShowGUI() {
        //Create and set up the stage.
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.getContentPane().add(mainsplitpane);
        //Display stage.
        this.setLocationByPlatform(true);
        this.pack();
        this.setVisible(true);
        //add lisner for the selection list --ide compalining when doit in constructor why?
        this.list.addListSelectionListener(this);
        //set div for split panes -for artio 0-1 must be done after panes are realized?
        this.setDiv();
        statusPane.setVisible(false);

    }

    public void refreshTable() {
        java.util.Timer t = new java.util.Timer();
        TimerTask tt;
        tt = new TimerTask() {
            @Override
            public void run() {
                model.fireTableRowsUpdated(0, model.getRowCount() - 1);
                toolbar.refreshToolBar();
            }
        };
        t.scheduleAtFixedRate(tt, 0, 100);
    }

    /**
     *
     * @param message
     * @param title
     * @return
     */
    public int showOptionPane(String message, String title) {
        int result = JOptionPane.showConfirmDialog(this, message,
                title, JOptionPane.OK_CANCEL_OPTION);
        return result;
    }

    public void removeDownload() {
        if (showOptionPane("Remove this download?", "Removing") > 0) {
            return;
        }
        try {
            DataDaoSqlite db = new DataDaoSqlite();
            //currentDownload = getselectedDownload();
            for (int i = 0; i < model.getRowCount(); i++) {
                System.err.println(i);
                if (model.getRow(i).getData().getId() == selectedDownload.getData().getId()) {
                    model.removeTableModelListener(table);//remove table listner before delete from model
                    model.removeRows(i);
                    model.addTableModelListener(table);//add it back
                    model.fireTableRowsUpdated(0, model.getColumnCount() - 1);
                    db.deleteDownloadData(selectedDownload.getData().getId());
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "JavaDM: "
                    + "error remove download", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
     

    public static void main(String[] args) {
        try {
            //listen to clipboard

            Toolkit.getDefaultToolkit().getSystemClipboard().addFlavorListener((FlavorEvent e) -> {
                System.out.println("ClipBoard UPDATED: " + e.getSource() + " " + e.toString());
            });
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
                MainFrame frame = new MainFrame();
                frame.createAndShowGUI();
                frame.refreshTable();

            });

        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            //Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.toString());
        }

    }



    public void showDetails() {
        statusPane.setVisible(!statusPane.isVisible());
        if (statusPane.isVisible()) {
            setDiv();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        model.fireTableRowsUpdated(0, model.getRowCount() - 1);
        System.out.println("javadm.gui.MainFrame.propertyChange()");
    }

}
