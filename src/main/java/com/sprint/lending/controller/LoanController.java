package com.sprint.lending.controller;

import com.sprint.lending.dto.request.LoanRequest;
import com.sprint.lending.dto.response.LoanResponse;
import com.sprint.lending.service.LoanService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@PreAuthorize("hasRole('BORROWER')")
public class LoanController {

    private final LoanService loanService;

    // Constructor injection
    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // API 1: Submit loan application
    @PostMapping
    public ResponseEntity<LoanResponse> applyForLoan(
            @Valid @RequestBody LoanRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        LoanResponse response =
                loanService.applyForLoan(email, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // API 2: View borrower's loans
    @GetMapping
    public ResponseEntity<List<LoanResponse>> getMyLoans(
            Authentication authentication) {

        String email = authentication.getName();

        List<LoanResponse> loans =
                loanService.getMyLoans(email);

        return ResponseEntity.ok(loans);
    }
}