package com.sprint.lending.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RepaymentResponse(
        Long repaymentId,
        Long loanId,
        BigDecimal amount,
        LocalDate dueDate,
        LocalDate paymentDate,
        String status
) {
}