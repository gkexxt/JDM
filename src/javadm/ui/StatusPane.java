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
import java.awt.Component;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import javadm.com.Download;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class StatusPane extends JPanel {

    private final DownloadManager dm;
    private final JTable errorView = new JTable();
    private JLabel progressView = new JLabel();
    private String downloadInstance = "";
    private final DefaultTableModel errmodel;

    private int last_line = 0;

    public StatusPane(DownloadManager downloadManager) {

        super(new GridLayout(1, 1));
        this.dm = downloadManager;

        //currentSelected = new Download();
        //errorView.setForeground(Color.RED);
        //errorView.setEditable(false);;
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

        errmodel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        errorView.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        errmodel.addColumn("Type");
        errmodel.addColumn("Message");
        errorView.setModel(errmodel);
        errorView.setVisible(true);
        typeRenderor typernderer = new typeRenderor();
        msgRenderer msgrenderer = new msgRenderer();
        errorView.getColumn("Type").setCellRenderer(typernderer);
        errorView.getColumn("Message").setCellRenderer(msgrenderer);
        errorView.getColumn("Type").setPreferredWidth(90);
        errorView.getColumn("Message").setPreferredWidth(1000);

    }

    private class typeRenderor extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            String rst = (String) value;
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setForeground(Color.BLACK);
            switch (rst) {
                case Download.ERROR:
                    c.setBackground(Color.red);
                    return c;
                case Download.DEBUG:
                    c.setBackground(Color.BLUE);
                    return c;
                case Download.INFO:
                    c.setBackground(Color.GREEN);
                    return c;
                case Download.WARNING:
                    c.setBackground(Color.orange);
                    return c;
                default:
                    c.setBackground(Color.white);
                    return c;
            }

        }
    }

    private class msgRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setBackground(Color.lightGray);
            return c;

        }
    }

    public void updateErrorView() {

        //List <String[]> error_log = dm.getSelectedDownload().getLogMsgs();
        if (dm.getSelectedDownload() == null) {
            this.setVisible(false);
            return;
        }

        if (dm.getSelectedDownload().getLogMsgs().size() < 1) {
            downloadInstance = dm.getSelectedDownload().toString();
            errmodel.setRowCount(0);
            return;
        }

        if (!dm.getSelectedDownload().toString().equals(downloadInstance)) {
            System.out.println("change");
            errmodel.setRowCount(0);
            downloadInstance = dm.getSelectedDownload().toString();
            for (String[] msg : dm.getSelectedDownload().getLogMsgs()) {
                errmodel.addRow(msg);
            }
            last_line = dm.getSelectedDownload().getLogMsgs().size();

        } else if (dm.getSelectedDownload().toString().equals(downloadInstance) && dm.getSelectedDownload().getLogMsgs().size() != last_line) {

            last_line = dm.getSelectedDownload().getLogMsgs().size();
            errmodel.addRow(dm.getSelectedDownload().getLogMsgs().get(last_line - 1));

        }
//
    }

}
