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
import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author gkalianan
 */
public class Downloader implements Runnable, PropertyChangeListener {

    private List<DownloadPart> downloadParts;
    private List<DownloadPart> xCompletePart;
    private List<DownloadPart> inQuePart;
    private List<DownloadPart> allxCompletePart;
    private DownloadPart rpart = new DownloadPart();
    Thread tDownloader;
    Download download;
    public static final long CONERR = -2;
    private volatile boolean stopWorker;
    private volatile boolean downloading = false;
    private int workerThreadCount = 0;
    private int retryCount = 0;
    private int errorCount = 0;
    private static int downloadercount = 0;
    private volatile boolean workerDownloading = true;

    public Downloader(Download download) {
        downloadercount++;
        //System.err.println(downloadercount);
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

        //check director exist.
        File tmpDir = new File(download.getDirectory());
        if (!(tmpDir.exists() && tmpDir.isDirectory())) {
            download.setState(Download.STERROR);
            download.addLogMsg(new String[]{Download.ERROR, "Invalid Directory - " + download.getDirectory()});
            download.updateDownload();
        };

        //if part is not initialized
        if (download.getType() == Download.UNKNOWN) {
            System.out.println("javadm.com.Downloader init part");

            String accept_ranges = "";
            try {

                URL urlTemp;

                urlTemp = new URL(download.getUrl());
                HttpURLConnection conn = (HttpURLConnection) urlTemp.openConnection();
                conn.setRequestProperty("User-Agent", download.getUserAgent());            // connect to server
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
                //System.err.println(download.getUrl());
                conn.connect();

                accept_ranges = conn.getHeaderField("Accept-Ranges");
                //System.err.println("get header");
                download.setFileSize(conn.getContentLengthLong());
                //System.err.println("getfilesize");
                conn.disconnect();
                //System.err.println("disconnect");
                //shit server may return diffrent content-length on every connectionn
                if (accept_ranges != null && accept_ranges.equalsIgnoreCase("bytes")) {
                    //open a second connection to check the file length again
                    urlTemp = new URL(download.getUrl());
                    HttpURLConnection conn2 = (HttpURLConnection) urlTemp.openConnection();
                    conn2.setRequestProperty("User-Agent", download.getUserAgent());            // connect to server
                    conn2.setConnectTimeout(3000);
                    conn2.setReadTimeout(3000);
                    conn2.connect();
                    long fsize2 = conn2.getContentLengthLong();
                    conn.disconnect();

                    //server returning difrent file length make the download Download.DYNAMIC
                    if (download.getFileSize() != fsize2) {
                        accept_ranges = null;
                        download.setFileSize(-1);

                    }
                }

            } catch (Exception ex) {
                download.setDownloadSize(Downloader.CONERR);
                download.addLogMsg(new String[]{Download.ERROR, ex.toString()});
                download.setState(Download.STERROR);
                download.updateDownload();
                return;//exut when error connecting
            }

            if (accept_ranges != null && accept_ranges.equalsIgnoreCase("bytes") && download.getFileSize() > 0) {
                download.setType(Download.RESUMABLE);
            } else if (accept_ranges == null && download.getFileSize() > 0) {
                download.setType(Download.NON_RESUMEABLE);
            } else if (download.getFileSize() < 0) {
                download.setType(Download.DYNAMIC);
            }
            download.initParts();
            download.addLogMsg(new String[]{Download.INFO, "Initializing - New Download"});
        }

        this.downloadParts = download.getParts();

        for (DownloadPart part : this.downloadParts) {
            if (part.getCurrentSize() < part.getSize()) {
                allxCompletePart.add(part);
                xCompletePart.add(part);
            }
        }

        stopWorker = false;
        downloading = true;
        ///System.out.println("javadm.com.Downloader.run() -- part length  " + allxCompletePart.size());
        download.setState(Download.STDOWNLOADING);
        long start_time = 0;
        long finish_time =0;
        while (true) {            
            download.setElapsed(download.getElapsed()+ (finish_time - start_time));
            start_time = System.currentTimeMillis();
            if (!downloading || !download.isRunning() || retryCount > download.getRetry() * download.getConnections() - 1) {
                //System.out.println("javadm.com.DownloadWorker.downloader exit");
                stopWorker = true;
                if (getWorkerThreadCount() < 1) {
                    if (retryCount > download.getRetry() * download.getConnections() - 1) {
                        download.addLogMsg(new String[]{Download.ERROR, "Stopping download - too many gummy bears"});
                        download.setState(Download.STERROR);
                    }
                    download.updateDownload();
                    
                    break;
                }

            } else {
                if (xCompletePart.size() > 0 && inQuePart.size() < 1) {
                    inQuePart.add(xCompletePart.get(0));
                    xCompletePart.remove(0);
                }

                if (workerThreadCount < download.getConnections() && inQuePart.size() > 0) {
                    //System.out.println("Spawning new thread");
                    workerThreadCount = workerThreadCount + 1;
                    DownloadWorker dt = new DownloadWorker(inQuePart.get(0), workerThreadCount);
                    inQuePart.remove(0);
                    dt.addPropertyChangeListener(this);
                    dt.startDworker();
                }

                if (workerThreadCount < 1) {
                    boolean completeParts = true;
                    for (int i = 0; i < allxCompletePart.size(); i++) {
                        if (!allxCompletePart.get(i).isCompleted()) {
                            completeParts = false;
                        }
                    }
                    //flag exit downloader
                    downloading = !completeParts;
                }

                try {
                    Thread.sleep(20);
                } catch (InterruptedException ex) {
                    download.addLogMsg(new String[]{Download.ERROR, ex.toString()});
                }

            }
            
            finish_time = System.currentTimeMillis();
        }

        System.err.println(download.isComplete());
        System.err.println(download.getState());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        rpart = (DownloadPart) evt.getNewValue();
        switch (download.getType()) {

            case Download.RESUMABLE:
                if (rpart.getCurrentSize() < rpart.getSize()) {
                    errorCount++;
                    //if server only accept single connection -> continue as long we have single conn active
                    if (workerDownloading && errorCount >= download.getRetry() * download.getConnections()) {
                        retryCount = 0;
                        //download.setConnections(1);
                    } else {
                        retryCount++;
                    }
                    inQuePart.add(rpart);
                } else {
                    rpart.setCompleted(true);
                    retryCount = 0;
                    //check if all parts are completed if true flag exit downloader

                }

                break;

            case Download.DYNAMIC:
                //part size is 0 retry till max count
                if (rpart.getCurrentSize() < 1) {
                    //remove old file
                    try {
                        File file = new File(download.getDirectory() + "/" + rpart.getPartFileName());
                        file.delete();
                    } catch (Exception ex) {
                    }
                    rpart.setCurrentSize(0); //redownload the file
                    inQuePart.add(rpart);
                    retryCount++;
                } else {
                    rpart.setCompleted(true);
                    downloading = false;
                }

                break;

            case Download.NON_RESUMEABLE:
                //download error file size is smaller
                if (rpart.getCurrentSize() < rpart.getSize()) {
                    //remove old file                   
                    try {
                        File file = new File(download.getDirectory() + "/" + rpart.getPartFileName());
                        file.delete();
                    } catch (Exception ex) {
                    }
                    rpart.setCurrentSize(0); //redownload the file
                    inQuePart.add(rpart);
                    retryCount++;
                } else {
                    rpart.setCompleted(true);
                    downloading = false;
                }

                break;

        }

        decThreacount();
    }

    class DownloadWorker implements Runnable {

        private final PropertyChangeSupport propChangeSupport
                = new PropertyChangeSupport(this);
        private String fname;
        Thread tDworker;
        long done_size = 0;
        int BUFFER_SIZE = 4092;
        private DownloadPart part;
        private int connection_id;

        public DownloadWorker(DownloadPart part, int connection_id) {
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

                if (download.getType() == Download.RESUMABLE) {
                    long rstartbyte = part.getStartByte() + part.getCurrentSize();
                    String byteRange = rstartbyte + "-" + part.getEndByte();
                    conn.setRequestProperty("Range", "bytes=" + byteRange);
                    //System.out.println("bytes=" + byteRange);
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(10000);
                } else {
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(60000);
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
                    download.addLogMsg(new String[]{Download.INFO, "File : "
                        + part.getPartFileName() + " downloading"});

                    while (!stopWorker && ((numRead = in.read(data, 0, BUFFER_SIZE)) != -1)) {
                        workerDownloading = true;
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
                    workerDownloading = false;
                    Thread.sleep(2000);
                    download.addLogMsg(new String[]{Download.ERROR, "Connection : "
                        + connection_id + " Connection Error - code : " + responsecode});
                    System.err.println("Part " + part.getCurrentSize() + " : " + part.getSize() + " > " + part.getStartByte() + " > " + part.getEndByte());
                    System.err.println(part.isCompleted());

                    propChangeSupport.firePropertyChange("error", "error :" + responsecode, part);

                }
            } catch (Exception ex) {
                workerDownloading = false;
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex1) {
                }
                download.addLogMsg(new String[]{Download.ERROR, "Connection : "
                    + connection_id + " " + ex.toString()});
                propChangeSupport.firePropertyChange("error", ex.toString(), part);

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

        }

    }

}
