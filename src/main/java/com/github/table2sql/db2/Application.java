package com.github.table2sql.db2;


import com.github.table2sql.db2.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;

@SpringBootApplication
public class Application implements CommandLineRunner {



    @Autowired
    private Service service;


    @Value("#{'${tableNames}'.split(',')}")
    private List<String> tableNames;



    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {


       // System.out.println("tableNames = " + tableNames);


        for (String tableName : tableNames) {
            service.tableDataToFile(tableName);
        }

    }
}
