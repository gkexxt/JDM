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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gkalianan
 */
public class DownloadWorker {

    String name;
    Download download;
    public static final long CONERR = -2;
    private volatile boolean start;
    private volatile boolean downloading = false;

    private int threacount = 0;

    public int getThreacount() {
        return threacount;
    }

    public void startDownloadController() {
        downloadController xxx = new downloadController();
        xxx.start();
    }

    public synchronized void decThreacount() {
        this.threacount = this.threacount - 1;
    }

    DownloadWorker(Download download) {
        this.download = download;
        System.out.println("javadm.com.DownloadWorker.<init>()");
        //System.out.println("New thread: " + t);
    }

    private synchronized void setDownloading(boolean downloading) {
        this.downloading = downloading;
    }

    public boolean isDownloading() {
        return downloading;
    }

    class downloadController implements Runnable, PropertyChangeListener {

        private List<Download.DownloadPart> downloadParts;
        private List<Download.DownloadPart> inCompParts;
        private List<Download.DownloadPart> errParts;
        Thread t;

        public downloadController() {

            this.inCompParts = new ArrayList<>();
        }

        public void start() {
            t = new Thread(this);
            System.out.println("javadm.com.DownloadWorker.Downloader.start()");
            t.start();

        }

        @Override
        public void run() {
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

            if (filesize < -1) {
                return;
            }

            download.initParts();
            this.downloadParts = download.getParts();
            int loopCount = 0;

            for (int i = 0; i < downloadParts.size(); i++) {
                if (!downloadParts.get(i).isCompleted()) {
                    inCompParts.add(downloadParts.get(i));
                }

            }

            while (download.isStart()) {

                if (loopCount > 50 && !downloading) {

                    start = false;
                    download.setStart(false);
                    if (getThreacount() < 0) {
                        break;
                    }

                } else {

                    if (threacount < download.getData().getConnections() && inCompParts.size() > 0) {
                        Downloader dt = new Downloader(inCompParts.get(0));
                        inCompParts.remove(0);
                        dt.addPropertyChangeListener(this);
                        dt.run();

                    }

                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ex) {
                        download.addLogMsg(new String[]{Download.ERROR, ex.toString()});
                    }

                }
            }

        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

            if (evt.toString().equals("error")) {

                threacount = threacount - 1;
                inCompParts.add((Download.DownloadPart) evt.getNewValue());

            }

        }

    }

    class Downloader implements Runnable {

        private final PropertyChangeSupport propChangeSupport
                = new PropertyChangeSupport(this);
        private String fname;
        Thread t;
        int BUFFER_SIZE = 4092;
        private final Download.DownloadPart part;

        public Downloader(Download.DownloadPart part) {
            this.part = part;

        }

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            propChangeSupport.addPropertyChangeListener(listener);
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
            propChangeSupport.removePropertyChangeListener(listener);
        }

        public void start() {
            t = new Thread(this);
            System.out.println("javadm.com.DownloadWorker.Downloader.start()");
            t.start();

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

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                String byteRange = part.getStartByte() + "-" + part.getEndByte();
                conn.setRequestProperty("Range", "bytes=" + byteRange);
                System.out.println("bytes=" + byteRange);
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U;"
                        + download.getUserAgent());
                // connect to server
                conn.connect();

                int responsecode = conn.getResponseCode();

                // Make sure the response code is in the 200 range.
                if (responsecode / 100 == 2) {

                    //set part size
                    //download.getData().setFileSize(conn.getContentLengthLong());
                    // get the input stream
                    in = new BufferedInputStream(conn.getInputStream());

                    // open the output file and seek to the start location
                    fname = download.getData().getDirectory() + "/" + part.getPartFileName();
                    raf = new RandomAccessFile(fname, "rw");

                    byte data[] = new byte[BUFFER_SIZE];
                    int numRead;

                    while (start && ((numRead = in.read(data, 0, BUFFER_SIZE)) != -1)) {
                        // write to buffer
                        raf.write(data, 0, numRead);
                        setDownloading(true);
                        try {

                            download.setProgress(numRead);
                        } catch (Exception ex) {
                            download.addLogMsg(new String[]{Download.WARNING, ex.toString()});
                            //System.out.println("javadm.com.Downloader.run()");
                        }

                    }
                    //if part is still instart mode and loop ended set complete
                    part.setCompleted(start || (in.read(data, 0, BUFFER_SIZE) != -1));
                    propChangeSupport.firePropertyChange("tend", "", this);
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

            decThreacount();
            setDownloading(false);

        }

    }

}
