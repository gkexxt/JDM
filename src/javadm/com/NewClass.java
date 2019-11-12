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

/**
 *
 * @author G.K #gkexxt@outlook.com
 */
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipFile;

/**
 * User: zhoujingjie
 * Date: 14-4-18
 * Time: 12:52 this afternoon
 */
public class Downloader extends Observable {
    protected String url, savePath;             //Download and save path
    protected FileChannel channel;              //Save the file path
    protected long size, perSize;              //The size of the file and each small file size
    protected volatile long downloaded;       // The downloaded
    protected int connectCount;                 //The number of connections
    protected Connection[] connections;         //The connection object
    protected boolean isSupportRange;         //Support breakpoint Download
    protected long timeout;                     //Timeout
    protected boolean exists;                   //The existence of
    private RandomAccessFile randomAccessFile;
    protected volatile boolean stop;            //Stop it
    private static volatile  boolean exception; //Whether the abnormal
    private AtomicLong prevDownloaded = new AtomicLong(0); //The last download results
    private static Log log = LogFactory.getLog(Downloader.class);
    private AtomicInteger loseNum = new AtomicInteger(0);
    private int maxThread;

    public Downloader(String url, String savePath) throws IOException {
        //One extra hour
        this(url, savePath, 1000 * 60*5,50);
    }

    public Downloader(String url, String savePath, long timeout,int maxThread) throws FileNotFoundException {
        this.timeout = timeout;
        this.url = url;
        this.maxThread = maxThread;
        File file = new File(savePath);
        if (!file.exists()) file.mkdirs();
        this.savePath= file.getAbsolutePath() + "/" + url.substring(url.lastIndexOf("/"));
        exists = new File(this.savePath).exists();
        if(!exists){
            randomAccessFile=   new RandomAccessFile(this.savePath+".temp", "rw");
            channel =randomAccessFile.getChannel();
        }
    }


    public GetMethod method(long start, long end) throws IOException {
        GetMethod method = new GetMethod(Downloader.this.url);
        method.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36");
        if (end > 0) {
            method.setRequestHeader("Range", "bytes=" + start + "-" + (end - 1));
        } else {
            method.setRequestHeader("Range", "bytes=" + start + "-");
        }
        HttpClientParams clientParams = new HttpClientParams();
        //5 second timeout
        clientParams.setConnectionManagerTimeout(5000);
        HttpClient client = new HttpClient(clientParams);
        client.executeMethod(method);
        int statusCode = method.getStatusCode();
        if (statusCode >= 200 && statusCode <300) {
            isSupportRange = (statusCode == 206) ? true : false;
        }
        return method;
    }

    public void init() throws IOException {
        size = method(0, -1).getResponseContentLength();
        if (isSupportRange) {
            if (size <4 * 1024 * 1024) {  //If less than 4M
                connectCount = 1;
            } else if (size <10 * 1024 * 1024) { //If the file is less than 10M two
                connectCount = 2;
            } else if (size <30 * 1024 * 1024) { //If the file is less than 80M 6 connection is used
                connectCount = 3;
            } else if (size <60 * 1024 * 1024) {          //If less than 60M 10 connection is used
                connectCount = 4;
            } else {
                //Otherwise, the 10 connection
                connectCount = 5;
            }
        } else {
            connectCount = 1;
        }
        log.debug(String.format("%s size:%s connectCount:%s", this.url, this.size, this.connectCount));
        perSize = size / connectCount;
        connections = new Connection[connectCount];
        long offset = 0;
        for (int i = 0; i <connectCount - 1; i++) {
            connections[i] = new Connection(offset, offset + perSize);
            offset += perSize;
        }
        connections[connectCount - 1] = new Connection(offset, size);
    }


    /**
     * Forced to release the memory mapping
     *
     * @param mappedByteBuffer
     */
    static void unmapFileChannel(final MappedByteBuffer mappedByteBuffer) {
        try {
            if (mappedByteBuffer == null) {
                return;
            }
            mappedByteBuffer.force();
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    try {
                        Method getCleanerMethod = mappedByteBuffer.getClass().getMethod("cleaner", new Class[0]);
                        getCleanerMethod.setAccessible(true);
                        sun.misc.Cleaner cleaner = (sun.misc.Cleaner) getCleanerMethod.invoke(mappedByteBuffer, new Object[0]);
                        cleaner.clean();
                    } catch (Exception e) {
                        //LOG.error("unmapFileChannel." + e.getMessage());
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            log.debug("Anomaly>exception=true");
            exception = true;
            log.error(e);
        }
    }


    private void timer() {
        Timer timer = new Timer();
        //A delay of 3 seconds, 3 seconds to run a
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                log.debug(String.format("Download>%s -> %s",(((double) downloaded) / size * 100) + "%", url));
                //If a download size and the current exit
                if(prevDownloaded.get() ==downloaded && downloaded<size){
                    if(loseNum.getAndIncrement()>=10){
                        log.debug(String.format("The last download%s and the current download%s,exception->true  url:%s ",prevDownloaded.get(),downloaded,url));
                        exception = true;
                    }
                }
                //If the download is complete or abnormal exit
                if(downloaded>=size || exception){
                    stop = true;
                    cancel();
                }
                //Set the last download size is equal to the size of the now
                prevDownloaded.set(downloaded);
            }
        },3000,3000);
    }

    public void start() throws IOException {
        if (exists) {
            log.info("File already exists" + this.url);
            Thread.currentThread().interrupt();
            return;
        }
        while (Thread.activeCount()>maxThread){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
        }
        init();
        timer();
        CountDownLatch countDownLatch = new CountDownLatch(connections.length);
        log.debug("To start the download:" + url);
        for (int i = 0; i <connections.length; i++) {
            new DownloadPart(countDownLatch, i).start();
        }
        end(countDownLatch);
    }

    private boolean rename(File tempFile){
        File file = new File(this.savePath);
        boolean isRename=tempFile.renameTo(file);
        if(!isRename){
            try {
                IOUtils.copy(new FileInputStream(tempFile),new FileOutputStream(file));
            } catch (IOException e) {
                log.error(e);
            }
        }
        return true;
    }

    public void end(CountDownLatch countDownLatch){
        try {
            //More than the specified time directly to the end
            countDownLatch.await(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            exception = true;
            log.error(e);
            log.info("Failed to download:"+this.url);
        } finally {
            try {
                channel.force(true);
                channel.close();
                randomAccessFile.close();
            } catch (IOException e) {
                log.error(e);
            }
            File temp = new File(this.savePath+".temp");
            log.debug(String.format("%s  %s", exception, this.url));
            //If there is abnormal, delete temporary files downloaded
            if(exception){
                if(!temp.delete()){
                    if(temp!=null)temp.delete();
                }
            }else{
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {}
                rename(temp);
                setChanged();
                notifyObservers(this.url);
                log.info("Download success:"+this.url);
            }
        }
    }



    private class Connection {
        long start, end;

        public Connection(long start, long end) {
            this.start = start;
            this.end = end;
        }

        public InputStream getInputStream() throws IOException {
            return method(start, end).getResponseBodyAsStream();
        }
    }


    private class DownloadPart implements Runnable {
        CountDownLatch countDownLatch;
        int i;

        public DownloadPart(CountDownLatch countDownLatch, int i) {
            this.countDownLatch = countDownLatch;
            this.i = i;
        }
        public void start() {
            new Thread(this).start();
        }
        @Override
        public void run() {
            MappedByteBuffer buffer = null;
            InputStream is = null;
            try {
                is = connections[i].getInputStream();
                buffer = channel.map(FileChannel.MapMode.READ_WRITE, connections[i].start, connections[i].end - connections[i].start);
                byte[] bytes = new byte[4 * 1024];
                int len;
                while ((len = is.read(bytes)) != -1 && !exception && !stop) {
                    buffer.put(bytes, 0, len);
                    downloaded+= len;
                }
                log.debug(String.format("file block had downloaded.%s %s",i,url));
            } catch (IOException e) {
                log.error(e);
            } finally {
                unmapFileChannel(buffer);
                if(buffer != null)buffer.clear();
                if (is != null) try {
                    is.close();
                } catch (IOException e) {
                }
                countDownLatch.countDown();
            }
        }
    }


}
