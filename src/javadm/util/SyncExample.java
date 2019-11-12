package javadm.util;


import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author gkalianan
 */
public class SyncExample {
  private static Date lastacc; 
  private static volatile boolean running = true;
  
  public static class Thingie {
    private Date lastAccess;
    private String data;
    public synchronized void setLastAccess(Date date, String datain) {
      this.lastAccess = date;
      this.data = datain;
        System.out.println(data + " : " +lastAccess.toString());      
        lastacc = lastAccess;
    }
  }

  public static class MyThread extends Thread {
    private Thingie thingie;
    private String name;
    public MyThread(Thingie thingie,String name) {
      this.thingie = thingie;
      this.name = name;
    }

    @Override
    
    public void run() {
        while (running) {            
            
            try {
                thingie.setLastAccess(new Date(),name);
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(SyncExample.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        System.out.println(name + " : stopped");
      
    }
  }

   public static void main(String[] args) throws InterruptedException {
    //Thingie thingie1 = new Thingie();
   // Thingie thingie2 = new Thingie();
for (int i = 0; i < 100; i++) {
           new MyThread(new Thingie(),"T " + Integer.toString(i)).start();
       }    
    //new MyThread(new Thingie(),"T2").start();
    Thread.sleep(100);
    running = false;
    System.out.println(lastacc.toString());
      //boolean lastAccess;
       //System.err.println(lastAccess);
  }
   
   
}
