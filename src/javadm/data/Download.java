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
package javadm.data;

import javadm.gui.DownloadTableUI;

/**
 *
 * @author G.K #gkexxt@outlook.com
 */
public class Download {

    private Data data;
    private boolean start;

    public DownloadTableUI getDownloadtableui() {
        return downloadtableui;
    }

    public void setDownloadtableui(DownloadTableUI downloadtableui) {
        this.downloadtableui = downloadtableui;
    }
    private  DownloadTableUI downloadtableui;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Download() {
        //downloadtableui = new DownloadTableUI();
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean startx) {
        this.start = startx;
        //System.out.println("javadm.data.Download.setStart()");
        //System.out.println(start);
        this.downloadtableui.setLblControl(start);
        if (start) {
            
            StartDownload();
        } else {

            StopDownload();
        }

    }

    private void StartDownload() {
        System.out.println("JavaDM.Data.DownloadData.StartDownload()");
    }

    private void StopDownload() {
        System.out.println("JavaDM.Data.DownloadData.StopDownload()");
    }

}
