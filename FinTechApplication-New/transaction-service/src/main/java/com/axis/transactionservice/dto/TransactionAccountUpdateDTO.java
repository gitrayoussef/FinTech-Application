package com.axis.transactionservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionAccountUpdateDTO {
    @NotBlank(message = "Account ID cannot be blank")
    private String accountId;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private Double amount;

    @NotNull(message = "Operation cannot be null")
    @Pattern(regexp = "^(deposit|withdraw)$", message = "Invalid operation. Must be 'deposit' or 'withdraw'")
    private String operation;
}
