package com.example.skillsh.controllers;


import com.example.skillsh.domain.dto.payment.OfferRequestDTO;
import com.example.skillsh.domain.dto.payment.OfferResponseDTO;
import com.example.skillsh.services.AdminServiceOfferService;
import com.example.skillsh.web.AdminServiceOfferController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminServiceOfferController.class)
@AutoConfigureMockMvc(addFilters = false) // Изключваме Spring Security
class AdminServiceOfferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminServiceOfferService adminServiceOfferService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void testGetAllOffers_ShouldReturn200AndList() throws Exception {
        // Arrange
        OfferResponseDTO responseDto = new OfferResponseDTO();
        responseDto.setId(10L);
        responseDto.setTitle("Урок по програмиране");
        responseDto.setPrice(150.0);

        when(adminServiceOfferService.getAllOffers()).thenReturn(List.of(responseDto));

        // Act & Assert
        mockMvc.perform(get("/api/admin/offers/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(10L))
                .andExpect(jsonPath("$[0].title").value("Урок по програмиране"))
                .andExpect(jsonPath("$[0].price").value(150.0));

        verify(adminServiceOfferService, times(1)).getAllOffers();
    }

    @Test
    void testCreateOffer_ShouldReturn200AndCreatedOffer() throws Exception {
        // Arrange
        OfferRequestDTO requestDto = new OfferRequestDTO();
        requestDto.setTitle("Ремонт на компютри");
        requestDto.setPrice(80.0);
        requestDto.setSellerId(5L);

        OfferResponseDTO responseDto = new OfferResponseDTO();
        responseDto.setId(1L);
        responseDto.setTitle("Ремонт на компютри");
        responseDto.setPrice(80.0);

        when(adminServiceOfferService.createOffer(any(OfferRequestDTO.class))).thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(post("/api/admin/offers/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Ремонт на компютри"))
                .andExpect(jsonPath("$.price").value(80.0));

        verify(adminServiceOfferService, times(1)).createOffer(any(OfferRequestDTO.class));
    }

    @Test
    void testUpdateOffer_ShouldReturn200() throws Exception {
        // Arrange
        Long offerId = 1L;
        OfferRequestDTO requestDto = new OfferRequestDTO();
        requestDto.setTitle("Обновено заглавие");
        requestDto.setPrice(90.0);

        OfferResponseDTO responseDto = new OfferResponseDTO();
        responseDto.setId(offerId);
        responseDto.setTitle("Обновено заглавие");
        responseDto.setPrice(90.0);

        when(adminServiceOfferService.updateOffer(eq(offerId), any(OfferRequestDTO.class)))
                .thenReturn(responseDto);

        // Act & Assert
        mockMvc.perform(put("/api/admin/offers/update/{id}", offerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Обновено заглавие"))
                .andExpect(jsonPath("$.price").value(90.0));

        verify(adminServiceOfferService, times(1)).updateOffer(eq(offerId), any(OfferRequestDTO.class));
    }

    @Test
    void testDeleteOffer_ShouldReturn200() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/admin/offers/delete/{id}", 1L))
                .andExpect(status().isOk());

        verify(adminServiceOfferService, times(1)).deleteOffer(1L);
    }
}


