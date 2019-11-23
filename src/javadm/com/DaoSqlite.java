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
package javadm.com;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
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
public class DaoSqlite implements DaoAPI {

    private String dbName = "downloads";

    private Download extractDownloadFromResultSet(ResultSet rs) throws SQLException {
        Download download = new Download();
        download.setId(rs.getInt("id"));
        download.setName(rs.getString("name"));
        download.setUrl(rs.getString("url"));
        download.setDirectory(rs.getString("directory"));
        download.setFileSize(rs.getLong("fsize"));
        download.setDoneSize(rs.getLong("dnsize"));
        download.setCreatedDate(rs.getString("crtdate"));
        download.setLastDate(rs.getString("lstdate"));
        download.setCompleteDate(rs.getString("cmpdate"));
        return download;
    }

    @Override
    public Download getDownload(int id) {
        Connection connection = ConnectionFactory.getConnection(dbName);
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM downloaddata WHERE id=" + id);

            if (rs.next()) {
                return extractDownloadFromResultSet(rs);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Download getLastDownload() {
        Connection connection = ConnectionFactory.getConnection(dbName);
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM downloaddata ORDER BY id DESC LIMIT 1");

            if (rs.next()) {
                return extractDownloadFromResultSet(rs);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Download> getAllDownload() {
        Connection connection = ConnectionFactory.getConnection(dbName);
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM downloaddata");
            List<Download> Downloads = new ArrayList<Download>();
            while (rs.next()) {
                Download download = extractDownloadFromResultSet(rs);
                Downloads.add(download);
            }
            return Downloads;

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insertDownload(Download download) {
        Connection connection = ConnectionFactory.getConnection(dbName);
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO downloaddata VALUES (NULL, ?, ?, ?,?,?,?,?,?)");
            ps.setString(1, download.getName());
            ps.setString(2, download.getUrl());
            ps.setString(3, download.getDirectory());
            ps.setLong(4, download.getFileSize());
            ps.setLong(5, download.getDoneSize());
            ps.setString(6, download.getCreatedDate());
            ps.setString(7, download.getLastDate());
            ps.setString(8, download.getCompleteDate());
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
    public boolean updateDownload(Download download) {
        Connection connection = ConnectionFactory.getConnection(dbName);
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE downloaddata SET name=?, url=?, directory=?, fsize=?, dnsize=?, crtdate=?, lstdate=?, cmpdate=? WHERE id=?");
            ps.setString(1, download.getName());
            ps.setString(2, download.getUrl());
            ps.setString(3, download.getDirectory());
            ps.setLong(4, download.getFileSize());
            ps.setLong(5, download.getDoneSize());
            ps.setString(6, download.getCreatedDate());
            ps.setString(7, download.getLastDate());
            ps.setString(8, download.getCompleteDate());
            ps.setInt(9, download.getId());
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
    public boolean deleteDownload(int id) {
        Connection connection = ConnectionFactory.getConnection(dbName);
        try {
            Statement stmt = connection.createStatement();
            int i = stmt.executeUpdate("DELETE FROM downloaddata WHERE id=" + id);
            if (i == 1) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public Setting getSetting() {
        Connection connection = ConnectionFactory.getConnection(dbName);
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM setting WHERE id=" + 1);

            if (rs.next()) {
                Setting setting = new Setting();
                setting.setDirectory(rs.getString("directory"));
                setting.setConnectionCount(rs.getInt("conncount"));
                setting.setMonitorMode(rs.getInt("monmode"));
                setting.setAutoStart(rs.getBoolean("automode"));
                return setting;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean setSetting(Setting setting) {
        Connection connection = ConnectionFactory.getConnection(dbName);
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE setting SET directory=?, conncount=?, monmode=?, automode=? WHERE id=?");
            ps.setString(1, setting.getDirectory());
            ps.setInt(2, setting.getConnectionCount());
            ps.setInt(3, setting.getMonitorMode());
            ps.setBoolean(4, setting.isAutoStart());
            ps.setInt(5, 1);
            System.err.println(ps.toString());
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

    public boolean isTableExists(String tableName) {
        Connection connection = ConnectionFactory.getConnection(dbName);
        try {
            DatabaseMetaData md = connection.getMetaData();
            ResultSet rs = md.getTables(null, null, tableName, null);
            while (rs.next()) {
                if (rs.getString("Table_NAME").equals(tableName)) {
                    return true;
                }
            }
        } catch (SQLException ex) {
        }
        return false;
    }

    public List<Part> getParts(int download_id) {
        Connection connection = ConnectionFactory.getConnection(dbName);
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM downloadpart where download_id = " + download_id);
            List<Part> downloadParts = new ArrayList<Part>();
            while (rs.next()) {
                //Data download = extractDownloadFromResultSet(rs);
                Part part = new Part();
                part.setId(download_id);
                part.setPartFileName(rs.getString("name"));
                part.setSize(rs.getLong("part_size"));
                part.setCurrentSize(rs.getLong("current_size"));
                part.setStartByte(rs.getLong("start_byte"));                
                part.setEndByte(rs.getLong("end_byte"));
                part.setType(rs.getByte("type"));
                part.setCompleted(rs.getBoolean("completed"));
                downloadParts.add(part);

            }
            return downloadParts;

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new ArrayList<Part>();
    }

    public boolean updatePart(long download_id, Part part) {
        Connection connection = ConnectionFactory.getConnection(dbName);
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE downloadpart SET current_size=?, completed=? WHERE download_id=? AND name=?");
            ps.setLong(1, part.getCurrentSize());
            ps.setBoolean(2, part.isCompleted());
            ps.setLong(3, download_id);
            ps.setString(4, part.getPartFileName());
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

    public boolean insertParts(int download_id, List<Part> parts) {
        Connection connection = ConnectionFactory.getConnection(dbName);
        for (Part part : parts) {

            try {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO downloadpart VALUES (?, ?, ?, ?,?,?,?,?)");;
                ps.setLong(1, download_id);
                ps.setString(2, part.getPartFileName());
                ps.setLong(3, part.getSize());
                ps.setLong(4, part.getCurrentSize());
                ps.setLong(5, part.getStartByte());
                ps.setLong(6, part.getEndByte());
                ps.setByte(7, part.getType());                
                ps.setBoolean(8, part.isCompleted());
                int i = ps.executeUpdate();
                if (i != 1) {
                    return false;
                }
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
                ex.printStackTrace();
                return false;
            }
        }
        return true;

    }

    public boolean deleteParts(int download_id) {
        Connection connection = ConnectionFactory.getConnection(dbName);
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE downloadpart WHERE download_id = ?");
            ps.setLong(1, download_id);
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

}
