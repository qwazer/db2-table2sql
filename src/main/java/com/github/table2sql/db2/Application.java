package com.github.table2sql.db2;


import com.github.table2sql.db2.service.TableDataProcessor;
import com.github.table2sql.db2.service.TableNamesDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class Application implements CommandLineRunner {



    @Autowired
    private TableDataProcessor tableDataProcessor;

    @Autowired
    private TableNamesDao tableNamesDao;


    @Value("#{'${tableNames}'.split(',')}")
    private List<String> tableNames;

    @Value("#{'${excludesTableNames}'.split(',')}")
    private List<String> excludesTableNames;



    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {


       // System.out.println("tableNames = " + tableNames);

        List<String> list = tableNamesDao.findTableNames();

        list.addAll(tableNames);
        list.removeAll(excludesTableNames);

        for (String tableName : list) {
            tableDataProcessor.readTableDataAndSaveToFile(tableName);
        }

    }
}
