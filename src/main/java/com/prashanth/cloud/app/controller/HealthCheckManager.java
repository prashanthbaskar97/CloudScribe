package com.prashanth.cloud.app.controller;
import com.timgroup.statsd.NonBlockingStatsDClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import org.postgresql.util.PSQLException;

import java.sql.Connection;
import java.sql.SQLException;

@RestController
@Slf4j
public class HealthCheckManager {
    @Autowired
    DataSource dataSource;

    @Autowired
    NonBlockingStatsDClient statsd;

    @GetMapping("/healthz")
    public ResponseEntity checkHealth() {
        try (Connection connection = dataSource.getConnection()) {
            log.info("Database is healthy");
            statsd.incrementCounter("healthcheck-invoke-count");
        } catch (PSQLException e) {
            return new ResponseEntity(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (SQLException e) {
            return new ResponseEntity(HttpStatus.SERVICE_UNAVAILABLE);
        } catch (CannotCreateTransactionException e) {
            return new ResponseEntity(HttpStatus.SERVICE_UNAVAILABLE);
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
