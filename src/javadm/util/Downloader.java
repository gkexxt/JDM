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
package javadm.util;

import java.io.BufferedInputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import javadm.com.Download;

/**
 *
 * @author G.K #gkexxt@outlook.com
 */
public class Downloader {

    private String fname;
    int BUFFER_SIZE = 4092;
    private Download download;

    public Downloader(Download download) {
        this.download = download;

    }

    public void run() {
        //System.out.println("javadm.com.Downloader.run()");
        BufferedInputStream in = null;
        RandomAccessFile raf = null;
      try {
            // open Http connection to URL
            URL url = new URL(download.getData().getUrl());
            
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U;"
                    + " Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            // connect to server
            conn.connect();
            
            int responsecode = conn.getResponseCode();
  
            // Make sure the response code is in the 200 range.
            if (responsecode / 100 == 2) {
                
                //set download size
                download.getData().setFileSize(conn.getContentLengthLong());
                // get the input stream
                in = new BufferedInputStream(conn.getInputStream());

                // open the output file and seek to the start location
                fname = download.getData().getDirectory() + "/" + download.getData().getName();
                raf = new RandomAccessFile(fname, "rw");

                byte data[] = new byte[BUFFER_SIZE];
                int numRead;
                while (download.isStart() && ((numRead = in.read(data, 0, BUFFER_SIZE)) != -1)) {
                    // write to buffer
                    raf.write(data, 0, numRead);
                    
                    
                    try {
                        
                        download.setProgress(numRead);
                    } catch (Exception ex) {
                        download.addLogMsg(new String [] {ex.getMessage(),ex.toString()});
                        //System.out.println("javadm.com.Downloader.run()");
                    }

                }
                //if download is still instart mode and loop ended set complete
                download.getData().setComplete(download.isStart());

            } else {
                download.addLogMsg(new String []{"protocol Error"," http Response error - code : " + responsecode});
            }
        } catch (Exception ex) {
           download.addLogMsg(new String [] {ex.getMessage(),ex.toString()});
           //System.out.println("javadm.com.Downloader.run()");
        } finally {
            
            
            
            if (raf != null) {
                try {
                    raf.close();
                } catch (Exception ex) {
                    download.addLogMsg(new String [] {ex.getMessage(),ex.toString()});
                }
            }

            
            
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ex) {
                    download.addLogMsg(new String [] {ex.getMessage(),ex.toString()});
                    //System.out.println("javadm.com.Downloader.run()");
                }
            }
            
            
        }
    }

}