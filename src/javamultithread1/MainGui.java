/*v
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javamultithread1;

/**
 *
 * @author gk
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

//SplitPaneDemo itself is not a visible component.
public class MainGui extends JPanel
        implements ListSelectionListener {

    private JLabel dowloadTable;
    //private JList list;
    private DataManager Manager = new DataManager();
    private JSplitPane splitPaneHortizontal;
    private JSplitPane splitPaneVertical;
    private JList list;

    public MainGui(DataManager Manager) {

        //
        this.Manager = Manager;
        this.list = Manager.getDownloadList();
        list.addListSelectionListener(this);

        //Vertical panels - downloadpane+StatusPane
        TabbedPaneDemo StatusPane = new TabbedPaneDemo();
        dowloadTable = new JLabel();
        dowloadTable.setFont(dowloadTable.getFont().deriveFont(Font.ITALIC));
        dowloadTable.setHorizontalAlignment(JLabel.CENTER);

        JScrollPane downloadPane = new JScrollPane(dowloadTable);
        splitPaneVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                downloadPane, StatusPane);

        //need for nesting panel - remove double border
        splitPaneVertical.setBorder(null);

        JScrollPane categoryPane = new JScrollPane(list);

        //Create a split pane with the two scroll panes in it.
        splitPaneHortizontal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                categoryPane, splitPaneVertical);
        splitPaneHortizontal.setOneTouchExpandable(true);

        //Provide minimum sizes for the two components in the split pane.
        Dimension minimumSize = new Dimension(100, 100);
        categoryPane.setMinimumSize(minimumSize);
        downloadPane.setMinimumSize(minimumSize);
        StatusPane.setMinimumSize(minimumSize);
        //Provide a preferred size for the split pane.
        splitPaneHortizontal.setPreferredSize(new Dimension(600, 400));

        updateLabel(list.getModel().getElementAt(list.getSelectedIndex()).toString());

    }

    //Listens to the list
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        updateLabel(list.getModel().getElementAt(list.getSelectedIndex()).toString());
        Manager.addlist("tttttt");
        list = Manager.getDownloadList();
    }

    //Renders the selected image
    private void updateLabel(String name) {

        //System.out.println(name);
        dowloadTable.setText(name);

    }

    public JSplitPane getMainPane() {
        return splitPaneHortizontal;
    }

    public void setDiv() {
        //System.out.println(splitPaneHortizontal.getHeight()*0.7);
        splitPaneVertical.setDividerLocation(0.4);
        splitPaneHortizontal.setDividerLocation(0.3);

    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     * @param path
     * @return 
     */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = MainGui.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event-dispatching thread.
     */
    private static void createAndShowGUI() {

        //Create and set up the window.
        JFrame frame = new JFrame("SplitPaneDemo");
        MenuBar demo = new MenuBar();
        frame.setJMenuBar(demo.createMenuBar());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        DataManager Manager = new DataManager();
        //JList list = listx.getDownloadList();
        MainGui splitPaneDemo = new MainGui(Manager);
        frame.getContentPane().add(splitPaneDemo.getMainPane());

        //Display the window.
        frame.pack();
        frame.setVisible(true);

        splitPaneDemo.setDiv();
        //frame.repaint();
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
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            } else if (System.getProperty("os.name").contains("window")) {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } else {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }

            //run ui/app
            javax.swing.SwingUtilities.invokeLater(() -> {
                createAndShowGUI();
            });

        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            //Logger.getLogger(MainGui.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.toString());
        }

    }

}
