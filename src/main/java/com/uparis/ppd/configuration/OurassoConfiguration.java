package com.uparis.ppd.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableScheduling
public class OurassoConfiguration {

    @Value("${spring.datasource.hikari.jdbc-url}")
    private String url;

    @Value("${spring.datasource.hikari.username}")
    private String usernameDatabase;

    @Value("${spring.datasource.hikari.password}")
    private String passwordDatabase;

    @Value("${spring.datasource.hikari.driver-class-name}")
    private String driver;

    @Value("${spring.datasource.hikari.maximum-pool-size}")
    private int poolSize;

    @Value("${spring.mail.username}")
    private String usernameMail;

    @Value("${spring.mail.password}")
    private String passwordMail;

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private String port;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(usernameDatabase);
        config.setPassword(passwordDatabase);
        config.setDriverClassName(driver);
        config.setMaximumPoolSize(poolSize);
        return new HikariDataSource(config);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(Integer.parseInt(port));
        mailSender.setUsername(usernameMail);
        mailSender.setPassword(passwordMail);
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        return mailSender;
    }
}
