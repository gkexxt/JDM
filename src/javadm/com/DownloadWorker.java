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
public class DownloadWorker implements Runnable, PropertyChangeListener {

    private List<Part> downloadParts;
    private List<Part> xCompletePart;
    private List<Part> inQuePart;
    private Part rpart = new Part();

    Thread t;
    String name;
    Download download;

    public static final long CONERR = -2;
    private volatile boolean start;
    private volatile boolean downloading = false;

    private int threacount = 0;

    DownloadWorker(Download download) {
        this.download = download;

        this.inQuePart = new ArrayList<>();
        this.xCompletePart = new ArrayList<>();
        //System.out.println("New thread: " + t);
    }

    public int getThreacount() {
        return threacount;
    }

    public synchronized void decThreacount() {
        this.threacount = this.threacount - 1;
    }

    private synchronized void setDownloading(boolean downloading) {
        this.downloading = downloading;
    }

    public boolean isDownloading() {
        return downloading;
    }

    public void startx() {
        t = new Thread(this);
        //System.out.println("javadm.com.DownloadWorker.Downloader.start()");
        t.start();

    }

    @Override
    public void run() {
        start = true;
        long filesize = -2;
        try {
            URL urlTemp;
            urlTemp = new URL(download.getData().getUrl());
            HttpURLConnection conn = (HttpURLConnection) urlTemp.openConnection();
            conn.setRequestProperty("User-Agent", download.getUserAgent());            // connect to server
            conn.connect();
            download.setDownloadSize(conn.getContentLengthLong());
            conn.disconnect();
        } catch (Exception ex) {
            download.setDownloadSize(DownloadWorker.CONERR);
            download.addLogMsg(new String[]{ex.getMessage(), ex.toString()});

        }

        if (download.getData().getFileSize() < -1) {
            // System.err.println("----------------");
            return;
        }

        download.initParts();
        System.err.println(download.getData().getName());

        this.downloadParts = download.getParts();
        int loopCount = 0;
        //System.err.println("----------------");
        for (int i = 0; i < downloadParts.size(); i++) {
            if (!downloadParts.get(i).isCompleted()) {
                xCompletePart.add(downloadParts.get(i));

            }

        }
        //
        while (download.isStart()) {
            //System.err.println("controller running");

            //System.err.println(" Thcount : " + threacount);
            //System.err.println(inQuePart.size());
            if (loopCount > 200 && !downloading && false) {
                System.out.println("javadm.com.DownloadWorker.downloading timeout");
                start = false;
                //;
                if (getThreacount() < 0) {
                    download.setStart(false);
                    break;
                }

            } else {

                if (xCompletePart.size() > 0 && inQuePart.size() < 1) {
                    inQuePart.add(xCompletePart.get(0));
                    xCompletePart.remove(0);
                }

                if (threacount < download.getData().getConnections() && inQuePart.size() > 0) {
                    System.out.println("Spawning new thread");
                    Downloader dt = new Downloader(inQuePart.get(0));
                    inQuePart.remove(0);
                    dt.addPropertyChangeListener(this);
                    dt.startx();
                    System.out.println("Spawning new thread --- ");
                    threacount = threacount + 1;

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
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        rpart = (Part) evt.getNewValue();
        if (evt.getPropertyName().equals("error")) {

            if (rpart.getCurrentSize() < rpart.getPartSize()) {
                inQuePart.add(rpart);
            } else {
                rpart.setCompleted(true);
            }
            decThreacount();
            //threacount = threacount - 1;

        }

        if (evt.getPropertyName().equals("tend")) {

            if (rpart.getCurrentSize() < rpart.getPartSize()) {
                inQuePart.add(rpart);
            } else {
                rpart.setCompleted(true);
            }
            decThreacount();

            // threacount = threacount - 1;
            //inCompParts.add((Download.Part) evt.getNewValue());
        }

        System.err.println("rpart : " + rpart.getPartFileName() + " : " + rpart.getCurrentSize());

    }

    class Downloader implements Runnable {

        private final PropertyChangeSupport propChangeSupport
                = new PropertyChangeSupport(this);
        private String fname;
        Thread tx;
        long done_size = 0;
        int BUFFER_SIZE = 16368;
        private Part part;

        public Downloader(Part part) {
            this.part = part;

        }

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            propChangeSupport.addPropertyChangeListener(listener);
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
            propChangeSupport.removePropertyChangeListener(listener);
        }

        public void startx() {
            tx = new Thread(this);
            System.out.println("javadm.com.DownloadWorker.Downloader.start()");
            tx.start();

        }

        @Override
        public void run() {
            //System.out.println("javadm.com.Downloader.run()");
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
                conn.setReadTimeout(5000);
                conn.connect();
                System.out.println("javadm.com.DownloadWorker.Downloader.run() + after connect ");
                int responsecode = conn.getResponseCode();
                System.err.println("Response code : " + responsecode);
                // Make sure the response code is in the 200 range.
                if (responsecode / 100 == 2) {
                    System.err.println("Response code xxx: " + responsecode);
                    //set part size
                    //download.getData().setFileSize(conn.getContentLengthLong());
                    // get the input stream
                    in = new BufferedInputStream(conn.getInputStream());

                    // open the output file and seek to the start location
                    fname = download.getData().getDirectory() + "/" + part.getPartFileName();
                    raf = new RandomAccessFile(fname, "rw");
                    raf.seek(part.getCurrentSize());
                    byte data[] = new byte[BUFFER_SIZE];
                    int numRead;

                    while (start && ((numRead = in.read(data, 0, BUFFER_SIZE)) != -1)) {
                        // write to buffer
                        //System.err.println("enter while loop");
                        raf.write(data, 0, numRead);
                        part.setCurrentSize(part.getCurrentSize() + numRead);
                        setDownloading(true);

                        try {

                            download.setProgress(numRead);
                        } catch (Exception ex) {
                            download.addLogMsg(new String[]{Download.WARNING, ex.toString()});
                            //System.out.println("javadm.com.Downloader.run()");
                        }

                    }
                    //if part is still instart mode and loop ended set complete
                    //part.setCompleted(start || (in.read(data, 0, BUFFER_SIZE) != -1));
                    propChangeSupport.firePropertyChange("tend", "", part);
                    System.err.println(part.getPartFileName() + " - done_size : " + part.getCurrentSize());
                    //download.getData().setComplete(download.isStart());

                } else {
                    download.addLogMsg(new String[]{"protocol Error", " http Response error - code : " + responsecode});
                    propChangeSupport.firePropertyChange("error", "error :" + responsecode, part);
                }
            } catch (Exception ex) {
                download.addLogMsg(new String[]{ex.getMessage(), ex.toString()});
                propChangeSupport.firePropertyChange("error", ex.toString(), part);
                //System.out.println("javadm.com.Downloader.run()");
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
                        //System.out.println("javadm.com.Downloader.run()");
                    }
                }

            }

            //propChangeSupport.firePropertyChange("");
            setDownloading(false);
            System.err.println("thread exit");

        }

    }

}
