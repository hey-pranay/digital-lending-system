package com.sprint.lending.service;

import com.sprint.lending.dto.response.RepaymentResponse;
import com.sprint.lending.entity.Loan;
import com.sprint.lending.entity.LoanStatus;
import com.sprint.lending.entity.Repayment;
import com.sprint.lending.entity.RepaymentStatus;
import com.sprint.lending.exception.LoanNotFoundException;
import com.sprint.lending.exception.RepaymentNotFoundException;
import com.sprint.lending.repository.LoanRepository;
import com.sprint.lending.repository.RepaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RepaymentService {

    private final RepaymentRepository repaymentRepository;
    private final LoanRepository loanRepository;
    private final EmiCalculator emiCalculator;

    public RepaymentService(
            RepaymentRepository repaymentRepository,
            LoanRepository loanRepository,
            EmiCalculator emiCalculator) {

        this.repaymentRepository = repaymentRepository;
        this.loanRepository = loanRepository;
        this.emiCalculator = emiCalculator;
    }

    // API 6: Generate repayment schedule
    @Transactional
    public List<RepaymentResponse> generateRepaymentSchedule(Long loanId) {

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException(
                        "Loan not found with ID: " + loanId
                ));

        if (loan.getStatus() != LoanStatus.APPROVED) {
            throw new IllegalStateException(
                    "Repayment schedule can only be generated for approved loans");
        }

        if (!repaymentRepository.findByLoanId(loanId).isEmpty()) {
            throw new IllegalStateException(
                    "Repayment schedule already exists for this loan");
        }

        BigDecimal emi = emiCalculator.calculateEmi(
                loan.getLoanAmount(),
                loan.getInterestRate(),
                loan.getTenureMonths()
        );

        LocalDate firstDueDate = LocalDate.now().plusMonths(1);

        List<Repayment> repayments = new ArrayList<>();

        for (int i = 0; i < loan.getTenureMonths(); i++) {

            Repayment repayment = new Repayment();

            repayment.setLoan(loan);
            repayment.setAmount(emi);
            repayment.setDueDate(firstDueDate.plusMonths(i));
            repayment.setStatus(RepaymentStatus.PENDING);

            repayment.setInstalmentNumber(i + 1);

            repayments.add(repayment);
        }

        return repaymentRepository.saveAll(repayments)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // API 7: View repayments for a particular loan
    public List<RepaymentResponse> getRepaymentsByLoan(Long loanId) {

        loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException(
                        "Loan not found with ID: " + loanId
                ));

        return repaymentRepository.findByLoanId(loanId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // API 8: View repayment by ID
    public RepaymentResponse getRepaymentById(Long repaymentId) {

        Repayment repayment = repaymentRepository.findById(repaymentId)
                .orElseThrow(() -> new RepaymentNotFoundException(
                        "Repayment not found with ID: " + repaymentId
                ));

        return mapToResponse(repayment);
    }

    // API 9: Filter repayments by status

    // API 9: Filter repayments by status
    @Transactional
    public List<RepaymentResponse> getRepaymentsByStatus(
            RepaymentStatus status) {

        // Update overdue repayments before fetching records.
        updateOverdueRepayments();

        return repaymentRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // API 10: Mark repayment as paid
    @Transactional
    public RepaymentResponse markRepaymentAsPaid(Long repaymentId) {

        Repayment repayment = repaymentRepository.findById(repaymentId)
                .orElseThrow(() -> new RepaymentNotFoundException(
                        "Repayment not found with ID: " + repaymentId
                ));

        if (repayment.getStatus() != RepaymentStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending repayments can be marked as paid");
        }

        repayment.setStatus(RepaymentStatus.PAID);
        repayment.setPaymentDate(LocalDate.now());

        Repayment savedRepayment = repaymentRepository.save(repayment);

        return mapToResponse(savedRepayment);
    }


    // Fetch repayment history for the logged-in borrower.
    @Transactional
    public List<RepaymentResponse> getMyRepayments(String email) {

        // First, update pending repayments whose due dates have passed.
        updateOverdueRepayments();

        // Then fetch the latest repayment records.
        return repaymentRepository
                .findByLoan_BorrowerProfile_User_EmailOrderByDueDateAsc(email)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // Convert Repayment entity into response DTO
    private RepaymentResponse mapToResponse(Repayment repayment) {

        return new RepaymentResponse(
                repayment.getId(),
                repayment.getLoan().getId(),
                repayment.getAmount(),
                repayment.getDueDate(),
                repayment.getPaymentDate(),
                repayment.getStatus().name()
        );
    }


    // Automatically update pending repayments whose due dates have passed.
    @Transactional
    public void updateOverdueRepayments() {

        List<Repayment> overdueRepayments =
                repaymentRepository.findByStatusAndDueDateBefore(
                        RepaymentStatus.PENDING,
                        LocalDate.now()
                );

        for (Repayment repayment : overdueRepayments) {
            repayment.setStatus(RepaymentStatus.OVERDUE);
        }

        repaymentRepository.saveAll(overdueRepayments);
    }


    // Mark an instalment as paid by the logged-in borrower.
    @Transactional
    public RepaymentResponse markMyRepaymentAsPaid(
            Long repaymentId, String email) {

        // Find the repayment.
        Repayment repayment = repaymentRepository.findById(repaymentId)
                .orElseThrow(() -> new RepaymentNotFoundException(
                        "Repayment not found with ID: " + repaymentId
                ));

        // Verify that this repayment belongs to the logged-in borrower.
        String borrowerEmail = repayment.getLoan()
                .getBorrowerProfile()
                .getUser()
                .getEmail();

        if (!borrowerEmail.equalsIgnoreCase(email)) {
            throw new IllegalStateException(
                    "You are not authorized to pay this repayment");
        }

        // Update overdue status before checking payment eligibility.
        updateOverdueRepayments();

        // Allow payment only for pending or overdue instalments.
        if (repayment.getStatus() != RepaymentStatus.PENDING &&
                repayment.getStatus() != RepaymentStatus.OVERDUE) {

            throw new IllegalStateException(
                    "This repayment has already been paid or cannot be paid");
        }

        // Update payment details.
        repayment.setStatus(RepaymentStatus.PAID);
        repayment.setPaymentDate(LocalDate.now());

        Repayment savedRepayment = repaymentRepository.save(repayment);

        // Check whether all instalments of this loan are paid.
        List<Repayment> allRepayments =
                repaymentRepository.findByLoanId(
                        repayment.getLoan().getId()
                );

        boolean allPaid = !allRepayments.isEmpty()
                && allRepayments.stream().allMatch(
                r -> r.getStatus() == RepaymentStatus.PAID
        );

        // Close the loan after the final instalment is paid.
        if (allPaid) {
            Loan loan = repayment.getLoan();
            loan.setStatus(LoanStatus.CLOSED);
            loanRepository.save(loan);
        }

        return mapToResponse(savedRepayment);
    }


}