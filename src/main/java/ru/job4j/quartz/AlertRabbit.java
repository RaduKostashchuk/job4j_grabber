package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class AlertRabbit {
    public static void main(String[] args) {
        Properties properties = load();
        int interval = Integer.parseInt(properties.getProperty("rabbit.interval"));
        try {
            Connection connection = init(properties);
            JobDataMap map = new JobDataMap();
            map.put("connection", connection);
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(map)
                    .build();
            SimpleScheduleBuilder times = SimpleScheduleBuilder
                    .simpleSchedule()
                    .withIntervalInSeconds(interval)
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (SchedulerException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Properties load() {
        Properties properties = null;
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static Connection init(Properties properties) {
        Connection connection = null;
        try {
            Class.forName(properties.getProperty("rabbit.driver"));
            connection = DriverManager.getConnection(
                    properties.getProperty("rabbit.url"),
                    properties.getProperty("rabbit.username"),
                    properties.getProperty("rabbit.password")
            );
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            Connection connection = (Connection) context.getJobDetail().getJobDataMap().get("connection");
            LocalDateTime localDateTime = LocalDateTime.now();
            try (PreparedStatement statement = connection.prepareStatement(
                    "insert into rabbit(created_date) values(?)")) {
                statement.setTimestamp(1, Timestamp.valueOf(localDateTime));
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
