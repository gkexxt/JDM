/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadm.data;

/**
 *
 * @author G.K #gkexxt@outlook.com
 */
import java.util.*;
import java.awt.BorderLayout;
import java.awt.Component;
import javadm.gui.DownloadTableUI;
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
                return downnload.getData().getId();
            case 1:
                return downnload.getData().getName();
            case 2:
                return downnload.getData().getCreatedDate();
            case 3:
                return downnload.getData().getDirectory();
            case 4:
                return downnload.getData().getUrl();
            case 5:
                return downnload.getDownloadtableui().getLblControl();
            case 6:
                return downnload.getDownloadtableui().getProgressbar();
            case 7:
                return downnload.getData().getFileSize();
            case 8:
                return downnload.getData().getDoneSize();
            case 9:
                return downnload.getDownloadtableui().isRowlocked();
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

        if (this.modelData.get(row).getDownloadtableui().isRowlocked()) {
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

                    download.getData().setFileSize(Long.parseLong(value.toString()));
                    break;
                case 8:
                    download.getData().setDoneSize(Long.parseLong(value.toString()));
                case 9:
                    download.getDownloadtableui().setRowlocked(Boolean.parseBoolean(value.toString()));
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

            return DwnTable.this.modelData.get(row).getDownloadtableui().getLblControl();
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
            //System.out.println("JavaDM.Data.DwnTable.ProgresRenderer.getTableCellRendererComponent()")
            //System.out.println(DwnTable.this.modelData.get(row).getData().getId());

            return DwnTable.this.modelData.get(row).getDownloadtableui().getProgressbar();

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
        DownloadDataDaoSqlite db = new DownloadDataDaoSqlite();

        //System.out.println();
        List<Data> datas = new ArrayList<>();
        datas.addAll(db.getAllDownloadData());
        List<Download> downloads = new ArrayList<>();
        //DownloadTableData dtb = 
        //dtb.a
        //System.err.println(datas.get(0).getLblControl().toString());
        for (int i = 0; i < datas.size(); i++) {
            //String arg = args[i];
            Data data = datas.get(i);
            Download download = new Download();
            download.setData(data);
            download.setDownloadtableui(new DownloadTableUI());

            int value = (int) (double) ((100.0 * download.getData().getDoneSize())
                    / download.getData().getFileSize());
            download.getDownloadtableui().setProgressBar(value);

            downloads.add(download);
            //lstdtb.add(new Download().setDdata(datas.get(i)));
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
        south.add(downloads.get(0).getDownloadtableui().getProgressbar());
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

                //studentSet.get(0).setDoneSize(datas.get(0).getDoneSize()+1);
                //downloads.get(0).setProgressBar();
                Download downloadx;
                downloadx = downloads.get(1);
                downloadx.getData().setDoneSize(downloadx.getData().getDoneSize() + 1);
                //model.fireTableRowsUpdated(0, table.getRowCount() - 1);
                //downloadx.setDownloadtableui(new DownloadTableUI());
                int value = (int) (double) ((100.0 * downloadx.getData().getDoneSize())
                        / downloadx.getData().getFileSize());
                //System.out.println(value);
                downloadx.getDownloadtableui().setProgressBar(value);
                model.fireTableRowsUpdated(0, model.getRowCount() - 1);
                //frame.repaint();
            }

        };
        t.scheduleAtFixedRate(tt, 100, 100);

    }
}
