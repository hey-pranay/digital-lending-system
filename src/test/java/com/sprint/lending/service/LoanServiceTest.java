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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class LoanServiceTest {

    private LoanRepository loanRepository;

    private BorrowerProfileRepository borrowerProfileRepository;

    private LoanService loanService;

    private BorrowerProfile borrower;

    private static final String EMAIL =
            "borrower@example.com";

    @BeforeEach
    void setUp() {

        loanRepository = mock(LoanRepository.class);

        borrowerProfileRepository =
                mock(BorrowerProfileRepository.class);

        loanService = new LoanService(
                loanRepository,
                borrowerProfileRepository,
                new EmiCalculator()
        );

        borrower = new BorrowerProfile();

        // Updated: ID setter is no longer available
        ReflectionTestUtils.setField(
                borrower, "id", 1L
        );

        borrower.setMonthlyIncome(
                new BigDecimal("30000")
        );
    }

    private LoanRequest validRequest() {

        return new LoanRequest(
                new BigDecimal("100000"),
                12
        );
    }

    // Test 1: Successful loan application
    @Test
    void shouldApplyForLoanSuccessfully() {

        when(borrowerProfileRepository
                .findByEmailForUpdate(EMAIL))
                .thenReturn(Optional.of(borrower));

        when(loanRepository
                .existsByBorrowerProfile_IdAndStatusIn(
                        eq(1L),
                        anyCollection()
                ))
                .thenReturn(false);

        when(loanRepository.save(any(Loan.class)))
                .thenAnswer(invocation -> {

                    Loan loan = invocation.getArgument(0);

                    // Updated: Assign generated ID for testing
                    ReflectionTestUtils.setField(
                            loan, "id", 1L
                    );

                    return loan;
                });

        LoanResponse response =
                loanService.applyForLoan(
                        EMAIL,
                        validRequest()
                );

        assertNotNull(response);

        assertEquals(1L, response.id());

        assertEquals("APPLIED", response.status());

        assertEquals(
                new BigDecimal("8884.88"),
                response.emi()
        );

        assertEquals(12, response.tenureMonths());

        verify(loanRepository, times(1))
                .save(any(Loan.class));
    }

    // Test 2: Duplicate active loan
    @Test
    void shouldRejectDuplicateLoan() {

        when(borrowerProfileRepository
                .findByEmailForUpdate(EMAIL))
                .thenReturn(Optional.of(borrower));

        when(loanRepository
                .existsByBorrowerProfile_IdAndStatusIn(
                        1L,
                        List.of(
                                LoanStatus.APPLIED,
                                LoanStatus.APPROVED
                        )
                ))
                .thenReturn(true);

        assertThrows(
                DuplicateLoanException.class,
                () -> loanService.applyForLoan(
                        EMAIL,
                        validRequest()
                )
        );

        verify(loanRepository, never())
                .save(any(Loan.class));
    }

    // Test 3: Insufficient income
    @Test
    void shouldRejectInsufficientIncome() {

        borrower.setMonthlyIncome(
                new BigDecimal("10000")
        );

        when(borrowerProfileRepository
                .findByEmailForUpdate(EMAIL))
                .thenReturn(Optional.of(borrower));

        assertThrows(
                LoanEligibilityException.class,
                () -> loanService.applyForLoan(
                        EMAIL,
                        validRequest()
                )
        );

        verify(loanRepository, never())
                .save(any(Loan.class));
    }

    // Test 4: Missing borrower profile
    @Test
    void shouldRejectMissingBorrowerProfile() {

        when(borrowerProfileRepository
                .findByEmailForUpdate(EMAIL))
                .thenReturn(Optional.empty());

        assertThrows(
                BorrowerProfileNotFoundException.class,
                () -> loanService.applyForLoan(
                        EMAIL,
                        validRequest()
                )
        );

        verify(loanRepository, never())
                .save(any(Loan.class));
    }

    // Test 5: Zero monthly income
    @Test
    void shouldRejectZeroIncome() {

        borrower.setMonthlyIncome(BigDecimal.ZERO);

        when(borrowerProfileRepository
                .findByEmailForUpdate(EMAIL))
                .thenReturn(Optional.of(borrower));

        assertThrows(
                LoanEligibilityException.class,
                () -> loanService.applyForLoan(
                        EMAIL,
                        validRequest()
                )
        );

        verify(loanRepository, never())
                .save(any(Loan.class));
    }

    // Test 6: Missing monthly income
    @Test
    void shouldRejectNullIncome() {

        borrower.setMonthlyIncome(null);

        when(borrowerProfileRepository
                .findByEmailForUpdate(EMAIL))
                .thenReturn(Optional.of(borrower));

        assertThrows(
                LoanEligibilityException.class,
                () -> loanService.applyForLoan(
                        EMAIL,
                        validRequest()
                )
        );

        verify(loanRepository, never())
                .save(any(Loan.class));
    }

    // Test 7: Retrieve borrower's loans
    @Test
    void shouldReturnBorrowerLoans() {

        Loan loan = new Loan();

        // Updated: ID setter removed
        ReflectionTestUtils.setField(
                loan, "id", 1L
        );

        loan.setLoanAmount(
                new BigDecimal("100000")
        );

        loan.setInterestRate(
                new BigDecimal("12.00")
        );

        loan.setTenureMonths(12);

        // Updated: New entity method name
        loan.setEmiAmount(
                new BigDecimal("8884.88")
        );

        loan.setStatus(LoanStatus.APPLIED);

        loan.setBorrowerProfile(borrower);

        when(borrowerProfileRepository
                .findByUser_Email(EMAIL))
                .thenReturn(Optional.of(borrower));

        when(loanRepository
                .findByBorrowerProfile_Id(1L))
                .thenReturn(List.of(loan));

        List<LoanResponse> response =
                loanService.getMyLoans(EMAIL);

        assertEquals(1, response.size());

        assertEquals(
                1L,
                response.get(0).id()
        );

        assertEquals(
                "APPLIED",
                response.get(0).status()
        );

        verify(loanRepository, times(1))
                .findByBorrowerProfile_Id(1L);
    }

    // Test 8: Borrower has no loans
    @Test
    void shouldReturnEmptyLoanList() {

        when(borrowerProfileRepository
                .findByUser_Email(EMAIL))
                .thenReturn(Optional.of(borrower));

        when(loanRepository
                .findByBorrowerProfile_Id(1L))
                .thenReturn(List.of());

        List<LoanResponse> response =
                loanService.getMyLoans(EMAIL);

        assertTrue(response.isEmpty());
    }
}