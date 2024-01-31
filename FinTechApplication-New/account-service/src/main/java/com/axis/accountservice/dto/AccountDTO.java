package com.axis.accountservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;






@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {


    @NotBlank(message = "Username cannot be blank")
    @Size(min = 4,max = 10)
    private String username;

    @Positive(message = "Balance must be a positive number")
    @Min(1000)
    private double balance;
}
