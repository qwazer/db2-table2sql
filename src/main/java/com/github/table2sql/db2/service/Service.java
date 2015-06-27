package com.github.table2sql.db2.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.*;
import java.sql.*;

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

    @Value("${max.rows.size}")
    private Integer maxRowsSize=1000;


    public void saveTableDataToFile(final String tableName) throws SQLException, IOException {

        logger.debug("Process table " + tableName);

        String sql = "SELECT * FROM " + tableName + " ORDER by 1 FETCH first "+maxRowsSize+" rows only with ur";

        String s = jdbcTemplate.query(sql, new InsertStatementResultSetExtractor(tableName));


        String fileName =fileNameConstructor.convert(tableName);
        logger.info(String.format("save tabledata of %s to file %s", tableName, fileName));
        FileUtils.writeStringToFile(new File(fileName), s);
    }

}
