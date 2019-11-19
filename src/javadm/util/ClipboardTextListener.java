/*
 * The MIT License
 *
 * Copyright 2019 gkalianan.
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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author gkalianan
 */
public class ClipboardTextListener implements Runnable {

    Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();

    private volatile boolean running ;
    private PropertyChangeSupport clipboardUpdate
            = new PropertyChangeSupport(this);
    public void terminate() {
        running = false;
    }
    
      public void addPropertyChangeListener(PropertyChangeListener listener) {
        clipboardUpdate.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        clipboardUpdate.removePropertyChangeListener(listener);
    }

    @Override
    public void run() {
        // the first output will be when a non-empty text is detected
        String recentContent = "";
        // continuously perform read from clipboard
        while (running) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                // request what kind of data-flavor is supported
                List<DataFlavor> flavors = Arrays.asList(sysClip.getAvailableDataFlavors());
                // this implementation only supports string-flavor
                if (flavors.contains(DataFlavor.stringFlavor)) {
                    String data = (String) sysClip.getData(DataFlavor.stringFlavor);
                    if (!data.equals(recentContent)) {
                        recentContent = data;
                        // Do whatever you want to do when a clipboard change was detected, e.g.:
                        //System.out.println("New clipboard text detected: " + data);
                        clipboardUpdate.firePropertyChange("ClipboardUpdate", "", data);
                    }
                }

            } catch (Exception e1) {
            } 
        }
    }
    
    public void startMonitor(){
        running = true;
        Thread thread = new Thread(this);
        thread.start();
    }
}
