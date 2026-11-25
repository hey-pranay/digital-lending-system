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
        }

