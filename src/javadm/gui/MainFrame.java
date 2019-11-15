/*v
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadm.gui;

/**
 * Java Download manager GUI class
 *
 * @author gk
 */
import javadm.misc.DataManager;
import java.awt.*;
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
        implements ListSelectionListener, PropertyChangeListener{

    //private JLabel dowloadTable = new JLabel();//will be a table later
    private final DataManager dm = new DataManager();//data manager handle items data
    private final JSplitPane splitPaneHortizontal;
    private final JSplitPane splitPaneVertical;
    private final JList list = new JList(); //hold downlad items data
    private final DownloadTable table;
    private static TableModel model;
    private final JSplitPane mainsplitpane;
    private Download selectedDownload ;
    public MainFrame() {
        
        
        list.setModel(dm.getDownloadList());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);

        model = new TableModel();
        //model.add
        DataDaoSqlite db = new DataDaoSqlite();
        java.util.List<Data> datas = new ArrayList<>();
        datas.addAll(db.getAllDownloadData());
        //List<Download> downloads = new ArrayList<>();

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
        ToolBar toolbar = new ToolBar();
        table = new DownloadTable(model);
        //table.setLayout(new BorderLayout());
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getSelectionModel().addListSelectionListener((ListSelectionEvent event) -> {
            // do some actions here, for example
            // print first column value from selected row
            toolbar.setCurrentDownload(model.getRow(table.getSelectedRow()));
            System.err.println(selectedDownload.toString());
            //System.out.println(table.getValueAt(table.getSelectedRow(), 0).toString());
        });
        System.err.println(selectedDownload.toString());
        
        //frame.getContentPane().
        //Vertical panels - downloadpane+StatusPane
        StatusPane StatusPane = new StatusPane();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSelectionInterval(0, 0);
        //table.setOpaque(false);
        //table.setBackground(Color.WHITE);
        ((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setBackground(Color.white);
        //dowloadTable.setFont(dowloadTable.getFont().deriveFont(Font.ITALIC));
        //dowloadTable.setHorizontalAlignment(JLabel.CENTER);
        JScrollPane downloadPane = new JScrollPane(table);
        splitPaneVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                downloadPane, StatusPane);
        splitPaneVertical.setBorder(null);//need for nesting panel - remove double border

        //HORIZONTAL_SPLIT panels - categorypane+splitPaneVertical(nested)
        JScrollPane categoryPane = new JScrollPane(list);
        splitPaneHortizontal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                categoryPane, splitPaneVertical);
        //splitPaneHortizontal.setOneTouchExpandable(true);
        
         mainsplitpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,toolbar,splitPaneHortizontal);
         mainsplitpane.setEnabled(false);
         mainsplitpane.setDividerSize(1);
         mainsplitpane.setBackground(Color.WHITE);
        //Provide minimum sizes for components in the split pane.
        Dimension minimumSize = new Dimension(100, 100);
        categoryPane.setMinimumSize(minimumSize);
        downloadPane.setPreferredSize(new Dimension(300, 500));
        downloadPane.setBackground(Color.white);
        downloadPane.setForeground(Color.white);
        //downloadPane.setSize(new Dimension(100, 800));
        StatusPane.setMinimumSize(minimumSize);
        //Provide a preferred size for the split pane.
        splitPaneHortizontal.setPreferredSize(new Dimension(600, 400));
        splitPaneHortizontal.setDividerSize(6);
        splitPaneVertical.setDividerSize(6);
        splitPaneVertical.setDividerLocation(400);
        splitPaneVertical.setBackground(Color.white);
        splitPaneHortizontal.setBackground(Color.red);
        StatusPane.setBackground(Color.WHITE);
        //splitPaneVertical.setd
        //createAndShowGUI();
        //updateLabel(list.getModel().getElementAt(list.getSelectedIndex()).toString());
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
     * Renders the selected data on TablePane label
     */
    /**
     * return MAinGUI nested panes to be add in JFrame
     *
     * @return JSplitPane
     */
    public JSplitPane getMainPane() {
        return mainsplitpane;
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
        //JFrame frame = new JFrame("JAVA Download Manager");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //menubar
        //MenuBar demo = new MenuBar();
        //frame.setJMenuBar(demo.createMenuBar());
        //frame.getContentPane().add(new ToolBar());
        //make panels
        //MainFrame mainUI = new MainFrame();
        //add to stage
        this.getContentPane().add(this.getMainPane());
        //Display stage.
//frame.setBackground(Color.yellow);
        this.pack();
        this.setVisible(true);
        //add lisner for the selection list --ide compalining when doit in constructor why?
        this.list.addListSelectionListener(this);
        //set div for split panes -for artio 0-1 must be done after panes are realized?
        this.setDiv();

    }

    public void refreshTable() {
        java.util.Timer t = new java.util.Timer();
        TimerTask tt;
        tt = new TimerTask() {
            @Override
            public void run() {
                model.fireTableRowsUpdated(0, model.getRowCount() - 1);
            }
        };
        t.scheduleAtFixedRate(tt, 0, 500);
    }

    public static void main(String[] args) {
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
                //createAndShowGUI();

            });

        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            //Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.toString());
        }

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    model.fireTableRowsUpdated(0, model.getRowCount() - 1);
        System.out.println("javadm.gui.MainFrame.propertyChange()");
    }

}
