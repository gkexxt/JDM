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
package javadm.gui;

import java.awt.Component;
import javadm.com.DownloadTableModel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author G.K #gkexxt@outlook.com
 */
public class DownloadTable extends JTable {
    
    private  DownloadTableModel dm;
    public DownloadTable(DownloadTableModel dm) {
        super(dm);
        this.dm = dm;
        getColumn("control").setCellRenderer(new LabelRenderer());
        getColumn("Progress").setCellRenderer(new ProgresRenderer());
        //System.err.println(rowHeight);
        this.setRowHeight(20);

    }  

 
    public void setModelxx(DownloadTableModel dataModelx) {
        super.setModel(dataModelx); //To change body of generated methods, choose Tools | Templates.
        this.dm = dataModelx;
        this.getModel().notifyAll();
    }
    
    

  
    private class LabelRenderer implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            //return DwnTable.this.modelData.get(row).getDownloadtableui().getLblControl();
            //System.err.println(table.getModel().getValueAt(row, column).toString());
            return dm.getRow(row).getDownloadtableui().getLblControl();
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

            //table.getModel().modelData.get(row).getDownloadtableui().getProgressbar();
            //return progress;
            return dm.getRow(row).getDownloadtableui().getProgressbar();
        }
    }

}
