package com.axis.accountservice;

import com.axis.accountservice.dto.AccountDTO;
import com.axis.accountservice.dto.UpdateAccountBalanceDTO;
import com.axis.accountservice.model.Account;
import com.axis.accountservice.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class AccountServiceApplicationTests {
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AccountRepository accountRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    void openAccount_shouldReturnCreatedStatus() throws Exception {
        accountRepository.deleteAll();

        AccountDTO accountDTO = getAccountRequest();
        String accountRequestString = objectMapper.writeValueAsString(accountDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(accountRequestString))
                .andExpect(status().isCreated());

        assertThat(accountRepository.count()).isEqualTo(1);
    }

    @Test
    void checkBalance_shouldReturnOkStatus() throws Exception {
        Account account = new Account();
        account.setUsername("TestUser");
        account.setBalance(1000);
        accountRepository.save(account);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/account/{id}/balance", account.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private AccountDTO getAccountRequest() {
        return AccountDTO.builder().username("Rana").balance(1000).build();
    }

    @Test
    void updateBalance_shouldReturnOkStatusOnDeposit() throws Exception {
        Account account = new Account();
        account.setUsername("TestUser");
        account.setBalance(1000);
        accountRepository.save(account);
        UpdateAccountBalanceDTO updateDTO = new UpdateAccountBalanceDTO(account.getId(), 500.00,"deposit");
        String updateRequestString = objectMapper.writeValueAsString(updateDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/account/updateBalance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestString))
                .andExpect(status().isOk());

    }


    @Test
    void updateBalance_shouldReturnBadRequestStatusWhenInsufficientBalanceOnWithDraw() throws Exception {
        Account account = new Account();
        account.setUsername("TestUser");
        account.setBalance(1000);
        accountRepository.save(account);
        UpdateAccountBalanceDTO updateDTO = new UpdateAccountBalanceDTO(account.getId(), 1500.0,"withdraw");
        String updateRequestString = objectMapper.writeValueAsString(updateDTO);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/account/updateBalance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequestString))
                .andExpect(status().isBadRequest());
    }

}
