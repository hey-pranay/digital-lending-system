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
    public List<RepaymentResponse> getRepaymentsByStatus(
            RepaymentStatus status) {

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
}
