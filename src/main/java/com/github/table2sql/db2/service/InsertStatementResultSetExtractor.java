package com.github.table2sql.db2.service;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * @author ar
 * @since Date: 27.06.2015
 */
class InsertStatementResultSetExtractor implements ResultSetExtractor<String> {
    private final String tableName;
//    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//    private static DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
//    private static DateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSS");

    public InsertStatementResultSetExtractor(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String extractData(ResultSet rs) throws SQLException, DataAccessException {

        String result = "";

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

        StringBuilder stringBuilder = new StringBuilder();

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
                        Date date = rs.getDate(i + 1);
                       // columnValues += date == null ? "null" : String.format("'%1$TF'", date) ;
                        columnValues += date == null ? "null" : "'" + date.toString() + "'"  ;
                        break;

                    case Types.TIME:
                        Time time= rs.getTime(i + 1);
                       // columnValues += time == null ? "null" : String.format("'%1$TT'", time) ;
                        columnValues += time == null ? "null" : "'" + time.toString() + "'" ;
                        break;
                    case Types.TIMESTAMP:
                        Timestamp timestamp = rs.getTimestamp(i + 1);
                     //   columnValues += timestamp == null ? "null" : String.format("'%1$TF %1$TT.%1$TN'", timestamp);
                        columnValues += timestamp == null ? "null" : "'" + timestamp.toString() + "'";
                        break;
                    default:
                        v = rs.getString(i + 1);
                        if (v != null) {
                            columnValues += "'" + v.replaceAll("'", "''") + "'";
                        } else {
                            columnValues += "null";
                        }
                        break;
                }
            }
            stringBuilder.append(String.format("INSERT INTO %s (%s) values (%s);\n",
                    tableName,
                    columnNames,
                    columnValues));
        }

        return stringBuilder.toString();
    }
}
