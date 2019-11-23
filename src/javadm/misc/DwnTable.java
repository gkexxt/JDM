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
 *
 * @author G.K #gkexxt@outlook.com
 */
import javadm.misc.RowTableModel;
import javadm.com.Download;
import java.util.*;
import java.awt.BorderLayout;
import java.awt.Component;
import javadm.com.Download;
import javadm.com.DaoSqlite;
import javadm.ui.DownloadControl;
import javax.swing.*;
import javax.swing.JProgressBar;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class DwnTable extends RowTableModel<Download> {
//private static int  xxx = 0;

    private static String[] COLUMN_NAMES
            = {
                "ID",
                "Name",
                "Pass",
                "Age",
                "Status",
                "control",
                "Progress",
                "filesize",
                "donesize",
                "rowlocked"

            };
    private Integer disablerow = null;

    DwnTable() {
        super(Arrays.asList(COLUMN_NAMES));
        setRowClass(Download.class);

        setColumnClass(5, Boolean.class);
        //setColumnClass(2, JLabel.class);
        setColumnClass(6, JProgressBar.class);
        setColumnClass(7, String.class);
        setColumnClass(8, String.class);
        setColumnClass(9, Boolean.class);
        //setColumnEditable(0, false);
        setColumnEditable(6, false);
        //setColumnClass(6, JProgressBar.class);

    }

    public void setRenderer(JTable table) {
        table.getColumn("control").setCellRenderer(new LabelRenderer());
        table.getColumn("Progress").setCellRenderer(new ProgresRenderer());
    }

    @Override
    public Object getValueAt(int row, int column) {
        Download downnload = getRow(row);

        switch (column) {
            case 0:
                return downnload.getId();
            case 1:
                return downnload.getName();
            case 2:
                return downnload.getCreatedDate();
            case 3:
                return downnload.getDirectory();
            case 4:
                return downnload.getUrl();
            case 5:
                return downnload.getDownloadControl().getLblControl();
            case 6:
                return downnload.getDownloadControl().getProgressbar();
            case 7:
                return downnload.getFileSize();
            case 8:
                return downnload.getDoneSize();
            case 9:
                return downnload.getDownloadControl().isRowlocked();
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (column == 0 || column == 4 || column == 6) {
            return false;
        }

        if (column == 5 || column == 9) {
            return true;
        }

        if (this.modelData.get(row).getDownloadControl().isRowlocked()) {
            return false;
        }

        return true;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        Download download = getRow(row);
        try {
            switch (column) {
                case 0:

                    break;
                case 5:
                    download.setStart(!download.isStart());
                    //download.getDownloadtableui().setLblControl(download.isStart());
                    break;
                case 7:

                    download.setFileSize(Long.parseLong(value.toString()));
                    break;
                case 8:
                    download.setDoneSize(Long.parseLong(value.toString()));
                case 9:
                    download.getDownloadControl().setRowlocked(Boolean.parseBoolean(value.toString()));
                    break;
                //break;
                default:
                    break;
            }
            fireTableCellUpdated(row, column);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage() + "\n" + e.toString(), "InfoBox: "
                    + "error update db", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    private class LabelRenderer implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            return DwnTable.this.modelData.get(row).getDownloadControl().getLblControl();
            //System.err.println(table.getModel().getValueAt(row, column).toString());

        }
    }

    private class ProgresRenderer implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            //JProgressBar progress = (JProgressBar) table.getModel().getValueAt(row, column);
            //table.get
            //String upper = table.getModel().getValueAt(row, 8)+"";
            //String lower = table.getModel().getValueAt(row, 7)+"";
            //System.out.println("JavaDM.Download.DwnTable.ProgresRenderer.getTableCellRendererComponent()")
            //System.out.println(DwnTable.this.modelData.get(row).getData().getId());

            return DwnTable.this.modelData.get(row).getDownloadControl().getProgressbar();

            //return progress;
        }
    }

    public static void main(String[] args) {
        JButton one = new JButton("One");
        JButton two = new JButton("Two");
        JButton three = new JButton("Three");

        //  Use the custom model
        DwnTable model = new DwnTable();
        //table.setModel(model);

        //  Use the BeanTableModel  
//		BeanTableModel<JButton> model =
//			new BeanTableModel<JButton>(JButton.class, java.awt.Component.class);
        DaoSqlite db = new DaoSqlite();

        //System.out.println();
        List<Download> downloads = db.getAllDownload();

        for (Download download : downloads) {
            int value = (int) (double) ((100.0 * download.getDoneSize())
                    / download.getFileSize());
            download.getDownloadControl().setProgressBar(value);
            model.addRow(download);

        }

        JTable table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollPane = new JScrollPane(table);
        model.setRenderer(table);

        //TableColumnModel tcm = table.getColumnModel();
        //tcm.removeColumn(tcm.getColumn(4));
        //table.removeColumn(aColumn);
        JPanel south = new JPanel();
        south.add(one);
        south.add(two);
        south.add(three);
        south.add(downloads.get(0).getDownloadControl().getProgressbar());
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(scrollPane);
        frame.getContentPane().add(south, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        java.util.Timer t = new java.util.Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {

                //studentSet.get(0).setDoneSize(downloads.get(0).getDoneSize()+1);
                //downloads.get(0).setProgressBar();
                Download downloadx;
                downloadx = downloads.get(1);
                downloadx.setDoneSize(downloadx.getDoneSize() + 1);
                //model.fireTableRowsUpdated(0, table.getRowCount() - 1);
                //downloadx.setDownloadtableui(new DownloadControl());
                int value = (int) (double) ((100.0 * downloadx.getDoneSize())
                        / downloadx.getFileSize());
                //System.out.println(value);
                downloadx.getDownloadControl().setProgressBar(value);
                model.fireTableRowsUpdated(0, model.getRowCount() - 1);
                //frame.repaint();
            }

        };
        t.scheduleAtFixedRate(tt, 100, 100);

    }
}
