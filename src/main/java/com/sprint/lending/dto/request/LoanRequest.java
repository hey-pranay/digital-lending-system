package com.sprint.lending.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record LoanRequest(

        @NotNull(message = "Loan amount is required")
        @Positive(message = "Loan amount must be positive")
        @DecimalMin(
                value = "10000.00",
                message = "Minimum loan amount is 10000"
        )
        @DecimalMax(
                value = "1000000.00",
                message = "Maximum loan amount is 1000000"
        )
        @Digits(
                integer = 7,
                fraction = 2,
                message = "Invalid loan amount format"
        )
        BigDecimal amount,

        @NotNull(message = "Loan tenure is required")
        @Min(
                value = 3,
                message = "Minimum tenure is 3 months"
        )
        @Max(
                value = 60,
                message = "Maximum tenure is 60 months"
        )
        Integer tenureMonths

) {
}