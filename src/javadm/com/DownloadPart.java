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
public class DownloadPart {

    private long startByte;
    private long endByte;
    private long currentSize;
    private long size;
    private String partFileName;
    private boolean completed;
    public static long partSize = 5000000; //default 5Mb
    private int id;

    public DownloadPart() {
        this.currentSize = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public static void setPartSize(long partSize) {
        DownloadPart.partSize = partSize;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }



    public String getPartFileName() {
        return partFileName;
    }

    public void setPartFileName(String partFileName) {
        this.partFileName = partFileName;
    }

    public long getStartByte() {
        return startByte;
    }

    public void setStartByte(long startByte) {
        this.startByte = startByte;
    }

    public long getEndByte() {
        return endByte;
    }

    public void setEndByte(long endByte) {
        this.endByte = endByte;
    }

    public long getCurrentSize() {
        return currentSize;
    }

    public synchronized void setCurrentSize(long currentSize) {
        this.currentSize = currentSize;
    }

}
