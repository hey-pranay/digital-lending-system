package com.sprint.lending.dto.response;

import java.math.BigDecimal;

public record LoanDashboardResponse(

        BigDecimal totalLoanAmount,

        BigDecimal outstandingBalance,

        BigDecimal monthlyEmi,

        String loanStatus

) {
}