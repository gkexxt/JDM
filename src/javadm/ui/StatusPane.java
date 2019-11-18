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
import java.awt.Color;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.util.List;
import javadm.com.Download;
import javadm.data.Data;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class StatusPane extends JPanel {

    private final MainFrame mainframe;
    private final JTextArea errorView = new JTextArea();
    private JLabel progressView = new JLabel();
    private int line = 0;
    private Download currentSelected = new Download();

    public StatusPane(MainFrame mainframe) {
        super(new GridLayout(1, 1));
        this.mainframe = mainframe;
        currentSelected.setData(new Data());

        JTabbedPane tabbedPane = new JTabbedPane();
        JScrollPane scroll = new JScrollPane(errorView);
        tabbedPane.addTab("Info", scroll);
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        errorView.setForeground(Color.RED);

        JScrollPane scroll2 = new JScrollPane(progressView);
        tabbedPane.addTab("Progress", scroll2);
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        //Add the tabbed pane to this panel.
        add(tabbedPane);

        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

    }

    public void updateErrorView() {
        line++;
        int i = 0;
        try {

            if (mainframe.getSelectedDownload() == null) {
                return;
            } else if (mainframe.getSelectedDownload().getData().getId() != currentSelected.getData().getId()) {
                currentSelected = mainframe.getSelectedDownload();
                errorView.setText("Error Log : " + currentSelected.getData().getName() + "\n\n");
                line = 0;
            }

            currentSelected = mainframe.getSelectedDownload();//          
            List<String[]> errolog = mainframe.getSelectedDownload().getErrorLog();
            errorView.setText(errorView.getText() + line + " : " + errolog.get(errolog.size() - 1)[0] + "\n"
                    + errolog.get(errolog.size() - 1)[1] + "\n");
        } catch (Exception ex) {
            // System.err.println(errorView.getText() + "\n" + ex.toString() + "/////////  " + i + mainframe.getSelectedDownload().getErrorLog().size());
        }

    }

}
