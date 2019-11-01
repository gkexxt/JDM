package javamultithread1;


import java.util.Date;

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
      thingie.setLastAccess(new Date(),name);
    }
  }

   public static void main(String[] args) throws InterruptedException {
    //Thingie thingie1 = new Thingie();
   // Thingie thingie2 = new Thingie();
for (int i = 0; i < 100000; i++) {
           new MyThread(new Thingie(),"T" + Integer.toString(i)).start();
       }
 
    
    new MyThread(new Thingie(),"T2").start();
    Thread.sleep(1000);
    System.out.println(lastacc.toString());
      //boolean lastAccess;
       //System.err.println(lastAccess);
  }
   
   
}
