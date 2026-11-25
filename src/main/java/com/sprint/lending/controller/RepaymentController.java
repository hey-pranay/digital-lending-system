package com.sprint.lending.controller;

import com.sprint.lending.dto.response.RepaymentResponse;
import com.sprint.lending.entity.RepaymentStatus;
import com.sprint.lending.service.RepaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/repayments")
public class RepaymentController {

    private final RepaymentService repaymentService;

    public RepaymentController(RepaymentService repaymentService) {
        this.repaymentService = repaymentService;
    }

    // API 6: Generate repayment schedule
    @PostMapping("/loans/{loanId}/generate")
    public ResponseEntity<List<RepaymentResponse>> generateRepaymentSchedule(
            @PathVariable Long loanId) {

        return ResponseEntity.ok(
                repaymentService.generateRepaymentSchedule(loanId));
    }

    // API 7: View repayments for a particular loan
    @GetMapping("/loans/{loanId}")
    public ResponseEntity<List<RepaymentResponse>> getRepaymentsByLoan(
            @PathVariable Long loanId) {

        return ResponseEntity.ok(
                repaymentService.getRepaymentsByLoan(loanId));
    }

    // API 8: View individual repayment
    @GetMapping("/{repaymentId}")
    public ResponseEntity<RepaymentResponse> getRepaymentById(
            @PathVariable Long repaymentId) {

        return ResponseEntity.ok(
                repaymentService.getRepaymentById(repaymentId));
    }

    // API 9: Filter repayments by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<RepaymentResponse>> getRepaymentsByStatus(
            @PathVariable RepaymentStatus status) {

        return ResponseEntity.ok(
                repaymentService.getRepaymentsByStatus(status));
    }

    // API 10: Mark repayment as paid
    @PatchMapping("/{repaymentId}/pay")
    public ResponseEntity<RepaymentResponse> markRepaymentAsPaid(
            @PathVariable Long repaymentId) {

        return ResponseEntity.ok(
                repaymentService.markRepaymentAsPaid(repaymentId));
    }
}
