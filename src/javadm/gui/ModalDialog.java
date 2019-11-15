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

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javadm.com.Download;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author gkalianan
 */
public class ModalDialog implements ActionListener{
    private Download selectedDownload;

  
    

    public ModalDialog(JFrame frame, Download selecteddownload) {
        JDialog d = new JDialog(frame, "dialog Box");
        this.selectedDownload = selecteddownload;
        
        d.setModal(true);
        d.setSize(200, 200);
        d.setVisible(true);
    }
    
    public void showOption(){
        JButton btn = new JButton("bla bla");
        btn.setSize(40, 25);               
        btn.addActionListener(this);        
        SpinnerModel modeltau = new SpinnerNumberModel(1, 1, 10, 1);
        JSpinner spinner = new JSpinner(modeltau);
        Font ff = new Font(spinner.getFont().toString(),Font.PLAIN,18);
        spinner.setFont(ff);
    }

    @Override
      public void actionPerformed(ActionEvent e) {
       throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        //System.err.println(selecteddownload.getData().getId()+ " : "+ selecteddownload.getData().getName());
        
    }
    
}


    
 


    


