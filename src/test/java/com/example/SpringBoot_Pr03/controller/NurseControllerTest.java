package com.example.SpringBoot_Pr03.controller;

import com.example.SpringBoot_Pr03.dao.NurseRepository;
import entity.Nurse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NurseController.class)
class NurseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NurseRepository nurseRepository;

    private Nurse testNurse;

    @BeforeEach
    void setUp() {
        testNurse = new Nurse(1, "Alice", "aliceUser", "password123");
    }

    @Test
    void getAllUsers_ShouldReturnNurseList() throws Exception {
        when(nurseRepository.findAll()).thenReturn(List.of(testNurse));

        mockMvc.perform(MockMvcRequestBuilders.get("/nurse/index"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Alice")));
    }

    @Test
    void login_ValidCredentials_ShouldReturnTrue() throws Exception {
        when(nurseRepository.findByUser("aliceUser")).thenReturn(testNurse);

        mockMvc.perform(MockMvcRequestBuilders.post("/nurse/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"user\":\"aliceUser\", \"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void login_InvalidPassword_ShouldReturnUnauthorized() throws Exception {
        when(nurseRepository.findByUser("aliceUser")).thenReturn(testNurse);

        mockMvc.perform(MockMvcRequestBuilders.post("/nurse/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"user\":\"aliceUser\", \"password\":\"wrongPassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("false"));
    }

    @Test
    void findByName_ShouldReturnNurse() throws Exception {
        when(nurseRepository.findByName("Alice")).thenReturn(testNurse);

        mockMvc.perform(MockMvcRequestBuilders.get("/nurse/name/Alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Alice")));
    }

    @Test
    void findById_ShouldReturnNurse() throws Exception {
        when(nurseRepository.findById(1)).thenReturn(Optional.of(testNurse));

        mockMvc.perform(MockMvcRequestBuilders.get("/nurse/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nurseId", is(1)));
    }

    /*
     * @Test
     * void updateNurse_ShouldReturnUpdatedNurse() throws Exception {
     * when(nurseRepository.findById(1)).thenReturn(Optional.of(testNurse));
     * when(nurseRepository.save(any(Nurse.class))).thenReturn(testNurse);
     * 
     * mockMvc.perform(MockMvcRequestBuilders.put("/nurse/1")
     * .contentType(MediaType.APPLICATION_JSON)
     * .content("{\"name\":\"UpdatedName\"}"))
     * .andExpect(status().isOk())
     * .andExpect(jsonPath("$.name", is("UpdatedName")));
     * }
     */

    @Test
    void createNurse_ShouldReturnCreatedNurse() throws Exception {
        when(nurseRepository.findByName("Alice")).thenReturn(null);
        when(nurseRepository.save(any(Nurse.class))).thenReturn(testNurse);

        mockMvc.perform(MockMvcRequestBuilders.post("/nurse/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Alice\", \"user\":\"aliceUser\", \"password\":\"password123\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Alice")));
    }

    @Test
    void createNurse_ExistingName_ShouldReturnConflict() throws Exception {
        when(nurseRepository.findByName("Alice")).thenReturn(testNurse);

        mockMvc.perform(MockMvcRequestBuilders.post("/nurse/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Alice\", \"user\":\"aliceUser\", \"password\":\"password123\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void deleteNurse_ShouldReturnNoContent() throws Exception {
        when(nurseRepository.findById(1)).thenReturn(Optional.of(testNurse));
        Mockito.doNothing().when(nurseRepository).delete(testNurse);

        mockMvc.perform(MockMvcRequestBuilders.delete("/nurse/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteNurse_NurseNotFound_ShouldReturnNotFound() throws Exception {
        when(nurseRepository.findById(2)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.delete("/nurse/2"))
                .andExpect(status().isNotFound());
    }
}
