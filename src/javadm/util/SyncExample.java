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
