/*
 * The MIT License
 *
 * Copyright 2019 gkalianan.
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

import java.util.TimerTask;

/**
 *
 * @author gkalianan
 */
public class DownloadWorker implements Runnable {

    String name;
    Thread t;
    Download download;

    DownloadWorker(String threadname, Download download) {
        this.download = download;
        name = threadname;
        t = new Thread(this, name);
        System.out.println("New thread: " + t);
    }

    public void start() {
        t.start();
    }

    @Override
    public void run() {
        java.util.Timer timer = new java.util.Timer();
        TimerTask tt = new TimerTask() {
            @Override
            public void run() {

                //studentSet.get(0).setDoneSize(datas.get(0).getDoneSize()+1);
                //downloads.get(0).setProgressBar();
                Download downloadx;
                downloadx = download;
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
// model.fireTableRowsUpdated(0, model.getRowCount() - 1);
//table.getModel().
//frame.repaint();
            }

        };
        timer.scheduleAtFixedRate(tt, 50, 50);
        System.out.println(name + " exiting.");
    }

}
