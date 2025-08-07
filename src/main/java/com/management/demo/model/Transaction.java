package com.management.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("transactions")
public class Transaction {

    @Id
    private Long id;

    @NotNull
    private Long bookId;

    @NotNull
    private Long memberId;

    @NotNull
    private LocalDate issueDate;

    @NotNull
    private LocalDate dueDate;

    private LocalDate returnDate;

    private BigDecimal fineAmount = BigDecimal.ZERO;
    private String status = "ISSUED";  // ISSUED | RETURNED | OVERDUE
    private LocalDateTime createdAt;
}
