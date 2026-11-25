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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RepaymentServiceTest {

    private RepaymentRepository repaymentRepository;
    private LoanRepository loanRepository;
    private RepaymentService repaymentService;

    private Loan loan;
    private Repayment repayment;

    @BeforeEach
    void setUp() {

        repaymentRepository = mock(RepaymentRepository.class);
        loanRepository = mock(LoanRepository.class);

        repaymentService = new RepaymentService(
                repaymentRepository,
                loanRepository,
                new EmiCalculator()
        );

        loan = new Loan();
        loan.setId(1L);
        loan.setLoanAmount(new BigDecimal("100000"));
        loan.setInterestRate(new BigDecimal("12"));
        loan.setTenureMonths(12);
        loan.setStatus(LoanStatus.APPROVED);

        repayment = new Repayment();
        repayment.setId(101L);
        repayment.setLoan(loan);
        repayment.setAmount(new BigDecimal("8884.88"));
        repayment.setDueDate(LocalDate.now().plusMonths(1));
        repayment.setStatus(RepaymentStatus.PENDING);
    }

    @Test
    void shouldGenerateRepaymentSchedule() {

        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan));

        when(repaymentRepository.findByLoanId(1L))
                .thenReturn(new ArrayList<>());

        when(repaymentRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<RepaymentResponse> result =
                repaymentService.generateRepaymentSchedule(1L);

        assertEquals(12, result.size());

        assertEquals(
                new BigDecimal("8884.88"),
                result.get(0).amount()
        );

        assertEquals("PENDING", result.get(0).status());

        assertEquals(
                LocalDate.now().plusMonths(1),
                result.get(0).dueDate()
        );

        verify(repaymentRepository).saveAll(anyList());
    }

    @Test
    void shouldNotGenerateScheduleForPendingLoan() {

        loan.setStatus(LoanStatus.PENDING);

        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan));

        assertThrows(
                IllegalStateException.class,
                () -> repaymentService.generateRepaymentSchedule(1L)
        );

        verify(repaymentRepository, never()).saveAll(anyList());
    }

    @Test
    void shouldNotGenerateDuplicateSchedule() {

        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan));

        when(repaymentRepository.findByLoanId(1L))
                .thenReturn(List.of(repayment));

        assertThrows(
                IllegalStateException.class,
                () -> repaymentService.generateRepaymentSchedule(1L)
        );

        verify(repaymentRepository, never()).saveAll(anyList());
    }

    @Test
    void shouldFetchRepaymentsByLoanId() {

        when(loanRepository.findById(1L))
                .thenReturn(Optional.of(loan));

        when(repaymentRepository.findByLoanId(1L))
                .thenReturn(List.of(repayment));

        List<RepaymentResponse> result =
                repaymentService.getRepaymentsByLoan(1L);

        assertEquals(1, result.size());
        assertEquals(101L, result.get(0).repaymentId());
        assertEquals(1L, result.get(0).loanId());
    }

    @Test
    void shouldThrowExceptionWhenLoanDoesNotExist() {

        when(loanRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                LoanNotFoundException.class,
                () -> repaymentService.getRepaymentsByLoan(99L)
        );
    }

    @Test
    void shouldFetchRepaymentById() {

        when(repaymentRepository.findById(101L))
                .thenReturn(Optional.of(repayment));

        RepaymentResponse result =
                repaymentService.getRepaymentById(101L);

        assertEquals(101L, result.repaymentId());
        assertEquals(1L, result.loanId());
        assertEquals("PENDING", result.status());
    }

    @Test
    void shouldThrowExceptionWhenRepaymentDoesNotExist() {

        when(repaymentRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                RepaymentNotFoundException.class,
                () -> repaymentService.getRepaymentById(999L)
        );
    }

    @Test
    void shouldFilterRepaymentsByStatus() {

        when(repaymentRepository.findByStatus(RepaymentStatus.PENDING))
                .thenReturn(List.of(repayment));

        List<RepaymentResponse> result =
                repaymentService.getRepaymentsByStatus(
                        RepaymentStatus.PENDING
                );

        assertEquals(1, result.size());
        assertEquals("PENDING", result.get(0).status());
    }

    @Test
    void shouldMarkRepaymentAsPaid() {

        when(repaymentRepository.findById(101L))
                .thenReturn(Optional.of(repayment));

        when(repaymentRepository.save(repayment))
                .thenReturn(repayment);

        RepaymentResponse result =
                repaymentService.markRepaymentAsPaid(101L);

        assertEquals(RepaymentStatus.PAID, repayment.getStatus());
        assertEquals("PAID", result.status());

        assertEquals(LocalDate.now(), repayment.getPaymentDate());

        verify(repaymentRepository).save(repayment);
    }

    @Test
    void shouldNotMarkAlreadyPaidRepaymentAgain() {

        repayment.setStatus(RepaymentStatus.PAID);

        when(repaymentRepository.findById(101L))
                .thenReturn(Optional.of(repayment));

        assertThrows(
                IllegalStateException.class,
                () -> repaymentService.markRepaymentAsPaid(101L)
        );

        verify(repaymentRepository, never()).save(any(Repayment.class));
    }
}
