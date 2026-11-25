package com.sprint.lending.controller;

import com.sprint.lending.dto.response.LoanDashboardResponse;
import com.sprint.lending.dto.response.RepaymentHistoryResponse;
import com.sprint.lending.service.BorrowerOverviewService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
@PreAuthorize("hasRole('BORROWER')")
public class BorrowerOverviewController {

    private final BorrowerOverviewService borrowerOverviewService;

    public BorrowerOverviewController(
            BorrowerOverviewService borrowerOverviewService) {

        this.borrowerOverviewService = borrowerOverviewService;
    }

    // API 3: Borrower dashboard
    @GetMapping("/dashboard")
    public ResponseEntity<LoanDashboardResponse> getDashboard(
            Authentication authentication) {

        return ResponseEntity.ok(
                borrowerOverviewService.getDashboard(
                        authentication.getName()
                )
        );
    }

    // API 4: Borrower repayment history
    @GetMapping("/repayments")
    public ResponseEntity<RepaymentHistoryResponse> getRepaymentHistory(
            Authentication authentication) {

        return ResponseEntity.ok(
                borrowerOverviewService.getRepaymentHistory(
                        authentication.getName()
                )
        );
    }
}