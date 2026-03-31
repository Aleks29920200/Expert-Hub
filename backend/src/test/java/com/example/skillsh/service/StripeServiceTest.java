package com.example.skillsh.service;

import com.example.skillsh.services.payment.StripeService;
import com.stripe.Stripe;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;


class StripeServiceTest {

    @Test
    void testInit_SetsStripeApiKey() {
        // 1. Arrange (Подготовка)
        StripeService stripeService = new StripeService();
        String mockApiKey = "sk_test_12345_mock_key";

        // Инжектираме стойността на private полето, което по принцип се пълни от @Value("${stripe.api.key}")
        ReflectionTestUtils.setField(stripeService, "stripeSecretKey", mockApiKey);

        // 2. Act (Изпълнение)
        // Извикваме ръчно метода, който иначе Spring извиква автоматично заради @PostConstruct
        stripeService.init();

        // 3. Assert (Проверка)
        // Проверяваме дали глобалната променлива на Stripe е успешно обновена
        assertEquals(mockApiKey, Stripe.apiKey);
    }
}




