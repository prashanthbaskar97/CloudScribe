package com.prashanth.cloud.app;

import com.timgroup.statsd.NonBlockingStatsDClient;
import org.postgresql.util.PSQLException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;
import java.net.ConnectException;
import java.sql.SQLException;

@SpringBootApplication
public class CloudAppApplication {
	public static void main(String[] args) {
		SpringApplication.run(CloudAppApplication.class, args);
	}

	@Bean
	@Scope("singleton")
	public NonBlockingStatsDClient personSingleton() {
		return new NonBlockingStatsDClient("cloudapp", "localhost", 8125);
	}
}
