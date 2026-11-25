
package com.sprint.lending.controller;

import com.sprint.lending.dto.request.BorrowerProfileRequest;
import com.sprint.lending.service.BorrowerProfileService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrower/profile")
public class BorrowerProfileController {

    private final BorrowerProfileService borrowerProfileService;

    public BorrowerProfileController(
            BorrowerProfileService borrowerProfileService) {

        this.borrowerProfileService = borrowerProfileService;
    }

    @PostMapping
    public ResponseEntity<String> createProfile(
            @Valid @RequestBody BorrowerProfileRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        borrowerProfileService.createProfile(email, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Borrower profile created successfully");
    }
}
