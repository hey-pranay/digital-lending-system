
package com.sprint.lending.dto.request;

import com.sprint.lending.entity.EmploymentType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record BorrowerProfileRequest(

        @NotBlank(message = "Phone number is required")
        @Pattern(
                regexp = "^[0-9+()\\- ]{7,15}$",
                message = "Enter a valid phone number"
        )
        String phone,

        @NotBlank(message = "Address is required")
        @Size(max = 255, message = "Address cannot exceed 255 characters")
        String address,

        @NotNull(message = "Monthly income is required")
        @DecimalMin(
                value = "0.01",
                message = "Monthly income must be greater than zero"
        )
        BigDecimal monthlyIncome,

        @NotNull(message = "Employment type is required")
        EmploymentType employmentType

) {
}
