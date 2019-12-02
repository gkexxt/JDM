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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import static java.time.LocalDateTime.now;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javadm.ui.DownloadControl;
import javax.swing.table.DefaultTableModel;

/**
 * collection of data + controls
 *
 * @author G.K #gkexxt@outlook.com
 */
public class Download {

    private String userAgent;
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
    public static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");    //download types
    public static final byte RESUMABLE = 1;
    public static final byte DYNAMIC = -1;
    public static final byte NON_RESUMEABLE = -2;
    public static final byte UNKNOWN = 0;
    private boolean scheduled = false;
    private String scheduleStart = "";
    private String scheduleStop = "";
    private volatile boolean needupdate = false;
    public byte type = UNKNOWN;
    private int id;
    private String name = "";
    private String url = "";
    private String directory = "";
    private long fileSize;
    private long doneSize;
    private String createdDate = FORMATTER.format(new Date());
    private long elapsed = 0;
    private String lastDate;
    private String completeDate;
    private String state = "";
    private long last_data_time = 0;
    private long current_data_time = 0;
    private long last_data_size = 0;
    private int connections = 1; //min
    private boolean complete;
    private String group = "";
    private int retry = 3;
    private volatile boolean running;
    private DownloadControl downloadControl;
    private final PropertyChangeSupport propChangeSupport
            = new PropertyChangeSupport(this);
    private List<DownloadPart> parts;
    private int rate = 0;
    private final DefaultTableModel logmodel;
    private static final DecimalFormat DF = new DecimalFormat("0.00");
    private AtomicBoolean downloader_started;

    public Download() {
        this.downloadControl = new DownloadControl();// instace of control
        parts = new ArrayList<>();
        logmodel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        logmodel.addColumn("Time");
        logmodel.addColumn("Type");
        logmodel.addColumn("Message");

    }

    public AtomicBoolean statusDownloaderStarted() {
        return downloader_started;
    }

   

    public long getElapsed() {
        return elapsed;
    }

    public void setElapsed(long elapsed) {
        this.elapsed = elapsed;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
        propChangeSupport.firePropertyChange("state", "x", "xx");
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

    public String getFormatedSize(long size) {
        if (size < 1000) {
            return size + " B";
        } else if (size < 1000000) {
            return DF.format(size / 1000.0) + " KB";
        } else if (size < 1000000000) {
            return DF.format(size / 1000000.0) + " MB";
        } else {
            return DF.format(size / 1000000000.0) + " GB";
        }
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

    public synchronized String getDownloadRate() {
        //System.err.println(rate);

        if (!this.isRunning()) {
            return "0 B/s";
        }

        current_data_time = new Date().getTime();

        if (current_data_time - last_data_time > 1000) {
            double rateDouble;
            long xdone = this.doneSize;
            rateDouble = (double) (xdone - last_data_size)
                    / ((double) (current_data_time - last_data_time) / 1000);
            last_data_size = xdone;
            last_data_time = current_data_time;
            rate = (int) rateDouble;
        }

        if (rate <= 1e+3) {
            return (int) rate + " B/s";
        } else if (rate <= 1e+6) {
            return (int) (rate / 1e+3) + " KB/s";
        } else if (rate <= 1e+9) {
            return (int) (rate / 1e+6) + " MB/s";
        } else if (rate <= 1e+12) {
            return (int) (rate / 1e+9) + " GB/s";
        } else {
            return (int) (rate / 1e+12) + " TB/s";
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

    public DefaultTableModel getLogmodel() {
        return logmodel;
    }

    public void setType(byte type) {
        this.type = type;

    }

    public List<DownloadPart> getParts() {
        return parts;
    }

    public void setParts(List<DownloadPart> parts) {
        this.parts = parts;
    }

    public void initParts() {
        parts = new ArrayList<>();
        switch (getType()) {
            case Download.UNKNOWN:
                return;
            case Download.DYNAMIC:
            case Download.NON_RESUMEABLE: {
                DownloadPart part = new DownloadPart();
                part.setSize(this.getFileSize());
                part.setPartFileName(this.getName() + "-" + this.getId() + 0);
                part.setCurrentSize(0);
                part.setId(0);
                parts.add(part);
                break;
            }
            case Download.RESUMABLE:
                long x = this.getFileSize() / DownloadPart.partSize;
                long last_length = this.getFileSize() - x * DownloadPart.partSize;
                for (int i = 0; i < x; i++) {
                    DownloadPart part = new DownloadPart();
                    part.setStartByte(i * DownloadPart.partSize);
                    part.setEndByte(i * DownloadPart.partSize + (DownloadPart.partSize - 1));
                    part.setSize(DownloadPart.partSize);
                    part.setPartFileName(this.getName() + "-" + this.getId() + ".part" + i);
                    part.setId(i);
                    parts.add(part);
                }
                if (last_length > 0) {
                    DownloadPart part = new DownloadPart();
                    part.setStartByte(x * DownloadPart.partSize);
                    part.setSize(last_length);
                    part.setEndByte(x * DownloadPart.partSize + last_length - 1);
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

    /**
     *
     * @param errorMessage
     */
    public synchronized void addLogMsg(String[] errorMessage) {
        String[] msgx = new String[]{FORMATTER.format(new Date()), errorMessage[0], errorMessage[1]};
        logmodel.addRow(msgx);
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
            this.addLogMsg(new String[]{Download.INFO, "Download Started"});
            last_data_time = new Date().getTime();
            new Downloader(this).startDownloader();
            this.needupdate = true;
            propChangeSupport.firePropertyChange("running", false, true);
        } else {
            addLogMsg(new String[]{Download.ERROR, "Download already completed"});
        }

    }

    public void stopDownload() {
        this.downloadControl.setLblControl(false);
        this.addLogMsg(new String[]{Download.INFO, "Download Stopped"});
        setScheduled(false);
        this.running = false;
        propChangeSupport.firePropertyChange("running", true, false);

    }

    public void updateDownload() {
        if (this.getState().equals(STERROR)) {
            DaoSqlite db = new DaoSqlite();
            db.updateDownload(this);
            this.downloadControl.setLblControl(false);
            setScheduled(false);
            this.running = false;
            needupdate = false;
            propChangeSupport.firePropertyChange("updateDownload", true, false);
            return;
        } else {
            this.setState(Download.STSTOPED);
        }

        //check if all part is complete
        boolean allPartComplete = true;
        for (DownloadPart part : this.parts) {
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
            if (buildfile()) {
                setComplete(true);
                setCompleteDate(now().toString());
                this.setState(STCOMPLETE);
            }

        } else {
            this.setState(STERROR);
        }

        db.updateDownload(this);
        this.downloadControl.setLblControl(false);
        setScheduled(false);
        this.running = false;
        needupdate = false;
        propChangeSupport.firePropertyChange("updateDownload", true, false);

    }

    private boolean buildfile() {
        //check if file exist anf rename to something else

        int append = 1;
        String tempname = getName();
        while (true) {
            File tmpfile = new File(getDirectory() + "/" + getName());
            if (tmpfile.exists()) {
                addLogMsg(new String[]{Download.WARNING, "File with same name exist - " + getName()});
                setName(tempname + "-" + append);
                addLogMsg(new String[]{Download.WARNING, "renaming file to - " + getName()});
                append++;
            } else {
                break;
            }
        }

        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(this.getDirectory() + "/" + this.getName(), "rw");

            for (DownloadPart part : this.parts) {
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
