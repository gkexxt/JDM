/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javamultithread1;

import javax.swing.DefaultListModel;

/**
 *
 * @author gk
 */
public class DataManager {
    //private JList list = new JList();
    private DefaultListModel listModel = new DefaultListModel();
    //test list
    private String[] imageNames = { "Bird", "Cat", "Dog", "Rabbit", "Pig", "dukeWaveRed",
        "kathyCosmo", "lainesTongue", "left", "middle", "right", "stickerface"};
    
        public DataManager(){ 
        //todo
         //fetch list from db -
        //list = new JList(imageNames);
            for (String item : imageNames ) {
                listModel.addElement(item);
            }

        }
        
     public DefaultListModel getDownloadList() {
        return listModel;
    }
     
    public void addlist(String item) {
       // return list;
        //System.out.println(list.getModel().getSize());
               
       listModel.addElement(item);
        //System.out.println(list.getModel().getSize());
        //list.repaint();
    }

}
