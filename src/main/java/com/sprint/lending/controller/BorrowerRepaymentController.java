
package com.sprint.lending.controller;

import com.sprint.lending.dto.response.RepaymentResponse;
import com.sprint.lending.service.RepaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrower/repayments")
@PreAuthorize("hasRole('BORROWER')")
public class BorrowerRepaymentController {

    private final RepaymentService repaymentService;

    public BorrowerRepaymentController(RepaymentService repaymentService) {
        this.repaymentService = repaymentService;
    }

    // Fetch repayment history of the logged-in borrower.
    @GetMapping
    public ResponseEntity<List<RepaymentResponse>> getMyRepayments(
            Authentication authentication) {

        List<RepaymentResponse> repayments =
                repaymentService.getMyRepayments(authentication.getName());

        return ResponseEntity.ok(repayments);
    }

    // Pay an instalment belonging to the logged-in borrower.
    @PatchMapping("/{repaymentId}/pay")
    public ResponseEntity<RepaymentResponse> payMyRepayment(
            @PathVariable Long repaymentId,
            Authentication authentication) {

        RepaymentResponse response =
                repaymentService.markMyRepaymentAsPaid(
                        repaymentId,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }
}