package com.sprint.lending.dto.response;

import java.math.BigDecimal;

public record LoanResponse(

        Long id,
        BigDecimal amount,
        BigDecimal interestRate,
        Integer tenureMonths,
        BigDecimal emi,
        String status,
        String remark

) {
}