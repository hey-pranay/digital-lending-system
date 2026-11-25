package com.sprint.lending.service;

import com.sprint.lending.dto.request.LoanRequest;
import com.sprint.lending.dto.response.LoanResponse;

import com.sprint.lending.entity.BorrowerProfile;
import com.sprint.lending.entity.Loan;
import com.sprint.lending.entity.LoanStatus;

import com.sprint.lending.exception.BorrowerProfileNotFoundException;
import com.sprint.lending.exception.DuplicateLoanException;
import com.sprint.lending.exception.LoanEligibilityException;

import com.sprint.lending.repository.BorrowerProfileRepository;
import com.sprint.lending.repository.LoanRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LoanService {

    private static final BigDecimal INTEREST_RATE =
            new BigDecimal("12.00");

    private static final BigDecimal EMI_INCOME_RATIO =
            new BigDecimal("0.40");

    private final LoanRepository loanRepository;

    private final BorrowerProfileRepository
            borrowerProfileRepository;

    private final EmiCalculator emiCalculator;

    public LoanService(
            LoanRepository loanRepository,
            BorrowerProfileRepository borrowerProfileRepository,
            EmiCalculator emiCalculator) {

        this.loanRepository = loanRepository;
        this.borrowerProfileRepository =
                borrowerProfileRepository;
        this.emiCalculator = emiCalculator;
    }

    // Submit a loan application
    @Transactional
    public LoanResponse applyForLoan(
            String email,
            LoanRequest request) {

        // Retrieve and lock borrower profile
        BorrowerProfile borrower =
                borrowerProfileRepository
                        .findByEmailForUpdate(email)
                        .orElseThrow(() ->
                                new BorrowerProfileNotFoundException(
                                        "Borrower profile not found"
                                )
                        );

        // Check duplicate active loan
        boolean activeLoanExists =
                loanRepository
                        .existsByBorrowerProfile_IdAndStatusIn(
                                borrower.getId(),
                                List.of(
                                        LoanStatus.APPLIED,
                                        LoanStatus.APPROVED
                                )
                        );

        if (activeLoanExists) {
            throw new DuplicateLoanException(
                    "You already have an active loan"
            );
        }

        // Calculate EMI
        BigDecimal emi =
                emiCalculator.calculateEmi(
                        request.amount(),
                        INTEREST_RATE,
                        request.tenureMonths()
                );

        // Check monthly income
        BigDecimal monthlyIncome =
                borrower.getMonthlyIncome();

        if (monthlyIncome == null
                || monthlyIncome.signum() <= 0) {

            throw new LoanEligibilityException(
                    "Valid monthly income is required"
            );
        }

        // Maximum affordable EMI
        BigDecimal maximumAllowedEmi =
                monthlyIncome.multiply(EMI_INCOME_RATIO);

        if (emi.compareTo(maximumAllowedEmi) > 0) {

            throw new LoanEligibilityException(
                    "Monthly EMI exceeds 40% of your income"
            );
        }

        // Create loan using updated domain entity
        Loan loan = new Loan();

        loan.setLoanAmount(request.amount());
        loan.setInterestRate(INTEREST_RATE);
        loan.setTenureMonths(request.tenureMonths());

        // UPDATED: Matches teammate's new entity
        loan.setEmiAmount(emi);

        loan.setStatus(LoanStatus.APPLIED);
        loan.setBorrowerProfile(borrower);

        // Save loan
        Loan savedLoan = loanRepository.save(loan);

        return mapToResponse(savedLoan);
    }

    // Retrieve borrower's loans
    @Transactional(readOnly = true)
    public List<LoanResponse> getMyLoans(String email) {

        BorrowerProfile borrower =
                borrowerProfileRepository
                        .findByUser_Email(email)
                        .orElseThrow(() ->
                                new BorrowerProfileNotFoundException(
                                        "Borrower profile not found"
                                )
                        );

        return loanRepository
                .findByBorrowerProfile_Id(borrower.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Convert entity to response DTO
    private LoanResponse mapToResponse(Loan loan) {

        return new LoanResponse(
                loan.getId(),
                loan.getLoanAmount(),
                loan.getInterestRate(),
                loan.getTenureMonths(),

                // UPDATED
                loan.getEmiAmount(),

                loan.getStatus().name(),

                // UPDATED
                loan.getRejectionReason()
        );
    }
}