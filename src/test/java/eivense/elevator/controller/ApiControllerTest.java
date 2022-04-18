package eivense.elevator.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eivense.elevator.entity.passenger.Passenger;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void reset() throws Exception {
        mockMvc.perform(post("/reset").header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$",hasSize(3)));
    }

    @Test
    void workload()throws Exception {
        ObjectMapper mapper=new ObjectMapper();
        List<List<Passenger>> list= mapper.readValue(new File("./src/test/resources/tests.json"), new TypeReference<List<List<Passenger>>>() {
        });
        for (List<Passenger> passengers : list) {
            mockMvc.perform(post("/workload").header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(passengers)))
                            .andDo(print())
                    .andExpect(status().is2xxSuccessful());
        }
    }
}