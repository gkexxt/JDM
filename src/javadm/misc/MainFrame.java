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
package javadm.misc;

/**
 *Java Download manager GUI class
 * @author gk
 */
import javadm.misc.DataManager;
import javadm.gui.MenuBar;
import java.awt.*;
import javadm.gui.MenuBar;
import javadm.gui.StatusPane;
import javax.swing.*;
import javax.swing.event.*;

public class MainFrame extends JPanel
        implements ListSelectionListener {

    private JLabel dowloadTable = new JLabel();//will be a table later
    private DataManager dm = new DataManager();//data manager handle items data
    private JSplitPane splitPaneHortizontal;
    private JSplitPane splitPaneVertical;
    private JList list = new JList(); //hold downlad items data

    public MainFrame() {

        list.setModel(dm.getDownloadList());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);

        //Vertical panels - downloadpane+StatusPane
        StatusPane StatusPane = new StatusPane();
        dowloadTable.setFont(dowloadTable.getFont().deriveFont(Font.ITALIC));
        dowloadTable.setHorizontalAlignment(JLabel.CENTER);
        JScrollPane downloadPane = new JScrollPane(dowloadTable);
        splitPaneVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                downloadPane, StatusPane);
        splitPaneVertical.setBorder(null);//need for nesting panel - remove double border

        //HORIZONTAL_SPLIT panels - categorypane+splitPaneVertical(nested)
        JScrollPane categoryPane = new JScrollPane(list);
        splitPaneHortizontal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                categoryPane, splitPaneVertical);
        //splitPaneHortizontal.setOneTouchExpandable(true);

        //Provide minimum sizes for components in the split pane.
        Dimension minimumSize = new Dimension(100, 100);
        categoryPane.setMinimumSize(minimumSize);
        downloadPane.setMinimumSize(minimumSize);
        StatusPane.setMinimumSize(minimumSize);
        //Provide a preferred size for the split pane.
        splitPaneHortizontal.setPreferredSize(new Dimension(600, 400));

        //updateLabel(list.getModel().getElementAt(list.getSelectedIndex()).toString());
    }

    //Listens to the list
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        updateLabel(this.list.getModel()
                .getElementAt(list.getSelectedIndex()).toString());
        dm.addlist("tttttt");
    }

    /**Renders the selected data on TablePane label
    */
    private void updateLabel(String data) {
        System.out.println(data);
        dowloadTable.setText(data);

    }
    
    /**return MAinGUI nested panes to be add in JFrame
     * 
     * @return JSplitPane
     */
    public JSplitPane getMainPane() {
        return splitPaneHortizontal;
    }
    
   /** set split panes division position by ratio call after panel/frame after 
    * realized
    * 
    */
    public void setDiv() { //set panes divisor position
        splitPaneVertical.setDividerLocation(0.4);
        splitPaneHortizontal.setDividerLocation(0.3);

    }

    /**
     * Create the MainFrame and show it.
     */
    private static void createAndShowGUI() {
        //Create and set up the stage.
        JFrame frame = new JFrame("JAVA Download Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //menubar
        MenuBar demo = new MenuBar();
        frame.setJMenuBar(demo.createMenuBar());
        //make panels
        MainFrame mainUI = new MainFrame();
        //add to stage
        frame.getContentPane().add(mainUI.getMainPane());
        //Display stage.
        frame.pack();
        frame.setVisible(true);
        //add lisner for the selection list --ide compalining when doit in constructor why?
        mainUI.list.addListSelectionListener(mainUI);
        //set div for split panes -for artio 0-1 must be done after panes are realized?
        mainUI.setDiv();
        
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
                createAndShowGUI();
            });

        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            //Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.toString());
        }

    }

}
