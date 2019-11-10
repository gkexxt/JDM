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
package javadm.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author G.K #gkexxt@outlook.com
 */
public class DownloadDataDaoSqlite implements DownloadDataDaoInterface {

    private Data extractDownloadDataFromResultSet(ResultSet rs) throws SQLException {
        Data downloadData = new Data();
        downloadData.setId(rs.getInt("id"));
        downloadData.setName(rs.getString("name"));
        downloadData.setUrl(rs.getString("url"));
        downloadData.setDirectory(rs.getString("directory"));
        downloadData.setFileSize(rs.getLong("fsize"));
        downloadData.setDoneSize(rs.getLong("dnsize"));
        downloadData.setCreatedDate(rs.getString("crtdate"));
        downloadData.setLastDate(rs.getString("lstdate"));
        downloadData.setCompleteDate(rs.getString("cmpdate"));
        return downloadData;
    }

    @Override
    public Data getDownloadData(int id) {
        Connection connection = ConnectionFactory.getConnection();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM downloaddata WHERE id=" + id);

            if (rs.next()) {
                return extractDownloadDataFromResultSet(rs);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Data> getAllDownloadData() {
        Connection connection = ConnectionFactory.getConnection();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM downloaddata");
            List<Data> DownloadDatas = new ArrayList<Data>();
            while (rs.next()) {
                Data downloadData = extractDownloadDataFromResultSet(rs);
                DownloadDatas.add(downloadData);
            }
            return DownloadDatas;

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insertDownloadData(Data downloadData) {
        Connection connection = ConnectionFactory.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO downloaddata VALUES (NULL, ?, ?, ?,?,?,?,?,?)");
            ps.setString(1, downloadData.getName());
            ps.setString(2, downloadData.getUrl());
            ps.setString(3, downloadData.getDirectory());
            ps.setLong(4, downloadData.getFileSize());
            ps.setLong(5, downloadData.getDoneSize());
            ps.setString(6, downloadData.getCreatedDate());
            ps.setString(7, downloadData.getLastDate());
            ps.setString(8, downloadData.getCompleteDate());
            int i = ps.executeUpdate();
            if (i == 1) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateDownloadData(Data downloadData) {
        Connection connection = ConnectionFactory.getConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE downloaddata SET name=?, url=?, directory=?, fsize=?, dnsize=?, crtdate=?, lstdate=?, cmpdate=? WHERE id=?");
            ps.setString(1, downloadData.getName());
            ps.setString(2, downloadData.getUrl());
            ps.setString(3, downloadData.getDirectory());
            ps.setLong(4, downloadData.getFileSize());
            ps.setLong(5, downloadData.getDoneSize());
            ps.setString(6, downloadData.getCreatedDate());
            ps.setString(7, downloadData.getLastDate());
            ps.setString(8, downloadData.getCompleteDate());
            ps.setInt(9, downloadData.getId());
            int i = ps.executeUpdate();
            if (i == 1) {
                return true;
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteDownloadData(int id) {
        Connection connection = ConnectionFactory.getConnection();
        try {
            Statement stmt = connection.createStatement();
            int i = stmt.executeUpdate("DELETE FROM downloaddate WHERE id=" + id);
            if (i == 1) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

}
