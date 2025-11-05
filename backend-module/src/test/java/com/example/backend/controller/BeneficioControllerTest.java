package com.example.backend.controller;

import com.example.backend.dto.BeneficioDTO;
import com.example.backend.dto.TransferRequestDTO;
import com.example.backend.service.BeneficioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeneficioController.class)
class BeneficioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BeneficioService service;

    @Test
    void testFindAll() throws Exception {
        BeneficioDTO dto1 = new BeneficioDTO(1L, "Beneficio A", "Descrição A", 
                                             new BigDecimal("1000.00"), true, 0L);
        BeneficioDTO dto2 = new BeneficioDTO(2L, "Beneficio B", "Descrição B", 
                                             new BigDecimal("500.00"), true, 0L);

        when(service.findAll()).thenReturn(Arrays.asList(dto1, dto2));

        mockMvc.perform(get("/api/v1/beneficios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Beneficio A"))
                .andExpect(jsonPath("$[1].nome").value("Beneficio B"));
    }

    @Test
    void testFindById() throws Exception {
        BeneficioDTO dto = new BeneficioDTO(1L, "Beneficio A", "Descrição A", 
                                            new BigDecimal("1000.00"), true, 0L);

        when(service.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/beneficios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Beneficio A"));
    }

    @Test
    void testCreate() throws Exception {
        BeneficioDTO dto = new BeneficioDTO(null, "Novo Beneficio", "Nova Descrição", 
                                            new BigDecimal("750.00"), true, null);
        BeneficioDTO created = new BeneficioDTO(3L, "Novo Beneficio", "Nova Descrição", 
                                                new BigDecimal("750.00"), true, 0L);

        when(service.create(any(BeneficioDTO.class))).thenReturn(created);

        mockMvc.perform(post("/api/v1/beneficios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    void testUpdate() throws Exception {
        BeneficioDTO dto = new BeneficioDTO(1L, "Beneficio Atualizado", "Descrição Atualizada", 
                                            new BigDecimal("1500.00"), true, 0L);

        when(service.update(eq(1L), any(BeneficioDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/v1/beneficios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Beneficio Atualizado"));
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/v1/beneficios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testTransfer() throws Exception {
        TransferRequestDTO request = new TransferRequestDTO(1L, 2L, new BigDecimal("200.00"));

        doNothing().when(service).transfer(any(TransferRequestDTO.class));

        mockMvc.perform(post("/api/v1/beneficios/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
