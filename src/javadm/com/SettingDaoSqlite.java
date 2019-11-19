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
package javadm.com;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author gkalianan
 */
public class SettingDaoSqlite implements SettingDaoAPI {

    private String dbName = "downloads";

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

}
