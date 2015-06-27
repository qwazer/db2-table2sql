package com.github.table2sql.db2.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.*;
import java.sql.*;
import java.util.logging.Logger;

/**
 * @author ar
 * @since Date: 19.06.2015
 */
@Component
public class Service {

    private static final Log logger = LogFactory.getLog(Service.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public Service(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    private FileNameConstructor fileNameConstructor;


    public void tableDataToFile(final String tableName) throws SQLException, IOException {

        logger.debug("Process table " + tableName);

        String sql = "SELECT * FROM " + tableName;

        String s = jdbcTemplate.query(sql, new ResultSetExtractor<String>() {
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
        });


        String fileName =fileNameConstructor.convert(tableName);
        logger.info(String.format("save tabledata of %s to file %s", tableName, fileName));
        FileUtils.writeStringToFile(new File(fileName), s);
    }

}
