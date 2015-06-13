package com.github.table2sql.db2;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
@EnableBatchProcessing
public class Db2Table2sqlApplication implements CommandLineRunner {


    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private ApplicationContext context;


    @Value("#{'${tableNames}'.split(',')}")
    private List<String> tableNames;

    @Bean
    protected Tasklet tasklet() {
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution,
                                        ChunkContext context) {
            //    System.out.println("contribution = " + contribution);
                return RepeatStatus.FINISHED;
            }
        };
    }

    @Bean
    public Job job() throws Exception {
        return this.jobs.get("job").start(step1()).build();
    }

    @Bean
    protected Step step1() throws Exception {
        return this.steps.get("step1").tasklet(tasklet()).build();
      //  return this.steps.get("step1").chunk(1).reader();
    }



    public static void main(String[] args) throws Exception {
        SpringApplication.run(Db2Table2sqlApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {


       // System.out.println("tableNames = " + tableNames);


        JobLauncher jobLauncher = context.getBean(JobLauncher.class);
        for (String tableName : tableNames) {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("tableName", tableName)
                    .toJobParameters();
            jobLauncher.run(context.getBean(Job.class), jobParameters);
        }

    }
}
