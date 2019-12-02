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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gkalianan
 */
public class Downloader implements Runnable {

    private List<DownloadPart> downloadParts;
    private final List<DownloadPart> xCompletePart;
    private Thread tDownloader;
    private final Download download;
    public static final long CONERR = -2;
    private volatile boolean stopWorker;
    private volatile boolean downloading = false;
    private int workerThreadCount = 0;
    private int errorCount = 0;
    private volatile boolean workerDone = true;
    private long filesize;
    private long filesize2;

    /**
     * Downloader thread to manage all the workers thread
     * @param download
     */
    public Downloader(Download download) {
        this.download = download;
        this.xCompletePart = new ArrayList<>();
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
            return;
        }

        //if part is not initialized
        if (download.getType() == Download.UNKNOWN) {
            System.out.println("javadm.com.Downloader init part");

            String accept_ranges ;
            try {

                URL urlTemp;

                urlTemp = new URL(download.getUrl());
                HttpURLConnection conn = (HttpURLConnection) urlTemp.openConnection();
                conn.setRequestProperty("User-Agent", download.getUserAgent());            // connect to server
                conn.setRequestMethod("HEAD");
                conn.setConnectTimeout(60000);
                conn.setReadTimeout(60000);
                System.err.println(download.getUrl());
                conn.connect();
                accept_ranges = conn.getHeaderField("Accept-Ranges");
                filesize = conn.getContentLengthLong();
                System.err.println(conn.getRequestMethod());
                conn.disconnect();
                conn = null;
                //shit server may return diffrent content-length on every connectionn

                //open a second connection to check the file length again
                urlTemp = new URL(download.getUrl());
                HttpURLConnection conn2 = (HttpURLConnection) urlTemp.openConnection();
                conn2.setRequestProperty("User-Agent", download.getUserAgent());            // connect to server
                conn2.setRequestMethod("HEAD");
                conn2.setConnectTimeout(60000);
                conn2.setReadTimeout(60000);
                conn2.connect();
                filesize2 = conn2.getContentLengthLong();
                conn2.disconnect();

                //server returning difrent file length make the download Download.DYNAMIC
                if (filesize != filesize2) {
                    accept_ranges = null;
                    filesize = -1;

                }

            } catch (Exception ex) {
                download.setDownloadSize(Downloader.CONERR);
                download.addLogMsg(new String[]{Download.ERROR, ex.toString()});
                download.setState(Download.STERROR);
                download.updateDownload();
                return;//exit when error connecting
            }

            if (accept_ranges != null && accept_ranges.equalsIgnoreCase("bytes") && filesize > 0) {
                download.setType(Download.RESUMABLE);
                download.setFileSize(filesize);
            } else if (accept_ranges == null && filesize > 0) {
                download.setType(Download.NON_RESUMEABLE);
                download.setFileSize(filesize);
            } else if (filesize < 0) {
                download.setType(Download.DYNAMIC);
                download.setFileSize(-1);
            }

            System.err.println("Type : " + download.getType());
            System.err.println(download.getFileSize());

            download.initParts();
            System.out.println("javadm.com.Downloader.run()");
            download.addLogMsg(new String[]{Download.INFO, "Initializing - New Download"});
            System.out.println("javadm.com.Downloader.run()");
            System.err.println(download.getParts().size());
        }

        this.downloadParts = download.getParts();

        switch (download.getType()) {
            case Download.RESUMABLE:
                for (DownloadPart part : this.downloadParts) {
                    if (part.getCurrentSize() < part.getSize()) {
                        xCompletePart.add(part);
                    }
                }
                break;
            case Download.NON_RESUMEABLE:
                for (DownloadPart part : this.downloadParts) {
                    xCompletePart.add(part);
                }
                break;
            case Download.DYNAMIC:
                for (DownloadPart part : this.downloadParts) {
                    xCompletePart.add(part);
                }
                break;
            default:
                download.addLogMsg(new String[]{Download.STERROR, "Unknown Download Type"});
                return;
        }

        stopWorker = false;
        download.setState(Download.STDOWNLOADING);
        long start_time = 0;
        long finish_time = 0;
        List<DownloadWorker> worker_list = new ArrayList<>();

        while (true) {
            download.setElapsed(download.getElapsed() + (finish_time - start_time));
            start_time = System.currentTimeMillis();

            if (!download.isRunning() || errorCount > download.getRetry() * download.getConnections() - 1) {
                stopWorker = true;
                workerDone = true;
                for (DownloadWorker worker : worker_list) {
                    if (!(worker.getState().equals(DownloadWorker.STERROR)
                            || worker.getState().equals(DownloadWorker.STDONE)
                            || worker.getState().equals(DownloadWorker.STCONNECTING))) {
                        workerDone = false;
                    }

                }

                if (workerDone) {
                    if (errorCount > download.getRetry() * download.getConnections() - 1) {
                        download.addLogMsg(new String[]{Download.ERROR, "Stopping download - too many gummy bears"});
                        download.setState(Download.STERROR);
                    }
                    download.updateDownload();
                    break;
                }

            } else {

                if (worker_list.size() < download.getConnections() && xCompletePart.size() > 0) {
                    DownloadWorker worker = new DownloadWorker(xCompletePart.get(0), worker_list.size());
                    xCompletePart.remove(0);
                    worker_list.add(worker);
                    worker.startDworker();
                }

                for (int i = 0; i < worker_list.size(); i++) {
                    DownloadWorker worker = worker_list.get(i);
                    switch (worker.getState()) {
                        case DownloadWorker.STDONE:
                            worker_list.remove(i);
                            break;
                        case DownloadWorker.STERROR:
                            xCompletePart.add(worker.getPart());
                            worker_list.remove(i);
                            errorCount++;
                            break;
                        case DownloadWorker.STDOWNLOADING:
                            //if server only allow single connection per-download
                            if (errorCount > download.getRetry() * download.getConnections() - 2) {
                                errorCount = 0;
                                download.setConnections(1);
                            }
                            break;
                        default:
                            break;
                    }

                }

                //download complete
                if (worker_list.size() < 1 && xCompletePart.size() < 1) {
                    download.updateDownload();
                    break;
                }

                try {
                    Thread.sleep(20);
                } catch (InterruptedException ex) {
                    download.addLogMsg(new String[]{Download.ERROR, ex.toString()});
                }

            }

            finish_time = System.currentTimeMillis();
        }

    }

    class DownloadWorker implements Runnable {

        static final String STERROR = "ERROR";
        static final String STDONE = "DONE";
        static final String STCONNECTING = "CONNECTING";
        static final String STDOWNLOADING = "DOWNLOADING";
        private String fname;
        private String state = "";
        private boolean onerror;

        Thread tDworker;
        long done_size = 0;

        int BUFFER_SIZE = 4092;
        private final DownloadPart part;

        private final int connection_id;
        private volatile boolean worker_connected = false;

        public DownloadWorker(DownloadPart part, int connection_id) {
            this.onerror = false;
            this.part = part;
            this.connection_id = connection_id;
        }

        public DownloadPart getPart() {
            return part;
        }

        public String getState() {
            synchronized (this) {
                return this.state;
            }
        }

        public void setState(String state) {
            this.state = state;
        }

        public boolean isWorker_connected() {
            return worker_connected;
        }

        public void setWorker_connected(boolean worker_connected) {
            this.worker_connected = worker_connected;
        }

        public void startDworker() {
            tDworker = new Thread(this);
            tDworker.start();
        }

        @Override
        public void run() {
            BufferedInputStream in = null;
            RandomAccessFile raf = null;
            try {
                // open Http connection to URL
                setState(STCONNECTING);
                URL url = new URL(download.getUrl());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if (download.getType() == Download.RESUMABLE) {
                    long rstartbyte = part.getStartByte() + part.getCurrentSize();
                    String byteRange = rstartbyte + "-" + part.getEndByte();
                    conn.setRequestProperty("Range", "bytes=" + byteRange);
                    System.err.println(byteRange);
                }
                conn.setConnectTimeout(30000);//some server take long time for initial connection
                conn.setReadTimeout(5000);

                conn.setRequestProperty("User-Agent", download.getUserAgent());
                conn.connect();

                int responsecode = conn.getResponseCode();
                // Make sure the response code is in the 200 range.
                if (responsecode / 100 == 2) {
                    setState(STDOWNLOADING);
                    download.addLogMsg(new String[]{Download.INFO, "Connection "
                        + connection_id + " Established"});
                    //set part size
                    // get the input stream
                    in = new BufferedInputStream(conn.getInputStream());

                    // open the output file and seek to the stopWorker location
                    fname = download.getDirectory() + "/" + part.getPartFileName();
                    raf = new RandomAccessFile(fname, "rw");
                    raf.seek(part.getCurrentSize());
                    byte data[] = new byte[BUFFER_SIZE];
                    int numRead;
                    download.addLogMsg(new String[]{Download.INFO, "File : "
                        + part.getPartFileName() + " downloading"});
                    while (!stopWorker && ((numRead = in.read(data, 0, BUFFER_SIZE)) != -1)) {
                        raf.write(data, 0, numRead);
                        part.setCurrentSize(part.getCurrentSize() + numRead);

                        try {

                            download.setProgress(numRead);
                        } catch (Exception ex) {
                            download.addLogMsg(new String[]{Download.ERROR, "Connection : "
                                + connection_id + " " + ex.toString()});
                        }

                    }

                } else {
                    onerror = true;
                    Thread.sleep(2000);
                    download.addLogMsg(new String[]{Download.ERROR, "Connection : "
                        + connection_id + " Connection Error - code : " + responsecode});

                }
            } catch (Exception ex) {
                onerror = true;
                download.addLogMsg(new String[]{Download.ERROR, "Connection : "
                    + connection_id + " " + ex.toString()});
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex1) {
                }

            } finally {

                if (raf != null) {
                    try {
                        raf.close();
                    } catch (Exception ex) {
                        onerror = true;
                        download.addLogMsg(new String[]{Download.ERROR, "Connection : "
                            + connection_id + " " + ex.toString()});
                    }
                }

                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception ex) {
                        onerror = true;
                        download.addLogMsg(new String[]{Download.ERROR, "Connection : "
                            + connection_id + " " + ex.toString()});
                    }
                }

            }

            if (onerror) {
                setState(STERROR);
            } else {
                DaoSqlite db = new DaoSqlite();
                db.updatePart(download.getId(), getPart());
                if (getPart().getCurrentSize() >= getPart().getSize()) {
                    download.addLogMsg(new String[]{Download.INFO,
                        "File : " + getPart().getPartFileName() + " complete"});
                    setState(STDONE);
                } else {
                    setState(STERROR);
                }
            }

        }

    }

}
