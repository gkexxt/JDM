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
import javadm.com.Data;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class StatusPane extends JPanel {

    private final DownloadManager dm;
    private final JTextArea errorView = new JTextArea();
    private JLabel progressView = new JLabel();
    private Download currentSelected = new Download();
    private String downloadInstance = "";

    public StatusPane(DownloadManager downloadManager) {

        super(new GridLayout(1, 1));
        this.dm = downloadManager;
        currentSelected.setData(new Data());
        errorView.setForeground(Color.RED);
        errorView.setEditable(false);;

        //create tabbedpane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Info", new JScrollPane(errorView));
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        tabbedPane.addTab("Progress", new JScrollPane(progressView));
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        //Add the tabbed pane to this panel.
        this.add(tabbedPane);

        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

    }

    public void updateErrorView() {
        if (dm.getSelectedDownload() == null) {
            this.setVisible(false);
        } else if (!dm.getSelectedDownload().toString().equals(downloadInstance)) {
            currentSelected = dm.getSelectedDownload();
            downloadInstance = currentSelected.toString();//
            errorView.setText("Error Log : " + currentSelected.getData().getName() + "\n\n");
            if (currentSelected.getErrorLog().size() > 0) {
                for (int i = 0; i < currentSelected.getErrorLog().size() - 1; i++) {
                    errorView.setText(errorView.getText() + i + " : " + currentSelected.getErrorLog().get(i)[0] + "\n"
                            + currentSelected.getErrorLog().get(i)[1] + "\n");
                }
            }
        } else {
            List<String[]> errLog = dm.getSelectedDownload().getErrorLog();
            int lastLine = errLog.size() - 1;
            if (lastLine > -1) {
                errorView.setText(errorView.getText() + lastLine + " : " + errLog.get(lastLine)[0] + "\n"
                        + errLog.get(lastLine)[1] + "\n");
            }
        }
    }

}
