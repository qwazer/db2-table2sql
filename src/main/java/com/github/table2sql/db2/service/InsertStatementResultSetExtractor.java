package com.github.table2sql.db2.service;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author ar
 * @since Date: 27.06.2015
 */
class InsertStatementResultSetExtractor implements ResultSetExtractor<String> {
    private final String tableName;

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

        java.util.Date d = null;
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
                        d = rs.getDate(i + 1);
                    case Types.TIME:
                        if (d == null) d = rs.getTime(i + 1);
                    case Types.TIMESTAMP:
                        if (d == null) d = rs.getTimestamp(i + 1);

                        if (d == null) {
                            columnValues += "null";
                        } else {
// todo                           columnValues += "TO_DATE('"
//                                    + dateFormat.format(d)
//                                    + "', 'YYYY/MM/DD HH24:MI:SS')";
                        }
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
