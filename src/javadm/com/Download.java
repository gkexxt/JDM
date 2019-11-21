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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    private final List<String[]> logMsgs;
    public static final String ERROR = "Error";
    public static final String WARNING = "Warning";
    public static final String DEBUG = "Debug";
    public static final String INFO = "Info";

    private DownloadControl downloadControl;
    private final PropertyChangeSupport propChangeSupport
            = new PropertyChangeSupport(this);
    private List<Part> parts;

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }

    public void display() {
        for (int i = 0; i < parts.size(); i++) {
            System.err.println(parts.get(i).getStartByte());
        }
    }

    public void initParts() {

        long x = getData().getFileSize() / Part.partDefaultSize;
        long last_length = getData().getFileSize() - x*Part.partDefaultSize;
      
        
        

        //System.err.println("segments : " + x);
        for (int i = 0; i < x; i++) {
            Part part = new Part();
            part.setStartByte(i * Part.partDefaultSize);
            part.setEndByte(i * Part.partDefaultSize + (Part.partDefaultSize - 1));
           part.setPartSize(Part.partDefaultSize);
            part.setPartFileName(getData().getName() + ".part" + i);
            parts.add(part);

        }
        
        if (last_length >0){
            Part part = new Part();
            part.setStartByte(x * Part.partDefaultSize);
            part.setPartSize(last_length);
            part.setEndByte(x * Part.partDefaultSize + last_length-1);
            part.setPartFileName(getData().getName() + ".part" + x);
            parts.add(part);
        }
        
        

    }

    public Download() {
        this.logMsgs = new ArrayList();
        this.userAgent = "Mozilla/5.0 (Macintosh; U;"
                + " Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2";
        this.downloadControl = new DownloadControl();// instace of control
        parts = new ArrayList<>();
    }

    public List<String[]> getLogMsgs() {
        return logMsgs;
    }

    /**
     *
     * @param errorMessage
     */
    public synchronized void addLogMsg(String[] errorMessage) {
        this.logMsgs.add(errorMessage);
        propChangeSupport.firePropertyChange("addErrorMessage", "errorMessage", "update");
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

    public synchronized Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public boolean isStart() {
        return start;
    }

    /**
     * get filename from url
     *
     * @param url
     * @return fname
     */
    public static String getUrl_name(String url) {
        String[] urlSplit = url.split("/");
        System.err.println(Arrays.toString(urlSplit));
        String fname = urlSplit[urlSplit.length - 1];
        System.err.println(fname);
        return (fname);
    }

    public synchronized void setProgress(long buffersize) {
        try {
            data.setDoneSize(data.getDoneSize() + buffersize);
            int value = (int) (double) ((100.0 * data.getDoneSize())
                    / data.getFileSize());
            this.downloadControl.getProgressbar().setValue(value);
            propChangeSupport.firePropertyChange("setProgress", "setProgress1", "setProgress2");
        } catch (Exception ex) {
            this.addLogMsg(new String[]{ex.getMessage(), ex.toString()});
        }
    }

    public void setStart(boolean startx) {
        //propChangeSupport.firePropertyChange("Startxxxxxxxxxx", start, startx);
        boolean oldstart = this.start;
        this.start = startx;
        //System.out.println("javadm.data.Download.setStart()");
        //System.out.println(start);
        this.downloadControl.setLblControl(start);
        this.downloadControl.setRowlocked(start);
        if (start) {

            //StartDownload();
            //getData().setFileSize(getcontentLength());
            new DownloadWorker(this).startDownloadController();
        } else {

            StopDownload();
        }

        propChangeSupport.firePropertyChange("setStart", oldstart, start);

    }

    public void setDownloadSize(long fsize) {
        this.data.setFileSize(fsize);
        if (start) {
            System.out.println("javadm.com.Download.setDownloadSize()");
            // new DownloadWorker(this).startDownloader();
        }
    }

    private void StartDownload() {

    }

    private void StopDownload() {
        //System.out.println("JavaDM.Data.Data.StopDownload()");
    }

    /**
     * check url is valid
     *
     * @param url
     * @return true if valid
     */
    public static boolean isUrlOK(String url) {
        try {
            URL u = new URL(url); // this would check for the protocol
            u.toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

   

}
