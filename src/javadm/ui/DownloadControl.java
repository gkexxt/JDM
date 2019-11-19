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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTable;

/**
 *
 * @author G.K #gkexxt@outlook.com
 */
public class DownloadControl {

    Dimension x = new Dimension(10, 300);
    private final JLabel lblControl;
    private final JProgressBar progressbar;
    private boolean rowlocked;
    private JTable table;

    public boolean isRowlocked() {
        return rowlocked;
    }

    public JTable getTable() {
        return table;
    }

    public void setRowlocked(boolean rowlocked) {
        this.rowlocked = rowlocked;
    }

    public DownloadControl() {

        lblControl = new JLabel();
        lblControl.setHorizontalAlignment(JLabel.CENTER);
        //lblControl.setIcon(startIcon);
        lblControl.setText("\u25B6");
        lblControl.setForeground(Color.GREEN);
        progressbar = new JProgressBar(0, 100);
        progressbar.setStringPainted(true);
        //progressbar.setValue(0);
        progressbar.setBorderPainted(false);
        //progressbar.setBackground(Color.WHITE);
        progressbar.setOpaque(false);
        //Font = new Font
        lblControl.setFont(new Font(lblControl.getName(), Font.PLAIN, 18));
    }

    public JProgressBar getProgressbar() {
        return this.progressbar;
    }

    public JLabel getLblControl() {
        return lblControl;
    }

    public void setLblControl(boolean startx) {

        //System.err.println(startx);
        if (startx) {
            //System.out.println("start");
            lblControl.setForeground(Color.red);
            lblControl.setText("\u25A0");

        } else {
            //lblControl.setIcon(startIcon);
            //System.out.println("stop");
            lblControl.setForeground(Color.GREEN);
            lblControl.setText("\u25B6");
        }
    }

    public void setProgressBar(int value) {
        //System.out.println("javadm.gui.DownloadControl.setProgressBar()");
        progressbar.setValue(value);
        //progressbar.setStringPainted(true);
    }

}
