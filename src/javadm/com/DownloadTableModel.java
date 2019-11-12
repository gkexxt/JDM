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
package javadm.com;

import javadm.util.RowTableModel;
import java.util.*;
import javax.swing.*;
import javax.swing.JProgressBar;

/**
 *
 * @author G.K #gkexxt@outlook.com
 */
public class DownloadTableModel extends RowTableModel<Download> {
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

    DownloadTableModel() {
        super(Arrays.asList(COLUMN_NAMES));
        setRowClass(Download.class);

        setColumnClass(5, Boolean.class);
        //setColumnClass(2, JLabel.class);
        setColumnClass(6, JProgressBar.class);
        setColumnClass(7, String.class);
        setColumnClass(8, String.class);
        //setColumnEditable(0, false);
        //setColumnEditable(6, false);
        //setColumnClass(6, JProgressBar.class);

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
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (column == 0 || column == 4 || column == 6) {
            return false;
        }

        if (column == 5 ) {
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
                    System.out.println("javadm.com.DownloadTableModel.setValueAt()");
                    break;
                case 7:

                    download.getData().setFileSize(Long.parseLong(value.toString()));
                    break;
                case 8:
                    download.getData().setDoneSize(Long.parseLong(value.toString()));
                
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

}
