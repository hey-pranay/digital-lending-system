package com.sprint.lending.service;

import com.sprint.lending.dto.response.LoanActivityResponse;
import com.sprint.lending.dto.response.LoanDashboardResponse;
import com.sprint.lending.dto.response.RepaymentHistoryResponse;
import com.sprint.lending.dto.response.RepaymentResponse;

import com.sprint.lending.entity.BorrowerProfile;
import com.sprint.lending.entity.Loan;
import com.sprint.lending.entity.LoanStatus;
import com.sprint.lending.entity.Repayment;
import com.sprint.lending.entity.RepaymentStatus;

import com.sprint.lending.repository.BorrowerProfileRepository;
import com.sprint.lending.repository.LoanRepository;
import com.sprint.lending.repository.RepaymentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class BorrowerOverviewService {

    private final LoanRepository loanRepository;
    private final BorrowerProfileRepository borrowerProfileRepository;
    private final RepaymentRepository repaymentRepository;

    public BorrowerOverviewService(
            LoanRepository loanRepository,
            BorrowerProfileRepository borrowerProfileRepository,
            RepaymentRepository repaymentRepository) {

        this.loanRepository = loanRepository;
        this.borrowerProfileRepository = borrowerProfileRepository;
        this.repaymentRepository = repaymentRepository;
    }

    // API 3: Borrower Dashboard
    public LoanDashboardResponse getDashboard(String email) {

        List<Loan> loans = findBorrowerLoans(email);

        BigDecimal totalLoanAmount = BigDecimal.ZERO;
        BigDecimal outstandingBalance = BigDecimal.ZERO;
        BigDecimal monthlyEmi = BigDecimal.ZERO;

        for (Loan loan : loans) {

            if (loan.getStatus() == LoanStatus.APPROVED
                    || loan.getStatus() == LoanStatus.CLOSED) {

                totalLoanAmount = totalLoanAmount.add(
                        loan.getLoanAmount()
                );
            }

            if (loan.getStatus() == LoanStatus.APPROVED) {

                outstandingBalance = outstandingBalance.add(
                        calculateOutstandingBalance(loan)
                );

                monthlyEmi = monthlyEmi.add(
                        loan.getEmiAmount()
                );
            }
        }

        String loanStatus = loans.stream()
                .filter(loan ->
                        loan.getStatus() == LoanStatus.APPROVED
                                || loan.getStatus() == LoanStatus.APPLIED)
                .max(Comparator.comparing(Loan::getId))
                .or(() -> loans.stream()
                        .max(Comparator.comparing(Loan::getId)))
                .map(loan -> loan.getStatus().name())
                .orElse("NOT_APPLIED");

        return new LoanDashboardResponse(
                formatAmount(totalLoanAmount),
                formatAmount(outstandingBalance),
                formatAmount(monthlyEmi),
                loanStatus
        );
    }

    // API 4: Borrower Repayment History
    public RepaymentHistoryResponse getRepaymentHistory(
            String email) {

        LocalDate today = LocalDate.now();

        List<RepaymentResponse> repayments =
                findBorrowerLoans(email)
                        .stream()
                        .filter(loan ->
                                loan.getStatus() == LoanStatus.APPROVED
                                        || loan.getStatus() == LoanStatus.CLOSED)
                        .flatMap(loan ->
                                repaymentRepository
                                        .findByLoanId(loan.getId())
                                        .stream()
                        )
                        .sorted(
                                Comparator.comparing(Repayment::getDueDate)
                                        .thenComparing(Repayment::getId)
                        )
                        .map(repayment ->
                                mapRepayment(repayment, today))
                        .toList();

        long paid = repayments.stream()
                .filter(r -> "PAID".equals(r.status()))
                .count();

        long pending = repayments.stream()
                .filter(r -> "PENDING".equals(r.status()))
                .count();

        long overdue = repayments.stream()
                .filter(r -> "OVERDUE".equals(r.status()))
                .count();

        return new RepaymentHistoryResponse(
                repayments.size(),
                paid,
                pending,
                overdue,
                repayments
        );
    }

    // Retrieve authenticated borrower's loans only
    private List<Loan> findBorrowerLoans(String email) {

        return borrowerProfileRepository
                .findByUser_Email(email)
                .map(BorrowerProfile::getId)
                .map(loanRepository::findByBorrowerProfile_Id)
                .orElseGet(List::of);
    }

    // Calculate estimated outstanding principal
    private BigDecimal calculateOutstandingBalance(Loan loan) {

        BigDecimal balance = loan.getLoanAmount();

        BigDecimal monthlyRate = loan.getInterestRate()
                .divide(
                        new BigDecimal("1200"),
                        16,
                        RoundingMode.HALF_UP
                );

        List<Repayment> paidRepayments =
                repaymentRepository.findByLoanId(loan.getId())
                        .stream()
                        .filter(repayment ->
                                repayment.getStatus()
                                        == RepaymentStatus.PAID)
                        .sorted(
                                Comparator.comparing(
                                        Repayment::getInstalmentNumber)
                        )
                        .toList();

        for (Repayment repayment : paidRepayments) {

            BigDecimal interest = balance
                    .multiply(monthlyRate)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal principalPaid = repayment.getAmount()
                    .subtract(interest)
                    .max(BigDecimal.ZERO);

            balance = balance.subtract(principalPaid)
                    .max(BigDecimal.ZERO);
        }

        return formatAmount(balance);
    }

    // Convert repayment entity into response DTO
    private RepaymentResponse mapRepayment(
            Repayment repayment,
            LocalDate today) {

        RepaymentStatus status = repayment.getStatus();

        if (status == RepaymentStatus.PENDING
                && repayment.getDueDate().isBefore(today)) {

            status = RepaymentStatus.OVERDUE;
        }

        return new RepaymentResponse(
                repayment.getId(),
                repayment.getLoan().getId(),
                repayment.getAmount(),
                repayment.getDueDate(),
                repayment.getPaymentDate(),
                status.name()
        );
    }

    // Format monetary amounts
    private BigDecimal formatAmount(BigDecimal amount) {

        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    // API: Get borrower's recent activities
    public List<LoanActivityResponse> getRecentActivities(String email) {

        List<LoanActivityResponse> activities = new ArrayList<>();

        LocalDate today = LocalDate.now();

        List<Loan> loans = findBorrowerLoans(email);

        for (Loan loan : loans) {

            // Loan application submitted
            activities.add(new LoanActivityResponse(
                    "LOAN",
                    "Loan Application Submitted",
                    loan.getId(),
                    loan.getLoanAmount(),
                    loan.getApplicationDate(),
                    "SUBMITTED"
            ));

            // Loan approved
            if (loan.getApprovalDate() != null) {

                activities.add(new LoanActivityResponse(
                        "LOAN",
                        "Loan Approved",
                        loan.getId(),
                        loan.getLoanAmount(),
                        loan.getApprovalDate(),
                        "APPROVED"
                ));
            }

            // Loan rejected
            if (loan.getRejectionDate() != null) {

                activities.add(new LoanActivityResponse(
                        "LOAN",
                        "Loan Rejected",
                        loan.getId(),
                        loan.getLoanAmount(),
                        loan.getRejectionDate(),
                        "REJECTED"
                ));
            }

            // Get repayments belonging to this loan
            List<Repayment> repayments =
                    repaymentRepository.findByLoanId(loan.getId());

            for (Repayment repayment : repayments) {

                // Repayment completed
                if (repayment.getStatus() == RepaymentStatus.PAID
                        && repayment.getPaymentDate() != null) {

                    activities.add(new LoanActivityResponse(
                            "REPAYMENT",
                            "Repayment Completed",
                            loan.getId(),
                            repayment.getAmount(),
                            repayment.getPaymentDate().atStartOfDay(),
                            "PAID"
                    ));
                }

                // Repayment overdue
                else if (repayment.getStatus() == RepaymentStatus.OVERDUE
                        || (repayment.getStatus() == RepaymentStatus.PENDING
                        && repayment.getDueDate().isBefore(today))) {

                    activities.add(new LoanActivityResponse(
                            "REPAYMENT",
                            "Repayment Overdue",
                            loan.getId(),
                            repayment.getAmount(),
                            repayment.getDueDate().atStartOfDay(),
                            "OVERDUE"
                    ));
                }
            }
        }

        // Sort activities by newest date first
        activities.sort(
                Comparator.comparing(
                        LoanActivityResponse::activityDate
                ).reversed()
        );

        // Return only the latest five activities
        return activities.stream()
                .limit(5)
                .toList();
    }


}