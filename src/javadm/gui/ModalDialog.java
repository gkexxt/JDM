/*
 * The MIT License
 *
 * Copyright 2019 gkalianan.
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
package javadm.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javadm.com.Download;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author gkalianan
 */
public class ModalDialog  {
    private Download selectedDownload;
    

    public ModalDialog(JFrame frame, Download selecteddownload) {
        JDialog d = new JDialog(frame, "dialog Box");
        this.selectedDownload = selecteddownload;
        System.err.println(this.selectedDownload.toString());
        //d.add((new JLabel("bla bla bla")));
        d.add(new AddOptionPanel(selectedDownload));
        d.setModal(true);
        d.setSize(200, 200);
        d.setVisible(true);
    }

  
    
}

class AddOptionPanel extends JPanel implements ActionListener{
    //JPanel optionpanelx = new JPanel(new BorderLayout());
    JButton btn = new JButton("bla bla");
    private Download selecteddownload;

    public AddOptionPanel(Download selectedDownload) {
        
        //System.err.println(selecteddownload.getData().getId()+ " : "+ selecteddownload.getData().getName());
        this.selecteddownload = selectedDownload;
        System.err.println(this.selecteddownload.toString());
        this.add(btn);
        btn.setSize(40, 25);
        //ActionEvent event = new ActionEvent(btn, 1, "bbb");
                
        btn.addActionListener(this);
    }

    //@Override
    public void actionPerformed(ActionEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        System.err.println(selecteddownload.getData().getId()+ " : "+ selecteddownload.getData().getName());
        
    }
    
}

