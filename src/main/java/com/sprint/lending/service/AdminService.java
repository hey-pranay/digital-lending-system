package com.sprint.lending.service;

import com.sprint.lending.dto.request.RejectLoanRequest;
import com.sprint.lending.dto.response.LoanApplicationResponse;
import com.sprint.lending.entity.Loan;
import com.sprint.lending.entity.LoanStatus;
import com.sprint.lending.exception.LoanNotFoundException;
import com.sprint.lending.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdminService {

    private final LoanRepository loanRepository;
    private final EmiCalculator emiCalculator;

    public AdminService(
            LoanRepository loanRepository,
            EmiCalculator emiCalculator) {

        this.loanRepository = loanRepository;
        this.emiCalculator = emiCalculator;
    }

    // API 1: View all applications
    public List<LoanApplicationResponse> getAllApplications() {

        return loanRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // API 2: Filter applications by status
    public List<LoanApplicationResponse> getApplicationsByStatus(
            LoanStatus status) {

        return loanRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // API 3: View individual application
    public LoanApplicationResponse getApplicationById(Long loanId) {

        Loan loan = findLoanById(loanId);

        return mapToResponse(loan);
    }

    // API 4: Approve loan
    @Transactional
    public LoanApplicationResponse approveLoan(Long loanId) {

        Loan loan = findLoanById(loanId);

        if (loan.getStatus() != LoanStatus.APPLIED
                && loan.getStatus() != LoanStatus.PENDING) {

            throw new IllegalStateException(
                    "Only applied or pending loans can be approved");
        }

        loan.setStatus(LoanStatus.APPROVED);
        loan.setApprovalDate(LocalDateTime.now());
        loan.setRejectionRemarks(null);

        loan.setRejectionDate(null);

        Loan savedLoan = loanRepository.save(loan);

        return mapToResponse(savedLoan);
    }

    // API 5: Reject loan
    @Transactional
    public LoanApplicationResponse rejectLoan(
            Long loanId,
            RejectLoanRequest request) {

        Loan loan = findLoanById(loanId);

        if (loan.getStatus() != LoanStatus.APPLIED
                && loan.getStatus() != LoanStatus.PENDING) {

            throw new IllegalStateException(
                    "Only applied or pending loans can be rejected");
        }

        if (request == null
                || request.getRemarks() == null
                || request.getRemarks().isBlank()) {

            throw new IllegalArgumentException(
                    "Rejection remarks are required");
        }

        loan.setStatus(LoanStatus.REJECTED);
        loan.setRejectionRemarks(request.getRemarks().trim());
        loan.setRejectionDate(LocalDateTime.now());

        Loan savedLoan = loanRepository.save(loan);

        return mapToResponse(savedLoan);
    }

    // Find loan by ID
    private Loan findLoanById(Long loanId) {

        return loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException(
                        "Loan not found with ID: " + loanId
                ));
    }

    // Convert Loan entity into response DTO
    private LoanApplicationResponse mapToResponse(Loan loan) {

        LoanApplicationResponse response =
                new LoanApplicationResponse();

        response.setLoanId(loan.getId());

        response.setBorrowerId(
                loan.getBorrowerProfile().getId());

        response.setBorrowerName(
                loan.getBorrowerProfile().getUser().getName());

        response.setBorrowerEmail(
                loan.getBorrowerProfile().getUser().getEmail());

        response.setLoanAmount(loan.getLoanAmount());
        response.setInterestRate(loan.getInterestRate());
        response.setTenureMonths(loan.getTenureMonths());

        response.setEmi(
                emiCalculator.calculateEmi(
                        loan.getLoanAmount(),
                        loan.getInterestRate(),
                        loan.getTenureMonths()
                )
        );

        response.setStatus(loan.getStatus().name());

        response.setRejectionRemarks(
                loan.getRejectionRemarks());

        return response;
    }
}