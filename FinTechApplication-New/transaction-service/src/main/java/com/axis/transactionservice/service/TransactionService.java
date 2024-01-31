package com.axis.transactionservice.service;

import com.axis.transactionservice.dto.TransactionAccountUpdateDTO;
import com.axis.transactionservice.dto.TransactionDTO;
import com.axis.transactionservice.model.Transaction;
import com.axis.transactionservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final WebClient.Builder webClientBuilder;
    private final String accountServiceUri = "http://account-service/api/account/";

    public Long deposit(TransactionDTO transactionDTO) {
        try {
            Boolean isAccountExist = webClientBuilder.build().get().uri(accountServiceUri + transactionDTO.getAccountId()).retrieve().bodyToMono(Boolean.class).block();

            if (Boolean.TRUE.equals(isAccountExist)) {
                Transaction transaction = Transaction.builder().accountId(transactionDTO.getAccountId()).amount(transactionDTO.getAmount()).type("DEPOSIT").build();
                transactionRepository.save(transaction);

                TransactionAccountUpdateDTO transactionAccountUpdateDTO=mapTransactionDTOS(transactionDTO,"deposit");
                updateAccountBalance(transactionAccountUpdateDTO);

                return transaction.getId();
            } else {
                throw new IllegalArgumentException("Account doesn't exist. Please open an account and try again later");
            }
        } catch (Exception e) {
            log.error("Error during deposit transaction", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error occurred", e);
        }
    }

    public Long withdraw(TransactionDTO transactionDTO) {
        try {
            Boolean isAccountExist = webClientBuilder.build().get().uri(accountServiceUri + transactionDTO.getAccountId()).retrieve().bodyToMono(Boolean.class).block();

            if (Boolean.TRUE.equals(isAccountExist)) {
                Transaction transaction = Transaction.builder().accountId(transactionDTO.getAccountId()).amount(transactionDTO.getAmount()).type("WITHDRAW").build();
                transactionRepository.save(transaction);

                TransactionAccountUpdateDTO transactionAccountUpdateDTO=mapTransactionDTOS(transactionDTO,"withdraw");
                updateAccountBalance(transactionAccountUpdateDTO);

                return transaction.getId();
            } else {
                throw new IllegalArgumentException("Account doesn't exist. Please open an account and try again later");
            }
        } catch (Exception e) {
            log.error("Error during withdraw transaction", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error occurred", e);
        }
    }

    private void updateAccountBalance(TransactionAccountUpdateDTO transactionAccountUpdateDTO) {
        try {
            webClientBuilder.build().put()
                    .uri(accountServiceUri + "/updateBalance")
                    .bodyValue(transactionAccountUpdateDTO)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            log.error("Error updating account balance", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating account balance", e);
        }
    }

    private TransactionAccountUpdateDTO mapTransactionDTOS(TransactionDTO transactionDTO, String operation) {
        TransactionAccountUpdateDTO transactionAccountUpdateDTO = new TransactionAccountUpdateDTO();
        transactionAccountUpdateDTO.setAccountId(transactionDTO.getAccountId());
        transactionAccountUpdateDTO.setAmount(transactionDTO.getAmount());
        transactionAccountUpdateDTO.setOperation(operation);
        return transactionAccountUpdateDTO;

    }
}
