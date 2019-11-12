/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadm.app;

/**
 *
 * @author gk
 */

import javadm.gui.DownloadMainFrame;
import javax.swing.SwingUtilities;


public class App {

    /**
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            //new ColorChoser("ColorChoser Demo");
            DownloadMainFrame main;
            main = new DownloadMainFrame();
            main.createAndShowGUI();
            main.refreshTable();
        });
    }

}