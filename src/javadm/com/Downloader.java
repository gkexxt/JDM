/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadm.com;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

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

        BufferedInputStream in = null;
        RandomAccessFile raf = null;

        try {
            // open Http connection to URL
            URL url = new URL(download.getData().getUrl());
            download.getData().setName(url.getFile().substring(1));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U;"
                    + " Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            // connect to server
            conn.connect();
            long contentlength = conn.getContentLengthLong();
            download.getData().setFileSize(conn.getContentLengthLong());
            int responsecode = conn.getResponseCode();

            // Make sure the response code is in the 200 range.
            if (responsecode / 100 == 2) {

                // get the input stream
                in = new BufferedInputStream(conn.getInputStream());

                // open the output file and seek to the start location
                fname = download.getData().getDirectory() + "/" + download.getData().getName();
                raf = new RandomAccessFile(fname, "rw");
                
                byte data[] = new byte[BUFFER_SIZE];
                int numRead;
                while (download.isStart() && ((numRead = in.read(data, 0, BUFFER_SIZE)) != -1)) {
                    // write to buffer
                    System.err.println(data.length);
                    raf.write(data, 0, numRead);
                    download.setProgress(numRead);
                    contentlength = conn.getContentLengthLong();
                    System.out.println(contentlength);

                }
            } else {
                System.err.println(" http Response error - code : " + responsecode);
            }
        } catch (IOException e) {
            System.err.println("Error : " + e.getMessage() + "\n" + e.toString());
        } finally {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e) {
                }
            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

}
