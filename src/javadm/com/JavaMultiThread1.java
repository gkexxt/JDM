/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadm.com;

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
    }

    public void start() {
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
    
    public static void main(String[] args) {        
        MyThread t1 = new MyThread("thread 1");
        MyThread t2 = new MyThread("thread 2");
        MyThread t3 = new MyThread("thread 3");
        t1.start();
        t2.start();
        t3.start();
        try {
            System.err.println("Main Thread sleeping");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
        }
        System.err.println("Main thread exit");        
    }

}
