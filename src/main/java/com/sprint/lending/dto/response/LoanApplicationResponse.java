package com.sprint.lending.dto.response;

import java.math.BigDecimal;

public class LoanApplicationResponse {

    private Long loanId;

    private Long borrowerId;

    private String borrowerName;

    private String borrowerEmail;

    private BigDecimal loanAmount;

    private BigDecimal interestRate;

    private Integer tenureMonths;

    private BigDecimal emi;

    private String status;

    private String rejectionRemarks;

    public LoanApplicationResponse() {
    }

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public Long getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(Long borrowerId) {
        this.borrowerId = borrowerId;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public String getBorrowerEmail() {
        return borrowerEmail;
    }

    public void setBorrowerEmail(String borrowerEmail) {
        this.borrowerEmail = borrowerEmail;
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

    public BigDecimal getEmi() {
        return emi;
    }

    public void setEmi(BigDecimal emi) {
        this.emi = emi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectionRemarks() {
        return rejectionRemarks;
    }

    public void setRejectionRemarks(String rejectionRemarks) {
        this.rejectionRemarks = rejectionRemarks;
    }
}
