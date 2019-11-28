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
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import static java.time.LocalDateTime.now;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javadm.ui.DownloadControl;

/**
 * collection of data + controls
 *
 * @author G.K #gkexxt@outlook.com
 */
public class Download {

    private String userAgent;
    private final List<String[]> logMsgs;
    //Download states
    public static final String STCOMPLETE = "Complete";
    public static final String STERROR = "Error";
    public static final String STDOWNLOADING = "Downloading";
    public static final String STFINISHED = "Finished";
    public static final String STUNKNOWN = "Unknown";
    public static final String STSTOPED = "Stoped";
    //error log types
    public static final String ERROR = "Error";
    public static final String WARNING = "Warning";
    public static final String DEBUG = "Debug";
    public static final String INFO = "Info";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
    //download types
    public static final byte RESUMABLE = 1;
    public static final byte DYNAMIC = -1;
    public static final byte NON_RESUMEABLE = -2;
    public static final byte UNKNOWN = 0;
    private boolean scheduled = false;
    private String scheduleStart = "";
    private String scheduleStop = "";
    private int xxxx = 0;
    private volatile boolean needupdate = false;
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
    private String state;
    private volatile double rate = 0;
    private long last_data_time = 0;
    private long current_data_time = 0;
    private long last_data_size = 0;
    private volatile String downloadRate;

    private int connections = 1; //min
    private boolean complete;
    private int retry = 3;
    private volatile boolean running;
    private DownloadControl downloadControl;
    private final PropertyChangeSupport propChangeSupport
            = new PropertyChangeSupport(this);
    private List<Part> parts;
    List<Double> myList = new ArrayList<>();

    public Download() {
        this.logMsgs = new ArrayList();
        this.downloadControl = new DownloadControl();// instace of control
        parts = new ArrayList<>();
        //this.scheduleStart = formatter.format(new Date());
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isScheduled() {
        return scheduled;
    }

    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }

    public String getScheduleStart() {

        return scheduleStart;
    }

    public void setScheduleStart(String scheduleStart) {
        this.scheduleStart = scheduleStart;
    }

    public String getScheduleStop() {
        return scheduleStop;
    }

    public void setScheduleStop(String scheduleStop) {
        this.scheduleStop = scheduleStop;
    }
  
    double ratex = 0;

    public synchronized String getDownloadRate() {

        if (!running) {
            return "0 KB/s";
        }
        //System.err.println(rate);
        current_data_time = new Date().getTime();

        if (current_data_time - last_data_time > 1000) {
            long xdone = this.doneSize;
            ratex = (double) (xdone - last_data_size) / ((double) (current_data_time - last_data_time) / 1000);
            //System.err.println(xdone);
            //System.err.println(last_data_size);
            //System.err.println(last_data_time);
            //System.err.println(current_data_time);
            last_data_size = xdone;
            last_data_time = current_data_time;
            //xxx++;

            //System.err.println(ratex);
            //System.err.println("------------------");      
        }

        if (ratex < 1) {
            return "0 KB/s";
        } else if (ratex <= 1e+3) {
            return (int) ratex + " B/s";
        } else if (ratex <= 1e+6) {
            return (int) (ratex / 1e+3) + " KB/s";
        } else if (ratex <= 1e+9) {
            return (int) (ratex / 1e+6) + " MB/s";
        } else if (ratex <= 1e+12) {
            return (int) (ratex / 1e+9) + " GB/s";

        } else {
            return "Unknown";
        }

    }

    public synchronized boolean isNeedupdate() {
        return needupdate;
    }

    public synchronized boolean isRunning() {
        return running;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;

    }

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }

    public void initParts() {
        parts = new ArrayList<>();
        switch (getType()) {
            case Download.UNKNOWN:
                return;
            case Download.DYNAMIC:
            case Download.NON_RESUMEABLE: {
                Part part = new Part();
                part.setSize(this.getFileSize());
                part.setPartFileName(this.getName() + "-" + this.getId() + 0);
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
                    part.setPartFileName(this.getName() + "-" + this.getId() + ".part" + i);
                    part.setId(i);
                    parts.add(part);
                }
                if (last_length > 0) {
                    Part part = new Part();
                    part.setStartByte(x * Part.partSize);
                    part.setSize(last_length);
                    part.setEndByte(x * Part.partSize + last_length - 1);
                    part.setPartFileName(this.getName() + "-" + this.getId() + ".part" + x);
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
    public  void addLogMsg(String[] errorMessage) {
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

    public void setDownloadSize(long fsize) {
        this.setFileSize(fsize);
    }

    public void startDownload() {
        if (!isComplete()) {
            this.running = true;
            this.downloadControl.setLblControl(true);
            this.downloadControl.setRowlocked(true);
            this.addLogMsg(new String[]{Download.INFO, "Download Started"});
            setState(Download.STDOWNLOADING);
            last_data_time = new Date().getTime();
            new Downloader(this).startDownloader();
            this.needupdate = true;
            propChangeSupport.firePropertyChange("running", false, true);
        } else {
            addLogMsg(new String[]{Download.ERROR, "Download already completed"});
        }

    }

    public void stopDownload() {
        if (!isComplete()) {
            this.downloadControl.setLblControl(false);
            this.downloadControl.setRowlocked(false);
            this.addLogMsg(new String[]{Download.INFO, "Download Stopped"});
            setState(Download.STSTOPED);
            setScheduled(false);
            this.running = false;
            propChangeSupport.firePropertyChange("running", true, false);

        } else {
            addLogMsg(new String[]{Download.ERROR, "Download should not be started if already completed!! fuck it got bug"});
        }

    }

    public void updateDownload() {
        System.out.println(xxxx);
        xxxx++;

        //check if all part is complete
        boolean allPartComplete = true;
        for (Part part : this.parts) {
            System.err.println(part.getSize() + " : " + part.getCurrentSize());
            if (part.getCurrentSize() >= part.getSize() && part.getCurrentSize() > 0) {
                part.setCompleted(true);
            }

            if (!part.isCompleted()) {
                allPartComplete = false;
            }
        }

        //upate parts @ db
        DaoSqlite db = new DaoSqlite();
        parts.forEach((part) -> {
            db.updatePart(this.getId(), part);
        });

        if (allPartComplete) {
            setComplete(true);
            setState(Download.STCOMPLETE);
            setCompleteDate(now().toString());
            db.deleteParts(this.getId());
            buildfile();

        }

        db.updateDownload(this);
        this.downloadControl.setLblControl(false);
        this.downloadControl.setRowlocked(false);
        needupdate = false;

    }

    private boolean buildfile() {
        //check if file exist anf rename to something else
        int append = 1;
        String tempname = getName();
        while (true) {
            File tmpfile = new File(getDirectory() + "/" + tempname);
            System.err.println(tempname);
            if (tmpfile.exists()) {
                //System.err.println("exist");
                addLogMsg(new String[]{Download.WARNING, "File with same name exist - " + tempname});
                //System.err.println("add message");
                tempname = tempname + "-" + append;
                addLogMsg(new String[]{Download.WARNING, "renaming file to - " + tempname});
                //System.err.println("add message 2");
                append++;
                //System.err.println("append : " + append);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Download.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                this.setName(tempname);
                break;
            }
        }

        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(this.getDirectory() + "/" + this.getName(), "rw");

            for (Part part : this.parts) {
                File file = new File(this.getDirectory() + "/" + part.getPartFileName());
                byte[] fileContent = Files.readAllBytes(file.toPath());
                raf.seek(raf.length());
                raf.write(fileContent);
                file.delete();

            }
            return true;
        } catch (Exception ex) {
            this.addLogMsg(new String[]{Download.ERROR, ex.toString()});
        }
        try {
            if (raf != null) {
                raf.close();
            }
        } catch (IOException ex) {
            this.addLogMsg(new String[]{Download.ERROR, ex.toString()});
        }

        return false;
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
