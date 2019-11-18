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
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javadm.data.Data;
import javadm.ui.DownloadControl;

/**
 * collection of data + controls
 *
 * @author G.K #gkexxt@outlook.com
 */
public class Download {

    private Data data;
    private boolean start;
    private String userAgent;
    private String errorMessage[];
    private List<String[]> errorLog;

    public List<String[]> getErrorLog() {
        return errorLog;
    }
    private DownloadControl downloadControl;
    private PropertyChangeSupport propChangeSupport
            = new PropertyChangeSupport(this);
     private PropertyChangeSupport errorRecived
            = new PropertyChangeSupport(this);
     
    public Download() {
        this.errorMessage = new String[2];
        this.errorLog = new ArrayList();
        this.userAgent = "Mozilla/5.0 (Macintosh; U;"
                + " Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2";
        this.downloadControl = new DownloadControl();//ad instace of control
    }

    /**
     *
     * @return errorMessage[2]
     */
    public String[] getErrorMessage() {
        return errorMessage;
    }

    /**
     *
     * @param errorMessage
     */
    public synchronized void setErrorMessage(String[] errorMessage) {
        this.errorMessage = errorMessage;
        this.errorLog.add(errorMessage);
        //System.err.println(errorMessage[0]);
        //System.err.println(Arrays.toString(errorLog.get(errorLog.size()-1)));
        propChangeSupport.firePropertyChange("setErrorMessage", "errorMessage", "update");
        //System.out.println("javadm.com.Download.setErrorMessage()");

    }

    /**
     *
     * @return userAgent String
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     *
     * @param userAgent
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

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

    public boolean isStart() {
        return start;
    }

    public String getUrl_name() {
        try {
            URL urlTemp;
            urlTemp = new URL(this.getData().getUrl());
            String[] urlSplit = urlTemp.getFile().split("/");
            return (urlSplit[urlSplit.length - 1]);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Download.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void setProgress(long buffersize) {
        try {
            data.setDoneSize(data.getDoneSize() + buffersize);
            int value = (int) (double) ((100.0 * data.getDoneSize())
                    / data.getFileSize());
            this.downloadControl.getProgressbar().setValue(value);
            propChangeSupport.firePropertyChange("setProgress", "setProgress1", "setProgress2");
        } catch (Exception ex) {
            this.setErrorMessage(new String [] {ex.getMessage(),ex.toString()});
        }
    }

    public void setStart(boolean startx) {
        //propChangeSupport.firePropertyChange("Startxxxxxxxxxx", start, startx);
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
        propChangeSupport.firePropertyChange("setStart", "start", "stop");

    }

    public long getcontentLength() {

        try {
            URL urlTemp;
            urlTemp = new URL(this.getData().getUrl());
            HttpURLConnection conn = (HttpURLConnection) urlTemp.openConnection();
            conn.setRequestProperty("User-Agent", userAgent);            // connect to server
            conn.connect();
            return conn.getContentLengthLong();
        } catch (MalformedURLException ex) {
            
            this.setErrorMessage(new String [] {ex.getMessage(),ex.toString()});
            return -2;
            
        } catch (IOException ex) {
            this.setErrorMessage(new String [] {ex.getMessage(),ex.toString()});
            return -1;
        }

    }

    private void StartDownload() {
        DownloadWorker t1 = new DownloadWorker("thread 1", this);
        //DownloadWorker t2 = new DownloadWorker("thread 2", this);
        //DownloadWorker t3 = new DownloadWorker("thread 4", this);
        t1.start();
        //t2.start();

    }

    private void StopDownload() {
        //System.out.println("JavaDM.Data.DownloadData.StopDownload()");
    }

}
