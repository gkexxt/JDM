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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javadm.util.RowTableModel;
import java.util.*;
import javadm.com.Download;
import javax.swing.*;
import javax.swing.JProgressBar;

/**
 *
 * @author G.K #gkexxt@outlook.com
 */
public class TableModel extends RowTableModel<Download> implements PropertyChangeListener {

    //private static int  xxx = 0;
    private static final String[] COLUMN_NAMES
            = {
                "Name",
                "Date",
                "rate",
                "url",
                "control",
                "Progress",
                "filesize",
                "donesize",};

    TableModel(DownloadManager mainframe) {
        super(Arrays.asList(COLUMN_NAMES));
        setRowClass(Download.class);

        setColumnClass(4, Boolean.class);
        //setColumnClass(2, JLabel.class);
        setColumnClass(5, JProgressBar.class);
        setColumnClass(6, String.class);
        setColumnClass(7, String.class);
        //setColumnEditable(0, false);
        //setColumnEditable(6, false);
        //setColumnClass(6, JProgressBar.class);

    }

    @Override
    public Object getValueAt(int row, int column) {
        Download downnload = getRow(row);

        switch (column) {
            case 0:
                return downnload.getName();
            case 1:
                return downnload.getCreatedDate();
            case 2:
                return downnload.getDownloadRate();
            case 3:
                return downnload.getUrl();
            case 4:
                return downnload.getDownloadControl().getLblControl();
            case 5:
                return downnload.getDownloadControl().getProgressbar();
            case 6:
                return downnload.getFileSize();
            case 7:
                return downnload.getDoneSize();
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (column == 0 || column == 4 || column == 6) {
            return true;
        }

        if (column == 5) {
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
                    download.setName(value.toString());
                    break;
                case 1:
                    download.setCreatedDate(value.toString());
                    break;
                case 2:
                    //download.setDirectory(value.toString());
                    break;
                case 3:
                    download.setUrl(value.toString());
                    break;
                case 4:
                    if (!download.isRunning()) {
                        download.startDownload();
                    } else {
                        download.stopDownload();
                    }
                    //download.getDownloadtableui().setLblControl(download.isStart());
                    //System.out.println("javadm.com.DownloadTableModel.setValueAt()");
                    break;
                case 6:

                    download.setFileSize(Long.parseLong(value.toString()));
                    break;
                case 7:
                    download.setDoneSize(Long.parseLong(value.toString()));

                //break;
                default:
                    break;
            }
            //fireTableCellUpdated(row, column);
            this.fireTableRowsUpdated(0, this.getRowCount());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage() + "\n" + e.toString(), "InfoBox: "
                    + "error update db", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        fireTableRowsUpdated(0, this.getRowCount());
    }

}
