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

/**
 * Class providing connection for db
 *
 * @author gkalianan
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    
    private static Connection c;

    public synchronized static Connection getConnection(String dbName) {
        try {

            if (c == null) {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection("jdbc:sqlite:"
                        + dbName
                        + ".db");
            }
            return c;
        
        //Class.forName("org.sqlite.JDBC");

        //Connection connection = DriverManager.getConnection("jdbc:sqlite:downloads.db");
        //return connection;
    }
    catch (ClassNotFoundException e

    
        ) {
            e.printStackTrace();
        return null;
    }
    catch (SQLException e

    
        ) {
            e.printStackTrace();
        return null;
    }
}

/**
 *
 * Test Connection
 *
 */
public static void main(String[] args) {

        Connection connection = ConnectionFactory.getConnection("test");
        //System.err.println(connection);

    }

}
