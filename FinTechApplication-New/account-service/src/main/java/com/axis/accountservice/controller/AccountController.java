package com.axis.accountservice.controller;

import com.axis.accountservice.dto.AccountDTO;
import com.axis.accountservice.dto.UpdateAccountBalanceDTO;
import com.axis.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@Validated
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> openAccount(@Valid @RequestBody AccountDTO accountDTO) {
        try {
            String accountId = accountService.openAccount(accountDTO).getId();
            return ResponseEntity.status(HttpStatus.CREATED).body(accountId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error opening account: " + e.getMessage());
        }
    }

    @GetMapping("{accountId}/balance")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> checkBalance(@PathVariable String accountId) {
        try {
            double balance = accountService.checkBalance(accountId).getBalance();
            return ResponseEntity.status(HttpStatus.OK).body(balance);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error checking balance: " + e.getMessage());
        }
    }

    @GetMapping("{accountId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> checkAccountExistence(@PathVariable String accountId) {
        try {
            boolean exists = accountService.checkAccountExistence(accountId);
            return ResponseEntity.status(HttpStatus.OK).body(exists);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error checking account existence: " + e.getMessage());
        }
    }

    @PutMapping("/updateBalance")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateBalance(@Valid @RequestBody UpdateAccountBalanceDTO updateDTO) {
        try {
            accountService.updateBalance(updateDTO);
            return ResponseEntity.status(HttpStatus.OK).body("Balance updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating balance: " + e.getMessage());
        }
    }

    // Exception handler for validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation error: " + e.getBindingResult().getFieldError().getDefaultMessage());
    }

    // Exception handler for unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error occurred: " + e.getMessage());
    }
}
