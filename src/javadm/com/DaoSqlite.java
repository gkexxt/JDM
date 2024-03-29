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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author G.K #gkexxt@outlook.com
 */
public class DaoSqlite implements DaoAPI {

    private final String dbName = "downloads";

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
        download.setType(rs.getByte("type"));
        download.setUserAgent(rs.getString("user_agent"));
        download.setComplete(rs.getBoolean("complete"));
        download.setConnections(rs.getInt("connection"));
        download.setState(rs.getString("state"));
        download.setScheduleStart(rs.getString("s_start"));
        download.setScheduleStop(rs.getString("s_stop"));
        download.setScheduled(rs.getBoolean("scheduled"));
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
        return new ArrayList<Download>();
    }

    @Override
    public boolean insertDownload(Download download) {
        Connection connection = ConnectionFactory.getConnection(dbName);
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO downloaddata VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?)");
            ps.setString(1, download.getName());
            ps.setString(2, download.getUrl());
            ps.setString(3, download.getDirectory());
            ps.setLong(4, download.getFileSize());
            ps.setLong(5, download.getDoneSize());
            ps.setString(6, download.getCreatedDate());
            ps.setString(7, download.getLastDate());
            ps.setString(8, download.getCompleteDate());
            ps.setByte(9, download.getType());
            ps.setString(10, download.getUserAgent());
            ps.setBoolean(11, download.isComplete());
            ps.setInt(12, download.getConnections());
            ps.setString(13, download.getState());
            ps.setString(14, download.getScheduleStart());
            ps.setString(15, download.getScheduleStop());
            ps.setBoolean(16, download.isScheduled());
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
            PreparedStatement ps = connection.prepareStatement("UPDATE "
                    + "downloaddata SET name=?, url=?, directory=?, fsize=?, "
                    + "dnsize=?, crtdate=?, lstdate=?, cmpdate=?, type=?, "
                    + "user_agent=?, complete=?, connection=?, "
                    + "state=?, s_start=?, s_stop=?, scheduled=? WHERE id=?");
            ps.setString(1, download.getName());
            ps.setString(2, download.getUrl());
            ps.setString(3, download.getDirectory());
            ps.setLong(4, download.getFileSize());
            ps.setLong(5, download.getDoneSize());
            ps.setString(6, download.getCreatedDate());
            ps.setString(7, download.getLastDate());
            ps.setString(8, download.getCompleteDate());
            ps.setInt(9, download.getType());
            ps.setString(10, download.getUserAgent());
            ps.setBoolean(11, download.isComplete());
            ps.setInt(12, download.getConnections());
            ps.setString(13, download.getState());
            ps.setString(14, download.getScheduleStart());
            ps.setString(15, download.getScheduleStop());

            ps.setBoolean(16, download.isScheduled());
            ps.setInt(17, download.getId());

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
                setting.setUserAgent(rs.getString("user_agent"));
                setting.setSchedulerEnable(rs.getBoolean("scheduler"));
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
            PreparedStatement ps = connection.prepareStatement("UPDATE setting SET directory=?, conncount=?, monmode=?, automode=?, user_agent=?, scheduler=? WHERE id=?");
            ps.setString(1, setting.getDirectory());
            ps.setInt(2, setting.getConnectionCount());
            ps.setInt(3, setting.getMonitorMode());
            ps.setBoolean(4, setting.isAutoStart());
            ps.setString(5, setting.getUserAgent());
            ps.setBoolean(6, setting.isSchedulerEnable());
            ps.setInt(7, 1);
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
    
        public boolean createSetting(Setting setting) {
        Connection connection = ConnectionFactory.getConnection(dbName);
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO setting VALUES (?,?, ?, ?, ?, ?, ?)");
            ps.setInt(1, 1);
            ps.setString(2, setting.getDirectory());
            ps.setInt(3, setting.getConnectionCount());
            ps.setInt(4, setting.getMonitorMode());
            ps.setBoolean(5, setting.isAutoStart());
            ps.setString(6, setting.getUserAgent());
            ps.setBoolean(7, setting.isSchedulerEnable());
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

    public List<DownloadPart> getParts(int download_id) {
        Connection connection = ConnectionFactory.getConnection(dbName);
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM downloadpart where download_id = " + download_id);
            List<DownloadPart> downloadParts = new ArrayList<DownloadPart>();
            while (rs.next()) {
                //Data download = extractDownloadFromResultSet(rs);
                DownloadPart part = new DownloadPart();
                part.setId(download_id);
                part.setPartFileName(rs.getString("name"));
                part.setSize(rs.getLong("part_size"));
                part.setCurrentSize(rs.getLong("current_size"));
                part.setStartByte(rs.getLong("start_byte"));
                part.setEndByte(rs.getLong("end_byte"));
                part.setCompleted(rs.getBoolean("completed"));
                downloadParts.add(part);

            }
            return downloadParts;

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return new ArrayList<DownloadPart>();
    }

    public synchronized boolean updatePart(long download_id, DownloadPart part) {
        Connection connection = ConnectionFactory.getConnection(dbName);
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE downloadpart SET part_size=?, current_size=?, completed=? WHERE download_id=? AND name=?");
            ps.setLong(1, part.getSize());
            ps.setLong(2, part.getCurrentSize());
            ps.setBoolean(3, part.isCompleted());
            ps.setLong(4, download_id);
            ps.setString(5, part.getPartFileName());
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

    public boolean insertParts(int download_id, List<DownloadPart> parts) {
        Connection connection = ConnectionFactory.getConnection(dbName);
        for (DownloadPart part : parts) {

            try {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO downloadpart VALUES (?, ?, ?,?,?,?,?)");;
                ps.setLong(1, download_id);
                ps.setString(2, part.getPartFileName());
                ps.setLong(3, part.getSize());
                ps.setLong(4, part.getCurrentSize());
                ps.setLong(5, part.getStartByte());
                ps.setLong(6, part.getEndByte());
                ps.setBoolean(7, part.isCompleted());
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
            PreparedStatement ps = connection.prepareStatement("DELETE FROM downloadpart WHERE download_id = ?");
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

    public void createNewDownloadTables() {

        try {
            String sql = "CREATE TABLE IF NOT EXISTS `downloaddata` ("
                    + "`id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "`name`	TEXT,"
                    + "`url`	TEXT,"
                    + "`directory`	TEXT,"
                    + "`fsize`	INTEGER,"
                    + "`dnsize`	INTEGER,"
                    + "`crtdate`	TEXT,"
                    + "`lstdate`	TEXT,"
                    + "`cmpdate`	NUMERIC,"
                    + "`type`	INTEGER NOT NULL DEFAULT 0,"
                    + "`user_agent`	TEXT,"
                    + "`complete`	TEXT,"
                    + "`connection`	INTEGER DEFAULT 1,"
                    + "`state`	TEXT DEFAULT 'Unknown',"
                    + "`s_start`	TEXT,"
                    + "`s_stop`	TEXT,"
                    + "`scheduled`	TEXT );";

            Connection connection = ConnectionFactory.getConnection(dbName);
            Statement stmt = connection.createStatement();
            stmt.execute(sql);

            sql = "CREATE TABLE IF NOT EXISTS `downloadpart` (\n"
                    + "	`download_id`	INTEGER NOT NULL,\n"
                    + "	`name`	TEXT NOT NULL,\n"
                    + "	`part_size`	INTEGER NOT NULL,\n"
                    + "	`current_size`	INTEGER NOT NULL,\n"
                    + "	`start_byte`	INTEGER,\n"
                    + "	`end_byte`	INTEGER,\n"
                    + "	`completed`	TEXT\n"
                    + ");";
            stmt.execute(sql);

            sql = "CREATE TABLE IF NOT EXISTS `setting` (\n"
                    + "	`id`	INT NOT NULL UNIQUE,\n"
                    + "	`directory`	STRING NOT NULL,\n"
                    + "	`conncount`	INT NOT NULL DEFAULT (1),\n"
                    + "	`monmode`	INT DEFAULT (0),\n"
                    + "	`automode`	BOOLEAN NOT NULL DEFAULT (0),\n"
                    + "	`user_agent`	TEXT,\n"
                    + "	`scheduler`	CHAR NOT NULL DEFAULT '0',\n"
                    + "	PRIMARY KEY(`id`)\n"
                    + ");";

            stmt.execute(sql);            
          
            
        } catch (SQLException ex) {
            Logger.getLogger(DaoSqlite.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
