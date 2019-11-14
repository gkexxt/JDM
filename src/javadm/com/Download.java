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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javadm.data.Data;
import javadm.gui.DownloadControl;

/**
 * collection of data + controls
 *
 * @author G.K #gkexxt@outlook.com
 */
public class Download {

    private Data data;
    private boolean start;
    private DownloadControl downloadControl;
      private PropertyChangeSupport propChangeSupport = 
       new PropertyChangeSupport(this);

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    propChangeSupport.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    propChangeSupport.removePropertyChangeListener(listener);
  }


 

    public DownloadControl getDownloadControl() {
        return downloadControl;
    }

    public void setDownloadControl(DownloadControl downloadControl) {
        this.downloadControl = downloadControl;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public  boolean isStart() {
        return start;
        
    }

    public void setProgress(long buffersize) {
        try {
            data.setDoneSize(data.getDoneSize()+ buffersize);
            int value = (int) (double) ((100.0 * data.getDoneSize())
                    / data.getFileSize());
            this.downloadControl.getProgressbar().setValue(value);
        } catch (Exception e) {
        }
         
    }

    public void setStart(boolean startx) {
        this.start = startx;
        //System.out.println("javadm.data.Download.setStart()");
        //System.out.println(start);
        this.downloadControl.setLblControl(start);
        this.downloadControl.setRowlocked(start);
        if (start) {

            StartDownload();

        } else {

            StopDownload();
        }
        propChangeSupport.firePropertyChange("Start",start,startx);

    }

    private void StartDownload() {
       DownloadWorker t1 = new DownloadWorker("thread 1", this);
       //DownloadWorker t2 = new DownloadWorker("thread 2", this);
        //DownloadWorker t3 = new DownloadWorker("thread 4", this);
        t1.start();
        //t2.start();
        
    }

    private void StopDownload() {
        System.out.println("JavaDM.Data.DownloadData.StopDownload()");
    }

}
