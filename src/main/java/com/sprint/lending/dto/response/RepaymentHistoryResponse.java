package com.sprint.lending.dto.response;

import java.util.List;

public record RepaymentHistoryResponse(

        long totalInstalments,

        long paidInstalments,

        long pendingInstalments,

        long overdueInstalments,

        List<RepaymentResponse> repayments

) {
}