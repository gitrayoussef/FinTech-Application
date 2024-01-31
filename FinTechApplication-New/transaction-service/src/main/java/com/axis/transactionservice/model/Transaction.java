package com.axis.transactionservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "t_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String accountId;
    private double amount;
    private String type;
}
