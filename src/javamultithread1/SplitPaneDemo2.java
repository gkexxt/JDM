/*
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

public class SplitPaneDemo2 extends JFrame
        implements ListSelectionListener {

    private JLabel label;
    private DataManager Manager;
    private JList list;

    public SplitPaneDemo2() {
        super("SplitPaneDemo2");

        //Create an instance of SplitPaneDemo
        Manager = new DataManager();
        list = Manager.getDownloadList();
        SplitPaneDemo splitPaneDemo = new SplitPaneDemo(Manager);
        JSplitPane top = splitPaneDemo.getSplitPane();
        list.addListSelectionListener(this);

        //XXXX: Bug #4131528, borders on nested split panes accumulate.
        //Workaround: Set the border on any split pane within
        //another split pane to null. Components within nested split
        //panes need to have their own border for this to work well.
        top.setBorder(null);

        //Create a regular old label
        label = new JLabel("Click on an image name in the list.",
                JLabel.CENTER);
        TabbedPaneDemo tabbedpane = new TabbedPaneDemo();
        //Create a split pane and put "top" (a split pane)
        //and JLabel instance in it.
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                top, tabbedpane);
        //splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(180);

        //Provide minimum sizes for the two components in the split pane
        top.setMinimumSize(new Dimension(100, 50));
        label.setMinimumSize(new Dimension(100, 30));

        //Add the split pane to this frame
        getContentPane().add(splitPane);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {

            //System.out.println(e.toString());
            return;
        }

        if (list.isSelectionEmpty()) {
            label.setText("Nothing selected.");
        } else {
            //int index = theList.getSelectedIndex();
            label.setText("Selected image number ");//+ index);
        }
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.

        MenuBar demo = new MenuBar();
        JFrame frame = new SplitPaneDemo2();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setJMenuBar(demo.createMenuBar());
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        try {

            //get os and use proper laf
            String osname = System.getProperty("os.name").toLowerCase();
            if (osname.contains("linux")) {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            } else if (System.getProperty("os.name").contains("window")) {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } else {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }

        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(SplitPaneDemo2.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.toString());
        }
        //run ui/app
        javax.swing.SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }
}
