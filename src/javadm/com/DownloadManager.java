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

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import javadm.data.Data;
import javadm.data.DataDaoSqlite;
import javadm.gui.DownloadControl;
import javadm.gui.DownloadTable;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 *
 * @author G.K #gkexxt@outlook.com
 */
public class DownloadManager {
    
    public static void main(String[] args) {
        DownloadTableModel model = new DownloadTableModel();
        DataDaoSqlite db = new DataDaoSqlite();
        List<Data> datas = new ArrayList<>();
        datas.addAll(db.getAllDownloadData());
        //List<Download> downloads = new ArrayList<>();
        
        for (int i = 0; i < datas.size(); i++) {
            Data data = datas.get(i);
            Download download = new Download();
            download.setData(data);
            download.setDownloadControl(new DownloadControl());
            download.setProgress(download.getData().getDoneSize());
            //downloads.add(download);
            model.addRow(download);
        }
        
        DownloadTable table = new DownloadTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollPane = new JScrollPane(table);
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(scrollPane);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
 
        
        java.util.Timer t = new java.util.Timer();
        TimerTask tt;
        tt = new TimerTask() {
            @Override
            public void run() {
                model.fireTableRowsUpdated(0, model.getRowCount() - 1);
            }
        };
        t.scheduleAtFixedRate(tt, 50, 50);
        
    }
    
}
