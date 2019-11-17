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
 * Tabbed status pane for JavaDM
 *
 * @author gk
 */
import javax.swing.JTabbedPane;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;

public class StatusPane extends JPanel {

    private final MainFrame mainframe;
    private JLabel errorView = new JLabel("xxxxxxx");

    public StatusPane(MainFrame mainframe) {
        super(new GridLayout(1, 1));
        this.mainframe = mainframe;
        JTabbedPane tabbedPane = new JTabbedPane();
        ImageIcon icon = createImageIcon("images/middle.gif");

        JComponent panel1 = makeTextPanel("Panel #1", errorView);
        tabbedPane.addTab("Tab 1", icon, panel1,
                "Does nothing");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        JComponent panel2 = makeTextPanel("Panel #2", new JLabel());
        tabbedPane.addTab("Tab 2", icon, panel2,
                "Does twice as much nothing");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        JComponent panel3 = makeTextPanel("Panel #3", new JLabel());
        tabbedPane.addTab("Tab 3", icon, panel3,
                "Still does nothing");
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

        JComponent panel4 = makeTextPanel(
                "Panel #4 (has a preferred size of 410 x 50).", new JLabel());
        panel4.setPreferredSize(new Dimension(410, 50));
        tabbedPane.addTab("Tab 4", icon, panel4,
                "Does nothing at all");
        tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);

        //Add the tabbed pane to this panel.
        add(tabbedPane);

        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

    }

    public void updateErrorView() {
        int i = 0;
        try {
            for ( i = 0; i < mainframe.getSelectedDownload().getErrorLog().size()-2; i++) {
                errorView.setText(errorView.getText()+mainframe.getSelectedDownload().getErrorLog().get(i)[0] + "\n"
                        + mainframe.getSelectedDownload().getErrorLog().get(i)[1] + "\n");
            }
        } catch (Exception ex) {
            System.err.println(errorView.getText()+"\n"+ex.toString() + "/////////  " +i+ mainframe.getSelectedDownload().getErrorLog().size()); 
        }

    }
    
    

    /**
     *
     * @param text
     * @param canvas
     * @return
     */
    protected JComponent makeTextPanel(String text, JLabel canvas) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(canvas);
        return panel;
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     *
     * @param path
     * @return
     */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = StatusPane.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

}
