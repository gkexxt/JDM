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
    private volatile boolean stop;
    private volatile boolean downloading = false;
    private int workerThreadCount = 0;

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
        if (download.getParts().size() < 1 && !download.getData().isComplete()) {

            try {
                URL urlTemp;
                urlTemp = new URL(download.getData().getUrl());
                HttpURLConnection conn = (HttpURLConnection) urlTemp.openConnection();
                conn.setRequestProperty("User-Agent", download.getUserAgent());            // connect to server
                conn.setConnectTimeout(3000);
                conn.setReadTimeout(3000);
                conn.connect();
                download.setDownloadSize(conn.getContentLengthLong());
                conn.disconnect();
            } catch (Exception ex) {
                download.setDownloadSize(Downloader.CONERR);
                download.addLogMsg(new String[]{ex.getMessage(), ex.toString()});
                return;//exut when error connecting
            }

            download.initParts();
        } else {

            return;
        }

        this.downloadParts = download.getParts();
        int loopCount = 0;
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

        stop = false;
        downloading = true;
        while (download.isStart()) {
            if (!downloading) {
                System.out.println("javadm.com.DownloadWorker.downloader exit");
                stop = true;
                if (getWorkerThreadCount() < 1) {
                    download.setStart(false);
                    break;
                }

            } else {

                if (xCompletePart.size() > 0 && inQuePart.size() < 1) {
                    inQuePart.add(xCompletePart.get(0));
                    xCompletePart.remove(0);
                }

                if (workerThreadCount < download.getData().getConnections() && inQuePart.size() > 0) {
                    System.out.println("Spawning new thread");
                    DownloadWorker dt = new DownloadWorker(inQuePart.get(0));
                    inQuePart.remove(0);
                    dt.addPropertyChangeListener(this);
                    dt.startDworker();
                    System.out.println("Spawning new thread --- ");
                    workerThreadCount = workerThreadCount + 1;

                }

                try {
                    Thread.sleep(20);
                } catch (InterruptedException ex) {
                    download.addLogMsg(new String[]{Download.ERROR, ex.toString()});
                }

            }
        }

        System.err.println("controller exit");

    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        rpart = (Part) evt.getNewValue();
        if (rpart.getSize() > 0) {
            if (rpart.getCurrentSize() < rpart.getSize()) {
                inQuePart.add(rpart);
            } else {
                rpart.setCompleted(true);
            }
            decThreacount();
            System.err.println("rpart : " + rpart.getPartFileName() + " : " + rpart.getCurrentSize());
        } else {
            downloading = false; // size -1 download error.
        }

        boolean complete = true;
        for (int i = 0; i < allxCompletePart.size(); i++) {
            if (!allxCompletePart.get(i).isCompleted()) {
                complete = false;
            }
        }

        downloading = !complete;
        System.err.println("Downloading : " + downloading);
        System.err.println("complete : " + complete);
    }

    class DownloadWorker implements Runnable {

        private final PropertyChangeSupport propChangeSupport
                = new PropertyChangeSupport(this);
        private String fname;
        Thread tDworker;
        long done_size = 0;
        int BUFFER_SIZE = 16368;
        private Part part;

        public DownloadWorker(Part part) {
            this.part = part;

        }

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            propChangeSupport.addPropertyChangeListener(listener);
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
            propChangeSupport.removePropertyChangeListener(listener);
        }

        public void startDworker() {
            tDworker = new Thread(this);
            System.out.println("javadm.com.DownloadWorker.Downloader.start()");
            tDworker.start();

        }

        @Override
        public void run() {
            BufferedInputStream in = null;
            RandomAccessFile raf = null;
            try {
                System.out.println("javadm.com.DownloadWorker.Downloader.run()");
                // open Http connection to URL
                URL url = new URL(download.getData().getUrl());
                System.out.println("javadm.com.DownloadWorker.Downloader.run() + ");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                long rstartbyte = part.getStartByte() + part.getCurrentSize();
                String byteRange = rstartbyte + "-" + part.getEndByte();
                conn.setRequestProperty("Range", "bytes=" + byteRange);
                System.out.println("bytes=" + byteRange);
                conn.setRequestProperty("User-Agent", download.getUserAgent());
                // connect to server
                System.out.println("javadm.com.DownloadWorker.Downloader.run() + before connect ");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(10000);
                conn.connect();
                System.out.println("javadm.com.DownloadWorker.Downloader.run() + after connect ");
                int responsecode = conn.getResponseCode();
                System.err.println("Response code : " + responsecode);
                // Make sure the response code is in the 200 range.
                if (responsecode / 100 == 2) {
                    System.err.println("Response code xxx: " + responsecode);
                    //set part size
                    // get the input stream
                    in = new BufferedInputStream(conn.getInputStream());

                    // open the output file and seek to the stop location
                    fname = download.getData().getDirectory() + "/" + part.getPartFileName();
                    raf = new RandomAccessFile(fname, "rw");
                    raf.seek(part.getCurrentSize());
                    byte data[] = new byte[BUFFER_SIZE];
                    int numRead;

                    while (!stop && ((numRead = in.read(data, 0, BUFFER_SIZE)) != -1)) {
                        // write to buffer
                        raf.write(data, 0, numRead);
                        part.setCurrentSize(part.getCurrentSize() + numRead);

                        try {

                            download.setProgress(numRead);
                        } catch (Exception ex) {
                            download.addLogMsg(new String[]{Download.WARNING, ex.toString()});
                        }

                    }

                    propChangeSupport.firePropertyChange("tend", "", part);
                    System.err.println(part.getPartFileName() + " - done_size : " + part.getCurrentSize());

                } else {
                    download.addLogMsg(new String[]{"protocol Error", " http Response error - code : " + responsecode});
                    propChangeSupport.firePropertyChange("error", "error :" + responsecode, part);
                }
            } catch (Exception ex) {
                download.addLogMsg(new String[]{ex.getMessage(), ex.toString()});
                propChangeSupport.firePropertyChange("error", ex.toString(), part);
            } finally {

                if (raf != null) {
                    try {
                        raf.close();
                    } catch (Exception ex) {
                        download.addLogMsg(new String[]{ex.getMessage(), ex.toString()});
                    }
                }

                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception ex) {
                        download.addLogMsg(new String[]{ex.getMessage(), ex.toString()});
                    }
                }

            }

            //propChangeSupport.firePropertyChange("");
            System.err.println("thread exit");

        }

    }

}
