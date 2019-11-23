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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedInputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gkalianan
 */
public class Downloader implements Runnable, PropertyChangeListener {

    private List<Part> downloadParts;
    private List<Part> xCompletePart;
    private List<Part> inQuePart;
    private List<Part> allxCompletePart;
    private Part rpart = new Part();
    Thread tDownloader;
    Download download;
    public static final long CONERR = -2;
    private volatile boolean stopWorker;
    private volatile boolean downloading = false;
    private volatile boolean workerConnected;
    private int workerThreadCount = 0;
    private long errorCount = 0;

    public Downloader(Download download) {
        this.download = download;
        this.inQuePart = new ArrayList<>();
        this.xCompletePart = new ArrayList<>();
        this.allxCompletePart = new ArrayList<>();
    }

    public int getWorkerThreadCount() {
        return workerThreadCount;
    }

    public synchronized void decThreacount() {
        this.workerThreadCount = this.workerThreadCount - 1;
    }

    private synchronized void setDownloading(boolean downloading) {
        this.downloading = downloading;
    }

    public boolean isDownloading() {
        return downloading;
    }

    public void startDownloader() {
        tDownloader = new Thread(this);
        tDownloader.start();
    }

    @Override
    public void run() {

        //is part not initialize   
        if (download.getParts().size() < 1 && !download.isComplete()) {
            String accept_ranges = "";
            try {
                URL urlTemp;
                urlTemp = new URL(download.getUrl());
                HttpURLConnection conn = (HttpURLConnection) urlTemp.openConnection();
                conn.setRequestProperty("User-Agent", download.getUserAgent());            // connect to server
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
                conn.connect();
                accept_ranges = conn.getHeaderField("Accept-Ranges");
                download.setDownloadSize(conn.getContentLengthLong());
                conn.disconnect();
            } catch (Exception ex) {
                download.setDownloadSize(Downloader.CONERR);
                download.addLogMsg(new String[]{ex.getMessage(), ex.toString()});
                return;//exut when error connecting
            }
            if (accept_ranges != null && accept_ranges.equalsIgnoreCase("bytes") && download.getFileSize() > 0) {
                download.initParts(Download.RESUMABLE);
            } else if (accept_ranges == null && download.getFileSize() > 0) {
                download.initParts(Download.NON_RESUMEABLE);
            } else if (download.getFileSize() < 0) {
                download.initParts(Download.DYNAMIC);
            }

            download.addLogMsg(new String[]{Download.INFO, "Initializing - New Download"});
        } else if (download.getParts().size() > 0 && !download.isComplete()) {
            download.addLogMsg(new String[]{Download.INFO, "Resuming - Download"});

        } else {
            download.addLogMsg(new String[]{Download.INFO, "Completed download or no parts"});
            return;
        }

        this.downloadParts = download.getParts();
        for (int i = 0; i < downloadParts.size(); i++) {
            if (!downloadParts.get(i).isCompleted()) {
                allxCompletePart.add(downloadParts.get(i));
                xCompletePart.add(downloadParts.get(i));
            }
        }

        if (xCompletePart.size() < 1) {
            download.addLogMsg(new String[]{Download.INFO, "No parts to download"});
            return;
        }

        stopWorker = false;
        downloading = true;
        while (true) {

            if (!downloading || !download.isStart() || errorCount > 200) {
                System.out.println("javadm.com.DownloadWorker.downloader exit");
                stopWorker = true;
                if (getWorkerThreadCount() < 1) {
                    download.setStart(false);
                    if (errorCount > 200) {
                        download.addLogMsg(new String[]{Download.ERROR, "Stopping download - too many gummy bears"});
                    }
                    break;
                }

            } else {

                if (xCompletePart.size() > 0 && inQuePart.size() < 1) {
                    inQuePart.add(xCompletePart.get(0));
                    xCompletePart.remove(0);
                }

                if (workerThreadCount < download.getConnections() && inQuePart.size() > 0) {
                    workerConnected = false;
                    System.out.println("Spawning new thread");
                    workerThreadCount = workerThreadCount + 1;
                    DownloadWorker dt = new DownloadWorker(inQuePart.get(0), workerThreadCount);
                    inQuePart.remove(0);
                    dt.addPropertyChangeListener(this);
                    dt.startDworker();
                    System.out.println("Spawning new thread --- ");

                }

                try {
                    Thread.sleep(20);
                } catch (InterruptedException ex) {
                    download.addLogMsg(new String[]{Download.ERROR, ex.toString()});
                }

            }
        }

        //System.err.println("controller exit");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        rpart = (Part) evt.getNewValue();
        if (rpart.getType() == Download.RESUMABLE) {
            if (rpart.getCurrentSize() < rpart.getSize()) {
                if (rpart.getCurrentSize() > 0) {
                    DaoSqlite db = new DaoSqlite();
                    db.updatePart(download.getId(), rpart);
                    inQuePart.add(rpart);
                }
            } else {
                rpart.setCompleted(true);
                DaoSqlite db = new DaoSqlite();
                db.updatePart(download.getId(), rpart);
            }
            decThreacount();
            //System.err.println("rpart : " + rpart.getPartFileName() + " : " + rpart.getCurrentSize());
        } else {
            downloading = false; // size -1 download error.
        }

        boolean completeParts = true;
        for (int i = 0; i < allxCompletePart.size(); i++) {
            if (!allxCompletePart.get(i).isCompleted()) {
                completeParts = false;
            }
        }

        downloading = !completeParts;
        //System.err.println("Downloading : " + downloading);
        //System.err.println("complete : " + completeParts);
    }

    class DownloadWorker implements Runnable {

        private final PropertyChangeSupport propChangeSupport
                = new PropertyChangeSupport(this);
        private String fname;
        Thread tDworker;
        long done_size = 0;
        int BUFFER_SIZE = 4092;
        private Part part;
        private int connection_id;

        public DownloadWorker(Part part, int connection_id) {
            this.part = part;
            this.connection_id = connection_id;

        }

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            propChangeSupport.addPropertyChangeListener(listener);
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
            propChangeSupport.removePropertyChangeListener(listener);
        }

        public void startDworker() {
            tDworker = new Thread(this);
            //System.out.println("javadm.com.DownloadWorker.Downloader.start()");
            tDworker.start();

        }

        @Override
        public void run() {
            BufferedInputStream in = null;
            RandomAccessFile raf = null;
            try {
                // System.out.println("javadm.com.DownloadWorker.Downloader.run()");
                // open Http connection to URL
                URL url = new URL(download.getUrl());
                //System.out.println("javadm.com.DownloadWorker.Downloader.run() + ");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if (part.getType() == Download.RESUMABLE) {
                    long rstartbyte = part.getStartByte() + part.getCurrentSize();
                    String byteRange = rstartbyte + "-" + part.getEndByte();
                    conn.setRequestProperty("Range", "bytes=" + byteRange);
                    //System.out.println("bytes=" + byteRange);
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(60000);
                } else {
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(180000);
                }
                conn.setRequestProperty("User-Agent", download.getUserAgent());
                // connect to server
                //System.out.println("javadm.com.DownloadWorker.Downloader.run() + before connect ");

                conn.connect();

                int responsecode = conn.getResponseCode();
                //System.err.println("Response code : " + responsecode);
                // Make sure the response code is in the 200 range.
                if (responsecode / 100 == 2) {
                    download.addLogMsg(new String[]{Download.INFO, "Connection "
                        + connection_id + " Established"});
                    //System.err.println("Response code xxx: " + responsecode);
                    //set part size
                    // get the input stream
                    in = new BufferedInputStream(conn.getInputStream());

                    // open the output file and seek to the stopWorker location
                    fname = download.getDirectory() + "/" + part.getPartFileName();
                    raf = new RandomAccessFile(fname, "rw");
                    raf.seek(part.getCurrentSize());
                    byte data[] = new byte[BUFFER_SIZE];
                    int numRead;
                    //System.out.println("javadm.com.DownloadWorker.Downloader.run() + after connect ");
                    workerConnected = true;
                    download.addLogMsg(new String[]{Download.INFO, "File : "
                        + part.getPartFileName() + " downloading"});

                    while (!stopWorker && ((numRead = in.read(data, 0, BUFFER_SIZE)) != -1)) {
                        // write to buffer
                        errorCount = 0;
                        raf.write(data, 0, numRead);
                        part.setCurrentSize(part.getCurrentSize() + numRead);

                        try {

                            download.setProgress(numRead);
                        } catch (Exception ex) {
                            download.addLogMsg(new String[]{Download.ERROR, "Connection : "
                                + connection_id + " " + ex.toString()});
                        }

                    }
                    download.addLogMsg(new String[]{Download.INFO,
                        "File : " + part.getPartFileName() + " complete"});

                    propChangeSupport.firePropertyChange("tend", "", part);
                    //System.err.println(part.getPartFileName() + " - done_size : " + part.getCurrentSize());

                } else {
                    Thread.sleep(2000);
                    download.addLogMsg(new String[]{Download.ERROR, "Connection : "
                        + connection_id + " Connection Error - code : " + responsecode});
                    propChangeSupport.firePropertyChange("error", "error :" + responsecode, part);
                    errorCount++;
                }
            } catch (Exception ex) {

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex1) {

                }

                download.addLogMsg(new String[]{Download.ERROR, "Connection : "
                    + connection_id + " " + ex.toString()});
                propChangeSupport.firePropertyChange("error", ex.toString(), part);
                errorCount++;
            } finally {

                if (raf != null) {
                    try {
                        raf.close();
                    } catch (Exception ex) {
                        download.addLogMsg(new String[]{Download.ERROR, "Connection : "
                            + connection_id + " " + ex.toString()});
                    }
                }

                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception ex) {
                        download.addLogMsg(new String[]{Download.ERROR, "Connection : "
                            + connection_id + " " + ex.toString()});
                    }
                }

            }

            //propChangeSupport.firePropertyChange("");
            //System.err.println("thread exit");
        }

    }

}
