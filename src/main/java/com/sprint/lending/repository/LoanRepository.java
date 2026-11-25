package com.sprint.lending.repository;

import com.sprint.lending.entity.Loan;
import com.sprint.lending.entity.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    //These methods are required to retrieve a borrower's loans and prevent duplicate active loan applications

    List<Loan> findByBorrowerProfile_Id(Long borrowerId);

    boolean existsByBorrowerProfile_IdAndStatusIn(
            Long borrowerId,
            Collection<LoanStatus> statuses
    );
}