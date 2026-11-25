package com.sprint.lending.dto.response;

import com.sprint.lending.entity.EmploymentType;
import java.math.BigDecimal;

public record BorrowerProfileResponse(
        Long id,
        String phone,
        String address,
        BigDecimal monthlyIncome,
        EmploymentType employmentType
) {
}