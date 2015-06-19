package com.github.table2sql.db2.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author ar
 * @since Date: 19.06.2015
 */

@Component
public class FileNameConstructor {

    @Value("${file.name.template}")
    private String template ;



    public String convert(String s) {
        String schemaName = "";
        String tableName = "";
        if (s.contains(".")){
            String[] strings = s.split(".");
            if (strings.length>1){
                throw new IllegalArgumentException();
            }
            schemaName =strings[0];
            tableName = strings[1];
        } else {
         tableName = s;
        }

        String result = template.replaceAll("\\{schema\\}", schemaName).replaceAll("\\{tablename\\}", tableName);
        return result;
    }
}
