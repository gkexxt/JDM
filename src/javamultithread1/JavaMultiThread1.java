/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javamultithread1;

/**
 *
 * @author gk
 */
class MyThread implements Runnable {

    String name;
    Thread t;

    MyThread(String threadname) {
        name = threadname;
        t = new Thread(this, name);
        System.out.println("New thread: " + t);
        t.start();
    }

    @Override
    public void run() {
        try {
            for (int i = 5; i > 0; i--) {
                System.out.println(name + ": " + i);
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.out.println(name + "Interrupted");
        }
        System.out.println(name + " exiting.");
    }
    

}

public class JavaMultiThread1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        MyThread t1 = new MyThread("thread 1");
        //t1.Start();
        MyThread t2 = new MyThread("thread 2");
        MyThread t3 = new MyThread("thread 3");
        try {
            System.err.println("Main Thread sleeping");
            Thread.sleep(10000);
        } catch (Exception e) {
        }
        System.err.println("Main thread exit");
        //t2.run();
        
    }

}
