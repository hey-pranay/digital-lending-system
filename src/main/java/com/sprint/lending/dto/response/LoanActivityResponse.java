
package com.sprint.lending.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LoanActivityResponse(
        String activityType,
        String description,
        Long loanId,
        BigDecimal amount,
        LocalDateTime activityDate,
        String status
) {
}