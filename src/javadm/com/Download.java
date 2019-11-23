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
import static java.time.LocalDateTime.now;
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

    private boolean start;
    private String userAgent;
    private final List<String[]> logMsgs;
    public static final String ERROR = "Error";
    public static final String WARNING = "Warning";
    public static final String DEBUG = "Debug";
    public static final String INFO = "Info";
    public static final byte RESUMABLE = 1;
    public static final byte DYNAMIC = -1;
    public static final byte NON_RESUMEABLE = -2;
    public static final byte UNKNOWN = 0;
    public byte type = UNKNOWN;
    private int id;
    private String name = "";
    private String url = "";
    private String directory = "";
    private long fileSize;
    private long doneSize;
    private String createdDate = now().toString();
    private String lastDate;
    private String completeDate;
    private int connections = 1; //min
    private boolean complete;
    
    private List<Part> parts;

    private DownloadControl downloadControl;
    private final PropertyChangeSupport propChangeSupport
            = new PropertyChangeSupport(this);

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
  
    }

    public Download() {
        this.logMsgs = new ArrayList();
        this.downloadControl = new DownloadControl();// instace of control
        parts = new ArrayList<>();
    }

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }

    public void initParts() {
        switch (getType()) {
            case Download.UNKNOWN:
                return;
            case Download.DYNAMIC:
            case Download.NON_RESUMEABLE: {
                Part part = new Part();
                part.setSize(this.getFileSize());
                part.setPartFileName(this.getName());
                part.setCurrentSize(0);
                part.setId(0);
                parts.add(part);
                break;
            }
            case Download.RESUMABLE:
                long x = this.getFileSize() / Part.partSize;
                long last_length = this.getFileSize() - x * Part.partSize;
                for (int i = 0; i < x; i++) {
                    Part part = new Part();
                    part.setStartByte(i * Part.partSize);
                    part.setEndByte(i * Part.partSize + (Part.partSize - 1));
                    part.setSize(Part.partSize);
                    part.setPartFileName(this.getName() + ".part" + i);
                    part.setId(i);
                    parts.add(part);
                }
                if (last_length > 0) {
                    Part part = new Part();
                    part.setStartByte(x * Part.partSize);
                    part.setSize(last_length);
                    part.setEndByte(x * Part.partSize + last_length - 1);
                    part.setPartFileName(this.getName() + ".part" + x);
                    part.setId((int) x);
                    parts.add(part);
                }
                break;
            default:
                break;
        }
        DaoSqlite db = new DaoSqlite();
        db.insertParts(this.getId(), this.getParts());

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
            this.setDoneSize(doneSize + buffersize);
            int value = (int) (double) ((100.0 * doneSize)
                    / this.getFileSize());
            this.downloadControl.getProgressbar().setValue(value);
        } catch (Exception ex) {
            this.downloadControl.getProgressbar().setValue(0);
        }
        propChangeSupport.firePropertyChange("setProgress", "setProgress1", "setProgress2");
    }

    public void setStart(boolean start) {
        //propChangeSupport.firePropertyChange("Startxxxxxxxxxx", startDownloader, startDownloader);
        boolean oldstart = this.start;
        this.start = start;
        //System.out.println("javadm.data.Download.setStart()");
        //System.out.println(startDownloader);
        this.downloadControl.setLblControl(start);
        this.downloadControl.setRowlocked(start);
        if (start) {
            new Downloader(this).startDownloader();
        } else {
            StopDownload();
        }

        propChangeSupport.firePropertyChange("setStart", oldstart, start);

    }

    public void setDownloadSize(long fsize) {
        this.setFileSize(fsize);
    }

    private void StopDownload() {
        boolean allPartComplete = true;
        for (Part part : parts) {
            if (!part.isCompleted()) {
                allPartComplete = false;
            }
        }
        setComplete(allPartComplete);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
        //System.out.println("javadm.com.Download.setFileSize()");
    }

    public long getDoneSize() {
        return doneSize;
    }

    public void setDoneSize(long doneSize) {
        this.doneSize = doneSize;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    public String getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(String completeDate) {
        this.completeDate = completeDate;
    }

    public int getConnections() {
        return connections;
    }

    public void setConnections(int connections) {
        this.connections = connections;
    }

    public boolean isComplete() {
        return complete;
    }

    public synchronized void setComplete(boolean complete) {
        this.complete = complete;
        //System.out.println("javadm.data.Download.setComplete()");
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
