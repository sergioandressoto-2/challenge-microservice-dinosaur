package com.challenge.microservice.integration;

import com.challenge.microservice.application.port.out.NotificationPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HealthEndpointIntegrationTest {

    @MockitoBean
    NotificationPort notificationPort;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void health_returns200_whenApplicationIsUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")));
    }

    @Test
    void health_databaseComponent_isUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.database.status", is("UP")))
                .andExpect(jsonPath("$.components.database.details.validation", is("JPA count() OK")));
    }

    @Test
    void health_applicationComponent_isUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.components.application.status", is("UP")))
                .andExpect(jsonPath("$.components.application.details.name", is("dinosaur")))
                .andExpect(jsonPath("$.components.application.details.schedulerCron").exists());
    }
}
