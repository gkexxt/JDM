/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javamultithread1;

import javax.swing.JList;
import javax.swing.ListSelectionModel;

/**
 *
 * @author gk
 */
public class DataManager {
    private JList list;
    
    //test list
    private String[] imageNames = { "Bird", "Cat", "Dog", "Rabbit", "Pig", "dukeWaveRed",
        "kathyCosmo", "lainesTongue", "left", "middle", "right", "stickerface"};
    
        public DataManager(){ 
        //todo
         //fetch list from db -
        list = new JList(imageNames);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0); 
        }
        
     public JList getDownloadList() {
        return list;
    }

}
