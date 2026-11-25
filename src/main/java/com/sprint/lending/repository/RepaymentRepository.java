package com.sprint.lending.repository;

import com.sprint.lending.entity.Repayment;
import com.sprint.lending.entity.RepaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RepaymentRepository extends JpaRepository<Repayment, Long> {
    List<Repayment> findByLoanId(Long loanId);

    List<Repayment> findByStatus(RepaymentStatus status);


    // Fetch all repayments belonging to a borrower using their email.
    List<Repayment> findByLoan_BorrowerProfile_User_EmailOrderByDueDateAsc(
            String email
    );

    // Find pending repayments whose due date has passed.
    List<Repayment> findByStatusAndDueDateBefore(
            RepaymentStatus status,
            LocalDate date
    );


}