package com.axis.accountservice.service;

import com.axis.accountservice.dto.AccountDTO;
import com.axis.accountservice.dto.AccountResponse;
import com.axis.accountservice.dto.UpdateAccountBalanceDTO;
import com.axis.accountservice.model.Account;
import com.axis.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountResponse openAccount(AccountDTO accountDTO) {
        Account account = Account.builder()
                .username(accountDTO.getUsername())
                .balance(accountDTO.getBalance())
                .build();
        accountRepository.save(account);
        log.info("Account {} is opened ", account.getId());
        return AccountResponse.builder().id(account.getId()).build();
    }

    public AccountResponse checkBalance(String id) {
        Optional<Account> account = accountRepository.findById(id);
        return account.map(acc -> AccountResponse.builder().balance(acc.getBalance()).build())
                .orElseThrow(() -> new IllegalArgumentException("Account doesn't exist. Please open an account and try again later."));
    }

    public boolean checkAccountExistence(String id) {
        boolean found = accountRepository.findById(id).isPresent();
        if (!found) {
            throw new IllegalArgumentException("Account doesn't exist. Please open an account and try again later.");
        }
        return true;
    }

    public void updateBalance(UpdateAccountBalanceDTO updateAccountBalanceDTO) {
        Account updatedAccount = accountRepository.findById(updateAccountBalanceDTO.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Account doesn't exist. Please open an account and try again later."));

        checkIsEnoughAccountBalance(updateAccountBalanceDTO, updatedAccount);
        double newBalance;
        if (updateAccountBalanceDTO.getOperation().equals("withdraw")) {
             newBalance = updatedAccount.getBalance() - updateAccountBalanceDTO.getAmount();
        } else {
             newBalance = updatedAccount.getBalance() + updateAccountBalanceDTO.getAmount();
        }
        updatedAccount.setBalance(newBalance);
        accountRepository.save(updatedAccount);
        log.info("Balance is {}", updatedAccount.getBalance());
    }

    private void checkIsEnoughAccountBalance(UpdateAccountBalanceDTO updateAccountBalanceDTO, Account updatedAccount) {
        if (updatedAccount.getBalance() < updateAccountBalanceDTO.getAmount()) {
            throw new IllegalArgumentException("Insufficient balance. Account doesn't have enough balance to complete the withdrawal.");
        }
    }
}
