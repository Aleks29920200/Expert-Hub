package com.example.skillsh.controllers;

import com.example.skillsh.services.user.UserService;
import com.example.skillsh.web.ServiceOfferController;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebMvcTest(ServiceOfferController.class)
@AutoConfigureMockMvc(addFilters = false)
class ServiceOfferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Мокваме зависимостите, описани в конструктора
    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private UserService userService;

    // Секюрити фикс
    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void contextLoads() {
        // Контролерът в момента няма дефинирани ендпойнти,
        // затова просто тестваме успешното му инициализиране от Spring.
        assertNotNull(mockMvc);
    }
}
