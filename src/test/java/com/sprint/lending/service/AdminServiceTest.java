package com.sprint.lending.service;

import com.sprint.lending.dto.request.RejectLoanRequest;
import com.sprint.lending.dto.response.LoanApplicationResponse;
import com.sprint.lending.entity.BorrowerProfile;
import com.sprint.lending.entity.Loan;
import com.sprint.lending.entity.LoanStatus;
import com.sprint.lending.entity.User;
import com.sprint.lending.exception.LoanNotFoundException;
import com.sprint.lending.repository.LoanRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    private LoanRepository loanRepository;
    private AdminService adminService;

    private Loan loan;

    @BeforeEach
    void setUp() {

        loanRepository = mock(LoanRepository.class);

        adminService = new AdminService(
                loanRepository,
                new EmiCalculator()
        );

        User user = new User();
        user.setName("Test Borrower");
        user.setEmail("borrower@test.com");

        BorrowerProfile profile = new BorrowerProfile();
        profile.setId(10L);
        profile.setUser(user);

        loan = new Loan();
        loan.setId(1L);
        loan.setLoanAmount(new BigDecimal("100000"));
        loan.setInterestRate(new BigDecimal("12"));
        loan.setTenureMonths(12);
        loan.setStatus(LoanStatus.PENDING);
        loan.setBorrowerProfile(profile);
    }

    @Test
    void shouldFetchAllApplications() {

        when(loanRepository.findAll())
                .thenReturn(List.of(loan));

        List<LoanApplicationResponse> result =
                adminService.getAllApplications();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getLoanId());
        assertEquals("Test Borrower",
                result.get(0).getBorrowerName());
    }

    @Test
    void shouldFilterApplicationsByStatus() {

        when(loanRepository.findByStatus(LoanStatus.PENDING))
                .thenReturn(List.of(loan));

        List<LoanApplicationResponse> result =
                adminService.getApplicationsByStatus(
                        LoanStatus.PENDING);

        assertEquals(1, result.size());
        assertEquals("PENDING", result.get(0).getStatus());
    }

    @Test
    void shouldFetchApplicationById() {

        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan));

        LoanApplicationResponse result =
                adminService.getApplicationById(1L);

        assertEquals(1L, result.getLoanId());
        assertEquals("borrower@test.com",
                result.getBorrowerEmail());
    }

    @Test
    void shouldThrowExceptionWhenLoanDoesNotExist() {

        when(loanRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                LoanNotFoundException.class,
                () -> adminService.getApplicationById(99L)
        );
    }

    @Test
    void shouldApprovePendingLoan() {

        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan));

        when(loanRepository.save(loan))
                .thenReturn(loan);

        LoanApplicationResponse result =
                adminService.approveLoan(1L);

        assertEquals(LoanStatus.APPROVED, loan.getStatus());
        assertEquals("APPROVED", result.getStatus());

        verify(loanRepository).save(loan);
    }

    @Test
    void shouldNotApproveAlreadyProcessedLoan() {

        loan.setStatus(LoanStatus.REJECTED);

        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan));

        assertThrows(
                IllegalStateException.class,
                () -> adminService.approveLoan(1L)
        );

        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    void shouldRejectPendingLoanWithRemarks() {

        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan));

        when(loanRepository.save(loan))
                .thenReturn(loan);

        RejectLoanRequest request = new RejectLoanRequest();
        request.setRemarks("Insufficient income");

        LoanApplicationResponse result =
                adminService.rejectLoan(1L, request);

        assertEquals(LoanStatus.REJECTED, loan.getStatus());
        assertEquals("REJECTED", result.getStatus());
        assertEquals("Insufficient income",
                result.getRejectionRemarks());

        verify(loanRepository).save(loan);
    }

    @Test
    void shouldNotRejectLoanWithoutRemarks() {

        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan));

        RejectLoanRequest request = new RejectLoanRequest();
        request.setRemarks("   ");

        assertThrows(
                IllegalArgumentException.class,
                () -> adminService.rejectLoan(1L, request)
        );

        verify(loanRepository, never()).save(any(Loan.class));
    }
}
