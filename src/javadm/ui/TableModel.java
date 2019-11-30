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

import javadm.util.RowTableModel;
import java.util.*;
import javadm.com.Download;
import javax.swing.JProgressBar;

/**
 *
 * @author G.K #gkexxt@outlook.com
 */
public class TableModel extends RowTableModel<Download> {

    //private static int  xxx = 0;
    private static final String[] COLUMN_NAMES
            = {
                "Name",
                "Size",
                "Complete",
                "Speed",
                "Added On",
                "Control",
                "Progress"};

    TableModel(DownloadManager mainframe) {
        super(Arrays.asList(COLUMN_NAMES));
        setRowClass(Download.class);

        setColumnClass(5, Boolean.class);
        //setColumnClass(2, JLabel.class);
        setColumnClass(6, JProgressBar.class);
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
                return downnload.getFormatedSize(downnload.getFileSize());
            case 2:
                return downnload.getFormatedSize(downnload.getDoneSize());
            case 3:
                return downnload.getDownloadRate();
            case 4:
                return downnload.getCreatedDate();
            case 5:
                return downnload.getDownloadControl().getLblControl();
            case 6:
                return downnload.getDownloadControl().getProgressbar();
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {      

        return column == 5;
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        Download download = getRow(row);
        switch (column) {
            case 5:
                if (!download.isRunning()) {
                    download.startDownload();
                } else {
                    download.stopDownload();
                }
                break;
            default:
                break;
        }
        fireTableCellUpdated(row, column);
        //this.fireTableRowsUpdated(0, this.getRowCount());

    }

}
