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

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author G.K #gkexxt@outlook.com
 */
public class Downloader {

    // File Location
    private static String filePath = "/home/gk/Downloads/test/test";

    //url
    private static String url = "http://192.168.1.10";

    public static void setFilePath(String directory, String filename) {

        Downloader.filePath = directory + "/" + filename;
    }

    public static void setUrl(String url) {
        Downloader.url = url;
    }

    // This Method Is Used To Download A Sample File From The Url
    public static void downloadFileFromUrlUsingNio() {

        URL urlObj = null;
        ReadableByteChannel rbcObj = null;
        FileOutputStream fOutStream = null;

        // Checking If The File Exists At The Specified Location Or Not
        Path filePathObj = Paths.get(filePath);
        boolean fileExists = Files.exists(filePathObj);
        if (fileExists) {
            try {
                urlObj = new URL(url);
                java.net.URLConnection urncon = new java.net.URLConnection();
                rbcObj = Channels.newChannel(urlObj.openStream().);
                fOutStream = new FileOutputStream(filePath);

                fOutStream.getChannel().transferFrom(rbcObj, 0, Long.MAX_VALUE);
                System.out.println("! File Successfully Downloaded From The Url !");
            } catch (IOException ioExObj) {
                System.out.println("Problem Occured While Downloading The File= " + ioExObj.getMessage());
            } finally {
                try {
                    if (fOutStream != null) {
                        fOutStream.close();
                    }
                    if (rbcObj != null) {
                        rbcObj.close();
                    }
                } catch (IOException ioExObj) {
                    System.out.println("Problem Occured While Closing The Object= " + ioExObj.getMessage());
                }
            }
        } else {
            System.out.println("File Not Present! Please Check!");
        }
    }

    public static void main(String[] args) {
        downloadFileFromUrlUsingNio();
    }
}
