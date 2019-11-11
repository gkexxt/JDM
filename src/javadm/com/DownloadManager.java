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

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import javadm.data.Data;
import javadm.data.DataDaoSqlite;
import javadm.gui.DownloadControl;
import javadm.gui.DownloadTable;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;


/**
 *
 * @author G.K #gkexxt@outlook.com
 */
public class DownloadManager {
    
       public static void main(String[] args) {
        JButton one = new JButton("One");
        JButton two = new JButton("Two");
        JButton three = new JButton("Three");
        DownloadTableModel model = new DownloadTableModel();
        DownloadTableModel model2 = new DownloadTableModel();
        DownloadTableModel model3 = new DownloadTableModel();
        //  Use the custom model
        //table.setModel(model);

        //  Use the BeanTableModel  
//		BeanTableModel<JButton> model =
//			new BeanTableModel<JButton>(JButton.class, java.awt.Component.class);
        DataDaoSqlite db = new DataDaoSqlite();

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
            download.setDownloadtableui(new DownloadControl());

            int value = (int) (double) ((100.0 * download.getData().getDoneSize())
                    / download.getData().getFileSize());
            download.getDownloadtableui().setProgressBar(value);

            downloads.add(download);
            //lstdtb.add(new Download().setDdata(datas.get(i)));
            model.addRow(download);
            model2.addRow(download);
            model3.addRow(download);

        }
        
        
        model2.removeRowRange(2, 3);
        model3.removeRowRange(0, 1);

        DownloadTable table = new DownloadTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);        
        JScrollPane scrollPane = new JScrollPane(table);
        //model.setRenderer(table);

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
                downloadx = downloads.get(5);
                downloadx.getData().setDoneSize(downloadx.getData().getDoneSize() + 1);
                //model.fireTableRowsUpdated(0, table.getRowCount() - 1);
                //downloadx.setDownloadtableui(new DownloadControl());
                int value = (int) (double) ((100.0 * downloadx.getData().getDoneSize())
                        / downloadx.getData().getFileSize());
                //System.out.println(value);
                
//                if (value > 50 && value < 70){
//                    table.setModel(model2);
//                    table.repaint();
//                }else if (value > 70){
//                  table.setModel(model2);
//                  table.repaint();
//                }
                downloadx.getDownloadtableui().setProgressBar(value);
                model.fireTableRowsUpdated(0, model.getRowCount() - 1);
                //table.getModel().
                //frame.repaint();
            }

        };
        t.scheduleAtFixedRate(tt, 5000, 5000);

    }
    
}
