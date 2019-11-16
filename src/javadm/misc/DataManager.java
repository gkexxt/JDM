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
package javadm.misc;

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

class DowloadListEntry {
    
  private final String title;
  private final String guid;
  private Double completion;
  private String url;
  
  //to read
  //https://dzone.com/articles/building-simple-data-access-layer-using-jdbc
  public DowloadListEntry(String title, String guid,String url,Double completion) {
    this.title = title;
    this.guid = guid;
    this.completion = completion;
  }

  public String getTitle() {
    return title;
  }

  public String getGuid() {
      return guid;
  }
  
   public Double getCompletion() {
      return completion;
  }
   
   public String getUrl() {
      return url;
  }
   
   
  // Override standard toString method to give a useful result
  @Override
  public String toString() {
    return title;
  }
}
