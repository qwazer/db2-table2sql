package com.github.table2sql.db2.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TableNamesDao {

    private static final Log logger = LogFactory.getLog(Service.class);

    private final JdbcTemplate jdbcTemplate;

    @Value("${query.to.find.tableNames}")
    private String query;


    @Value("${query.to.find.tableNames.enable}")
    private Boolean tableNamesDaoEnabled;


    @Autowired
    public TableNamesDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<String> findTableNames() {

        if (tableNamesDaoEnabled) {
            return jdbcTemplate.queryForList(query, String.class);
        } else {
            return new ArrayList<String>();
        }

    }
}
