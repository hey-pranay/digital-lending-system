
package com.sprint.lending.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal loanAmount;

    private BigDecimal interestRate;

    private Integer tenureMonths;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanStatus status;

    @ManyToOne
    @JoinColumn(name = "borrower_profile_id", nullable = false)
    private BorrowerProfile borrowerProfile;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Integer getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public BorrowerProfile getBorrowerProfile() {
        return borrowerProfile;
    }

    public void setBorrowerProfile(BorrowerProfile borrowerProfile) {
        this.borrowerProfile = borrowerProfile;
    }
}
