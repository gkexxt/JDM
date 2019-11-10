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

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 *
 * @author G.K #gkexxt@outlook.com
 */
public class DownloadTableUI {

    private final JLabel lblControl;
    private final ImageIcon startIcon;
    private final ImageIcon stopIcon;
    private final JProgressBar progressbar;

    public DownloadTableUI() {
        lblControl = new JLabel();
        lblControl.setHorizontalAlignment(JLabel.CENTER);
        startIcon = new ImageIcon(new ImageIcon("play.png")
                .getImage().getScaledInstance(20, 20, java.awt.Image.SCALE_DEFAULT));
        stopIcon = new ImageIcon(new ImageIcon("stop.png")
                .getImage().getScaledInstance(20, 20, java.awt.Image.SCALE_DEFAULT));
        lblControl.setIcon(startIcon);
        progressbar = new JProgressBar(0, 100);
        progressbar.setStringPainted(true);
        progressbar.setValue(50);
    }

    public JProgressBar getProgressbar() {
        return this.progressbar;
    }

    public JLabel getLblControl() {
        return lblControl;
    }

    public void setLblControl(boolean startx) {
        
        System.err.println(startx);
        if (startx) {
            System.out.println("start");
            lblControl.setIcon(stopIcon);
        } else {
            lblControl.setIcon(startIcon);
            System.out.println("stop");
        }
    }

    public void setProgressBar(int value) {
        //System.out.println("javadm.gui.DownloadTableUI.setProgressBar()");
        progressbar.setValue(value);
        //progressbar.setStringPainted(true);
    }

}
