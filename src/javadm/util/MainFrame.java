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

/**
 *
 * @author gk
 */
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.MouseInfo;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

public class MainFrame extends JFrame {

    private JLabel countLabel1 = new JLabel("0");
    private JLabel statusLabel = new JLabel("Task not completed.");
    private JButton startButton = new JButton("Start");
    private JButton stopButton = new JButton("Stop");
    private JPanel panel_l = new JPanel();
    
    private volatile boolean stop = false;

    public MainFrame(String title) {
        super(title);
               addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }

            private void formMousePressed(MouseEvent evt) {
                //System.exit(0); //To change body of generated methods, choose Tools | Templates.
            }
        });
        setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();

        gc.fill = GridBagConstraints.RELATIVE;
        gc.gridwidth =2;
        gc.weightx = 0.1;
        gc.weighty =0.1;
                

        gc.gridx = 1;
        gc.gridy = 0;
        add(countLabel1, gc);
        
        gc.gridx = 1;
        gc.gridy = 1;
        add(statusLabel, gc);
        
        gc.gridwidth=1;        
        gc.gridx = 1;
        gc.gridy = 2;
        add(startButton, gc);
        
        gc.gridx = 2;
        gc.gridy = 2;
        add(stopButton, gc);

        startButton.addActionListener((ActionEvent arg0) -> {
            stop = false;
            start();
        });

        stopButton.addActionListener((ActionEvent arg0) -> {
            stop = true;
        });

        setSize(200, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void start() {

        // Use SwingWorker<Void, Void> and return null from doInBackground if
        // you don't want any final result and you don't want to update the GUI
        // as the thread goes along.
        // First argument is the thread result, returned when processing finished.
        // Second argument is the value to update the GUI with via publish() and process()
        statusLabel.setText("starting......");
        SwingWorker<Boolean, Point> worker = new SwingWorker<Boolean, Point>() {

            @Override
            /*
             * Note: do not update the GUI from within doInBackground.
             */
            protected Boolean doInBackground() throws Exception {
                statusLabel.setText("started......");
                // Simulate useful work
                int i = 0;
                //location i = new DocumentationTool.Location
                while (!stop) {
                    //Thread.sleep(100);
                    //System.out.println("Hello: " + i);
                    //i++;
                    Point locationx =  MouseInfo.getPointerInfo().getLocation();
                    
                    //System.out.println(MouseInfo.getPointerInfo().getLocation());

                    // optional: use publish to send values to process(), which
                    // you can then use to update the GUI.
                    publish(locationx);
                    Thread.sleep(100);
                }
                return false;
            }

            @Override
            // This will be called if you call publish() from doInBackground()
            // Can safely update the GUI here.
            protected void process(List<Point> chunks) {
                Point value = chunks.get(chunks.size() - 1);

                countLabel1.setText("Current value: " + value);
            }

            @Override
            // This is called when the thread finishes.
            // Can safely update GUI here.
            protected void done() {

                try {
                    Boolean status = get();
                    statusLabel.setText("Completed with status: " + status);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        };

        worker.execute();
    }
}
