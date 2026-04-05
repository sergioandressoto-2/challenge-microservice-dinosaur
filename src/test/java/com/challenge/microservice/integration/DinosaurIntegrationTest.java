package com.challenge.microservice.integration;

import com.challenge.microservice.adapters.out.repository.DbRepository;
import com.challenge.microservice.application.dto.DinosaurRequest;
import com.challenge.microservice.application.port.out.NotificationPort;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DinosaurIntegrationTest {

    @MockitoBean
    NotificationPort notificationPort;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DbRepository dbRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Date past;
    private Date future;

    @BeforeEach
    void setUp() {
        dbRepository.deleteAll();
        past   = new Date(System.currentTimeMillis() - 86_400_000L);
        future = new Date(System.currentTimeMillis() + 86_400_000L);
    }

    private String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    private DinosaurRequest buildRequest(String name, String species, Date discovery, Date extinction, String status) {
        DinosaurRequest req = new DinosaurRequest();
        req.setName(name);
        req.setSpecies(species);
        req.setDiscoveryDate(discovery);
        req.setExtinctionDate(extinction);
        req.setStatus(status);
        return req;
    }

    private DinosaurRequest buildRequest(String name, String species, Date discovery, Date extinction) {
        return buildRequest(name, species, discovery, extinction, "ALIVE");
    }

    private Long savedId() {
        return dbRepository.findAll().get(0).getId();
    }

    // -------------------------------------------------------------------------
    // POST /api/v1/dinosaur
    // -------------------------------------------------------------------------

    @Test
    void createDinosaur_returns201_whenRequestIsValid() throws Exception {
        mockMvc.perform(post("/api/v1/dinosaur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(buildRequest("T-Rex", "Tyrannosaurus", past, future))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("T-Rex")))
                .andExpect(jsonPath("$.species", is("Tyrannosaurus")))
                .andExpect(jsonPath("$.status", is("ALIVE")));
    }

    @Test
    void createDinosaur_persistsDinosaurWithStatusAlive() throws Exception {
        mockMvc.perform(post("/api/v1/dinosaur")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(buildRequest("T-Rex", "Tyrannosaurus", past, future))));

        mockMvc.perform(get("/api/v1/dinosaur"))
                .andExpect(jsonPath("$[0].name", is("T-Rex")))
                .andExpect(jsonPath("$[0].status", is("ALIVE")));
    }

    @Test
    void createDinosaur_returns422_whenNameAlreadyExists() throws Exception {
        DinosaurRequest req = buildRequest("T-Rex", "Tyrannosaurus", past, future);
        mockMvc.perform(post("/api/v1/dinosaur")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(req)));

        mockMvc.perform(post("/api/v1/dinosaur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(req)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code", is(422)))
                .andExpect(jsonPath("$.message", containsString("T-Rex")));
    }

    @Test
    void createDinosaur_returns422_whenDiscoveryIsAfterExtinction() throws Exception {
        mockMvc.perform(post("/api/v1/dinosaur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(buildRequest("T-Rex", "Tyrannosaurus", future, past))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code", is(422)))
                .andExpect(jsonPath("$.message", containsString("discoveryDate must be before extinction date")));
    }

    @Test
    void createDinosaur_returns422_whenDiscoveryEqualsExtinction() throws Exception {
        Date same = new Date();
        mockMvc.perform(post("/api/v1/dinosaur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(buildRequest("T-Rex", "Tyrannosaurus", same, same))))
                .andExpect(status().isUnprocessableEntity());
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/dinosaur
    // -------------------------------------------------------------------------

    @Test
    void getDinosaurs_returns200_withEmptyList_whenNoDinosaurs() throws Exception {
        mockMvc.perform(get("/api/v1/dinosaur"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getDinosaurs_returns200_withAllPersistedDinosaurs() throws Exception {
        mockMvc.perform(post("/api/v1/dinosaur")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(buildRequest("T-Rex", "Tyrannosaurus", past, future))));
        mockMvc.perform(post("/api/v1/dinosaur")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(buildRequest("Raptor", "Velociraptor", past, future))));

        mockMvc.perform(get("/api/v1/dinosaur"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("T-Rex", "Raptor")));
    }

    @Test
    void getDinosaurs_returnsAllFields() throws Exception {
        mockMvc.perform(post("/api/v1/dinosaur")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(buildRequest("T-Rex", "Tyrannosaurus", past, future))));

        mockMvc.perform(get("/api/v1/dinosaur"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].name", is("T-Rex")))
                .andExpect(jsonPath("$[0].species", is("Tyrannosaurus")))
                .andExpect(jsonPath("$[0].status", is("ALIVE")));
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/dinosaur/{id}
    // -------------------------------------------------------------------------

    @Test
    void getDinosaur_returns200_withCorrectFields_whenFound() throws Exception {
        mockMvc.perform(post("/api/v1/dinosaur")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(buildRequest("T-Rex", "Tyrannosaurus", past, future))));

        Long id = savedId();

        mockMvc.perform(get("/api/v1/dinosaur/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.intValue())))
                .andExpect(jsonPath("$.name", is("T-Rex")))
                .andExpect(jsonPath("$.species", is("Tyrannosaurus")))
                .andExpect(jsonPath("$.status", is("ALIVE")));
    }

    @Test
    void getDinosaur_returns404_whenNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/dinosaur/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    // -------------------------------------------------------------------------
    // PUT /api/v1/dinosaur/{id}
    // -------------------------------------------------------------------------

    @Test
    void updateDinosaur_returns200_whenDinosaurExists() throws Exception {
        mockMvc.perform(post("/api/v1/dinosaur")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(buildRequest("T-Rex", "Tyrannosaurus", past, future))));

        mockMvc.perform(put("/api/v1/dinosaur/{id}", savedId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(buildRequest("T-Rex Updated", "New Species", past, future, "ENDANGERED"))))
                .andExpect(status().isOk());
    }

    @Test
    void updateDinosaur_persistsChanges() throws Exception {
        mockMvc.perform(post("/api/v1/dinosaur")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(buildRequest("T-Rex", "Tyrannosaurus", past, future))));

        Long id = savedId();

        mockMvc.perform(put("/api/v1/dinosaur/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(buildRequest("T-Rex Updated", "New Species", past, future, "ENDANGERED"))));

        mockMvc.perform(get("/api/v1/dinosaur/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("T-Rex Updated")))
                .andExpect(jsonPath("$.species", is("New Species")))
                .andExpect(jsonPath("$.status", is("ENDANGERED")));
    }

    @Test
    void updateDinosaur_returns404_whenNotFound() throws Exception {
        mockMvc.perform(put("/api/v1/dinosaur/{id}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(buildRequest("T-Rex", "Tyrannosaurus", past, future))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", containsString("9999")));
    }

    @Test
    void updateDinosaur_returns422_whenDinosaurIsExtinct() throws Exception {
        mockMvc.perform(post("/api/v1/dinosaur")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(buildRequest("T-Rex", "Tyrannosaurus", past, future))));

        Long id = savedId();

        mockMvc.perform(put("/api/v1/dinosaur/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(buildRequest("T-Rex", "Tyrannosaurus", past, future, "EXTINCT"))));

        mockMvc.perform(put("/api/v1/dinosaur/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(buildRequest("T-Rex", "Tyrannosaurus", past, future, "ALIVE"))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code", is(422)))
                .andExpect(jsonPath("$.message", containsString("Cannot update an extinct dinosaur")));
    }

    @Test
    void updateDinosaur_returns422_whenInvalidDates() throws Exception {
        mockMvc.perform(post("/api/v1/dinosaur")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(buildRequest("T-Rex", "Tyrannosaurus", past, future))));

        mockMvc.perform(put("/api/v1/dinosaur/{id}", savedId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(buildRequest("T-Rex", "Tyrannosaurus", future, past, "ALIVE"))))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateDinosaur_canTransitionToExtinct() throws Exception {
        mockMvc.perform(post("/api/v1/dinosaur")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(buildRequest("T-Rex", "Tyrannosaurus", past, future))));

        Long id = savedId();

        mockMvc.perform(put("/api/v1/dinosaur/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(buildRequest("T-Rex", "Tyrannosaurus", past, future, "EXTINCT"))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/dinosaur/{id}", id))
                .andExpect(jsonPath("$.status", is("EXTINCT")));
    }

    // -------------------------------------------------------------------------
    // DELETE /api/v1/dinosaur/{id}
    // -------------------------------------------------------------------------

    @Test
    void deleteDinosaur_returns200_whenFound() throws Exception {
        mockMvc.perform(post("/api/v1/dinosaur")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(buildRequest("T-Rex", "Tyrannosaurus", past, future))));

        mockMvc.perform(delete("/api/v1/dinosaur/{id}", savedId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteDinosaur_removesFromDatabase() throws Exception {
        mockMvc.perform(post("/api/v1/dinosaur")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(buildRequest("T-Rex", "Tyrannosaurus", past, future))));

        Long id = savedId();
        mockMvc.perform(delete("/api/v1/dinosaur/{id}", id));

        mockMvc.perform(get("/api/v1/dinosaur/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteDinosaur_returns404_whenNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/dinosaur/{id}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is(404)))
                .andExpect(jsonPath("$.message", containsString("9999")));
    }

    @Test
    void deleteDinosaur_canDeleteExtinctDinosaur() throws Exception {
        mockMvc.perform(post("/api/v1/dinosaur")
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(buildRequest("T-Rex", "Tyrannosaurus", past, future))));

        Long id = savedId();

        mockMvc.perform(put("/api/v1/dinosaur/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(buildRequest("T-Rex", "Tyrannosaurus", past, future, "EXTINCT"))));

        mockMvc.perform(delete("/api/v1/dinosaur/{id}", id))
                .andExpect(status().isNoContent());
    }
}
