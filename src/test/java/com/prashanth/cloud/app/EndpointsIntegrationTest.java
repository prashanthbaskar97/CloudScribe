package com.prashanth.cloud.app;

import com.hari.cloud.app.dao.User;
import com.hari.cloud.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
public class EndpointsIntegrationTest {
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WebApplicationContext applicationContext;

    @BeforeAll
    public void init(){
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .build();
        setupUser();
    }

    private void setupUser(){
        User user = new User();
        user.setPassword("test123");
        user.setEmail("test@spring.com");
        user.setFirst_name("Sheldon");
        user.setLast_name("Cooper");
        user.setAccount_created(new Date());
        user.setAccount_updated(new Date());
        userRepository.save(user);
    }

    @Test
    @WithMockUser(username = "test@spring.com",password = "test123",roles = {"USER"})
    public void whenGetHealthStatus_thenReturnSuccess() throws Exception {
        mockMvc.perform(get("/healthz"))
                .andExpect(status().isOk());
    }
}
