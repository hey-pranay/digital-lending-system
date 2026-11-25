package com.sprint.lending.controller;

import com.sprint.lending.dto.request.RejectLoanRequest;
import com.sprint.lending.dto.response.LoanApplicationResponse;
import com.sprint.lending.entity.LoanStatus;
import com.sprint.lending.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/loans")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // API 1: View all loan applications
    @GetMapping
    public ResponseEntity<List<LoanApplicationResponse>> getAllApplications() {
        return ResponseEntity.ok(
                adminService.getAllApplications());
    }

    // API 2: Filter applications by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<LoanApplicationResponse>> getApplicationsByStatus(
            @PathVariable LoanStatus status) {

        return ResponseEntity.ok(
                adminService.getApplicationsByStatus(status));
    }

    // API 3: View individual loan application
    @GetMapping("/{loanId}")
    public ResponseEntity<LoanApplicationResponse> getApplicationById(
            @PathVariable Long loanId) {

        return ResponseEntity.ok(
                adminService.getApplicationById(loanId));
    }

    // API 4: Approve loan
    @PatchMapping("/{loanId}/approve")
    public ResponseEntity<LoanApplicationResponse> approveLoan(
            @PathVariable Long loanId) {

        return ResponseEntity.ok(
                adminService.approveLoan(loanId));
    }

    // API 5: Reject loan
    @PatchMapping("/{loanId}/reject")
    public ResponseEntity<LoanApplicationResponse> rejectLoan(
            @PathVariable Long loanId,
            @Valid @RequestBody RejectLoanRequest request) {

        return ResponseEntity.ok(
                adminService.rejectLoan(loanId, request));
    }
}
