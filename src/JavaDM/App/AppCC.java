/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JavaDM.App;

/**
 *
 * @author gk
 */

import javadm.com.ColorChoser;
import javax.swing.SwingUtilities;


public class AppCC {

    /**
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                new ColorChoser("ColorChoser Demo");
            }
        });
    }

}