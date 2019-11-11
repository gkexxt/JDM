/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import javax.swing.JRootPane;
import javax.swing.SwingWorker;

public class ColorChoser extends JFrame {

    private JLabel countLabel1 = new JLabel("0");
    private JLabel statusLabel = new JLabel("Task not completed.");
    private JButton startButton = new JButton("Choose");
    private JPanel panel_l = new JPanel();
    private JFrame parentx ;
    private volatile boolean stop = false;

    public ColorChoser(String title) {
        parentx = this;
        parentx.setUndecorated(true);
        
               addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
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

        startButton.addActionListener((ActionEvent arg0) -> {
            stop = false;
            start(parentx);
        });

  

        setSize(200, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }
    
                private void formMousePressed(MouseEvent evt) {
                //System.exit(0); //To change body of generated methods, choose Tools | Templates.
                if (stop){
                    stop = false; start(parentx);//return;
                    //ColorChoser.setDefaultLookAndFeelDecorated(true);
                    parentx.setUndecorated(false);
                    return;
                    //getRootPane().setWindowDecorationStyle(JRootPane.NONE);
                }
                else{
                //System.out.println(evt.getSource());
                int x = MouseInfo.getPointerInfo().getLocation().x;
                int y = MouseInfo.getPointerInfo().getLocation().y;
                    System.out.println("X : "+x+" Y : "+y);
                parentx.setLocation(x-parentx.getWidth()/2, y-parentx.getHeight()/2);
                stop = true;
                parentx.setUndecorated(true);
                }
            }

    private void start(JFrame parentx) {

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
                parentx.setLocation(value.x-parentx.getWidth()/2,value.y-parentx.getHeight()/2);
                
                //countLabel1.setText("Current value: " + value);
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
