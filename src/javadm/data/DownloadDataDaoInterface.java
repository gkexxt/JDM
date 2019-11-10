/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadm.data;

import java.util.List;
/**
 *
 * @author gkalianan
 */
public interface DownloadDataDaoInterface {

    Data getDownloadData(int id);
    List<Data> getAllDownloadData();
    //User getUserByUserNameAndPassword(String user, String pass);
    boolean insertDownloadData(Data downloadData);
    boolean updateDownloadData(Data downloadData);
    boolean deleteDownloadData(int id);

}
