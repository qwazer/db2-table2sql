package com.github.table2sql.db2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

/**
 * @author ar
 * @since Date: 19.06.2015
 */
@Component
public class Service {

    @Autowired
    private DataSource dataSource;


    public void tableDataToFile(String tableName) throws SQLException, IOException {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName);
        ResultSetMetaData rsmd = rs.getMetaData();
        int numColumns = rsmd.getColumnCount();
        int[] columnTypes = new int[numColumns];
        String columnNames = "";
        for (int i = 0; i < numColumns; i++) {
            columnTypes[i] = rsmd.getColumnType(i + 1);
            if (i != 0) {
                columnNames += ",";
            }
            columnNames += rsmd.getColumnName(i + 1);
        }

        java.util.Date d = null;
        PrintWriter p = new PrintWriter(new FileWriter(tableName + "_insert.sql"));

        while (rs.next()) {
            String columnValues = "";
            for (int i = 0; i < numColumns; i++) {
                if (i != 0) {
                    columnValues += ",";
                }

                switch (columnTypes[i]) {
                    case Types.BIGINT:
                    case Types.BIT:
                    case Types.BOOLEAN:
                    case Types.DECIMAL:
                    case Types.DOUBLE:
                    case Types.FLOAT:
                    case Types.INTEGER:
                    case Types.SMALLINT:
                    case Types.TINYINT:
                        String v = rs.getString(i + 1);
                        columnValues += v;
                        break;

                    case Types.DATE:
                        d = rs.getDate(i + 1);
                    case Types.TIME:
                        if (d == null) d = rs.getTime(i + 1);
                    case Types.TIMESTAMP:
                        if (d == null) d = rs.getTimestamp(i + 1);

                        if (d == null) {
                            columnValues += "null";
                        }
                        else {
// todo                           columnValues += "TO_DATE('"
//                                    + dateFormat.format(d)
//                                    + "', 'YYYY/MM/DD HH24:MI:SS')";
                        }
                        break;

                    default:
                        v = rs.getString(i + 1);
                        if (v != null) {
                            columnValues += "'" + v.replaceAll("'", "''") + "'";
                        }
                        else {
                            columnValues += "null";
                        }
                        break;
                }
            }
            p.println(String.format("INSERT INTO %s (%s) values (%s);\n",
                    tableName,
                    columnNames,
                    columnValues));
        }
        p.close();



    }

}
