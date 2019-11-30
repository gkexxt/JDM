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
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javadm.com.Download;
import javadm.com.Part;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;

public class StatusPane extends JPanel implements TableModelListener, PropertyChangeListener {

    private final JTable errorView = new JTable();
    private JProgressBar mainbar = new JProgressBar();
    private JPanel progressView = new JPanel();
    private Download sDownload;
    ArrayList<JProgressBar> plist = new ArrayList<>();
    JScrollPane logpane;
    typeRenderor typernderer = new typeRenderor();
    msgRenderer msgrenderer = new msgRenderer();

    public StatusPane() {
        super(new GridLayout(1, 1));
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Info", logpane = new JScrollPane(errorView));

        progressView.setLayout(new BoxLayout(progressView, BoxLayout.PAGE_AXIS));

        tabbedPane.addTab("Progress", new JScrollPane(progressView));
        //Add the tabbed pane to this panel.
        this.add(tabbedPane);

        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        errorView.setVisible(true);

    }

    public void setsDownload(Download download) {
        if (sDownload != null) {
            this.sDownload.removePropertyChangeListener(this);
        }
        if (download != null) {
            this.sDownload = download;
            this.sDownload.addPropertyChangeListener(this);
            updateErrorView(this.sDownload);
            setupProgressView(this.sDownload);
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        SwingUtilities.invokeLater(() -> {
            logpane.getVerticalScrollBar().setValue(logpane.getVerticalScrollBar().getMaximum());
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(() -> {
            updateProgressView();
        });

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

    public void updateErrorView(Download download) {
        errorView.getModel().removeTableModelListener(this);
        errorView.setModel(download.getLogmodel());
        errorView.getModel().addTableModelListener(this);
        errorView.getColumn("Type").setCellRenderer(typernderer);
        errorView.getColumn("Message").setCellRenderer(msgrenderer);
        errorView.getColumn("Type").setPreferredWidth(90);
        errorView.getColumn("Message").setPreferredWidth(1000);

    }

    public void updateProgressView() {
        if (sDownload.getParts().size() != plist.size()) {
            setupProgressView(sDownload);
        }

        var xxx = ((double) sDownload.getDoneSize() / sDownload.getFileSize()) * 100.00;
        mainbar.setValue((int) xxx);
        mainbar.setString((int) xxx + "%");

        if (plist.size() > 0) {
            for (int i = 0; i < sDownload.getParts().size(); i++) {
                Part part = sDownload.getParts().get(i);
                xxx = ((double) part.getCurrentSize() / part.getSize()) * 100.00;
                plist.get(i).setValue((int) xxx);
                plist.get(i).setString((int) xxx + "%");
            }
        }

    }

    public void setupProgressView(Download download) {
        
        if (download == null) {
            this.setVisible(false);
            progressView.removeAll();
            plist.clear();
            return;
        }

        progressView.removeAll();
        plist.clear();

        progressView.add(Box.createRigidArea(new Dimension(0, 10)));
        progressView.add(new JLabel("Overall Progress"));
        //Font fx = new Font("Arial",14,Font.PLAIN) ;
        mainbar.setFont(new Font(mainbar.getFont().getName(), Font.PLAIN, 17));
        mainbar.setStringPainted(true);
        var xxx = ((double) sDownload.getDoneSize() / sDownload.getFileSize()) * 100.00;
        mainbar.setString((int) xxx + "%");
        mainbar.setValue((int) xxx);
        mainbar.setBorder(new LineBorder(Color.GREEN, 1));
        progressView.add(mainbar);
        progressView.add(Box.createRigidArea(new Dimension(0, 12)));

        System.out.println("javadm.ui.StatusPane.setupProgressView()");

        for (int i = 0; i < download.getParts().size(); i++) {
            JProgressBar pprogress = new JProgressBar();

            xxx = ((double) download.getParts().get(i).getCurrentSize() / download.getParts().get(i).getSize()) * 100.00;
            progressView.add(new JLabel("Part : " + (i + 1)));
            pprogress.setString((int) xxx + "%");
            pprogress.setValue((int) xxx);
            pprogress.setStringPainted(true);
            plist.add(pprogress);
            progressView.add(pprogress);
        }

        this.repaint();

    }

}
