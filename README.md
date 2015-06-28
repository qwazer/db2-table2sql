# db2-table2sql


### Description ###

**db2-table2sql** is the simple utility to extract DB2 LUW table data as sql insert scripts.  Build on top of Spring Boot framework.

### Configuration ###

```
    spring.datasource.url=jdbc:db2://localhost:50000/sample
    spring.datasource.username=
    spring.datasource.password=
    spring.datasource.driverClassName=com.ibm.db2.jcc.DB2Driver
    file.name.template=./{schema}/tabledata/{tablename}.inserts.sql 
    max.rows.size=1000
    tableNames=SYSIBM.TABLES, SYSIBM.VIEWS, SAMPLE.MAIN_TABLE
    query.to.find.tableNames.enable=true
    query.to.find.tableNames=select table_schema || '.' || TABLE_NAME from SYSIBM.TABLES WHERE TABLE_SCHEMA='COMMON'
```