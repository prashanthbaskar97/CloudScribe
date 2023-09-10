package com.prashanth.cloud.app.util;

import com.hari.cloud.app.dao.User;
import com.hari.cloud.app.service.UserService;
import com.opencsv.CSVReader;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.util.Date;

@Component
public class LoadUsersUtil {
    String fileName = "/opt/users.csv";
    @Autowired
    UserService userService;
    @PostConstruct
    public void load() {
        try {
            // Create an object of filereader
            // class with CSV file as a parameter.
            FileReader filereader = new FileReader(fileName);

            // create csvReader object passing
            // file reader as a parameter
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;
            csvReader.readNext();
            // we are going to read data line by line
            while ((nextRecord = csvReader.readNext()) != null) {
                if(userService.getUserBy(nextRecord[2]) != null) continue;
                User user = new User(nextRecord[0], nextRecord[1], nextRecord[2], Utility.passwordEncoder.encode(nextRecord[3]), new Date(), new Date());
                userService.create(user);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
