package com.sprint.lending.entity;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EntityTests {


    @Test
    void testRepaymentEntity() {

        Repayment repayment = new Repayment();

        repayment.setAmount(new BigDecimal("8791.59"));
        repayment.setInstalmentNumber(1);
        repayment.setDueDate(LocalDate.of(2026, 12, 25));
        repayment.setStatus(RepaymentStatus.PENDING);

        assertEquals(new BigDecimal("8791.59"), repayment.getAmount());
        assertEquals(1, repayment.getInstalmentNumber());
        assertEquals(LocalDate.of(2026, 12, 25), repayment.getDueDate());
        assertEquals(RepaymentStatus.PENDING, repayment.getStatus());
    }

    @Test
    void testUserEntity() {

        User user = new User();

        user.setName("Test Borrower");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setRole(Role.BORROWER);

        assertEquals("Test Borrower", user.getName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals(Role.BORROWER, user.getRole());
    }

    @Test
    void testBorrowerProfileEntity() {

        BorrowerProfile profile = new BorrowerProfile();

        profile.setPhone("9876543210");
        profile.setAddress("Pune");
        profile.setMonthlyIncome(new BigDecimal("50000.00"));
        profile.setEmploymentType(EmploymentType.SALARIED);

        assertEquals("9876543210", profile.getPhone());
        assertEquals("Pune", profile.getAddress());
        assertEquals(new BigDecimal("50000.00"), profile.getMonthlyIncome());
        assertEquals(EmploymentType.SALARIED, profile.getEmploymentType());
    }

    @Test
    void testLoanEntity() {

        Loan loan = new Loan();

        loan.setLoanAmount(new BigDecimal("100000.00"));
        loan.setInterestRate(new BigDecimal("10.00"));
        loan.setTenureMonths(12);
        loan.setEmiAmount(new BigDecimal("8791.59"));
        loan.setStatus(LoanStatus.APPLIED);

        assertEquals(new BigDecimal("100000.00"), loan.getLoanAmount());
        assertEquals(new BigDecimal("10.00"), loan.getInterestRate());
        assertEquals(12, loan.getTenureMonths());
        assertEquals(new BigDecimal("8791.59"), loan.getEmiAmount());
        assertEquals(LoanStatus.APPLIED, loan.getStatus());
    }
        }

