package com.ai.codegeneration.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ai.codegeneration.api.model.PaymentRequest;
import com.ai.codegeneration.api.service.PaymentService;
import com.ai.codegeneration.api.entity.TransactionEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class MainControllerTest {

    @MockBean
    private PaymentService paymentService;
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testInitiatePayment() throws Exception {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setPaymentAmount(250.00);
        paymentRequest.setCardNumber("5555 1234 5678 9012");
        paymentRequest.setExpiryDate("10/28");
        paymentRequest.setCvv("662");
        
        mockMvc.perform(post("/api/payments/initiate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(paymentRequest)))
                .andExpect(status().isOk());
    }
    
    @Test
    public void testGetTransactionHistory() throws Exception {
        Long userId = 123456L;
        
        // Mock the service call
        List<TransactionEntity> mockTransactionHistory = createMockTransactionHistory();
        
        Mockito.when(paymentService.retrieveTransactionHistory(userId)).thenReturn(mockTransactionHistory);
        
        // Perform the GET request and assert the response
        MvcResult result = mockMvc.perform(get("/api/transactions/history")
                .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andReturn();
        
        String responseJson = result.getResponse().getContentAsString();
        List<TransactionEntity> responseTransactionHistory = new ObjectMapper().readValue(responseJson, new TypeReference<List<TransactionEntity>>() {});
        
        // Recursive assertion to verify that response is the same as mockTransactionHistory
        assertThat(responseTransactionHistory).usingRecursiveComparison().isEqualTo(mockTransactionHistory);
    }

    private List<TransactionEntity> createMockTransactionHistory() {
        List<TransactionEntity> mockTransactionHistory = new ArrayList<>();
        
        TransactionEntity transaction1 = new TransactionEntity();
        transaction1.setId(1L);
        transaction1.setPaymentAmount(100.00);
        transaction1.setCardLastFourDigits("1234");
        transaction1.setCurrency("USD");
        // Set other properties with random values
        
        TransactionEntity transaction2 = new TransactionEntity();
        transaction2.setId(2L);
        transaction2.setPaymentAmount(200.00);
        transaction2.setCardLastFourDigits("5678");
        transaction2.setCurrency("USD");
        // Set other properties with random values
        
        mockTransactionHistory.add(transaction1);
        mockTransactionHistory.add(transaction2);
        
        return mockTransactionHistory;
    }

}
