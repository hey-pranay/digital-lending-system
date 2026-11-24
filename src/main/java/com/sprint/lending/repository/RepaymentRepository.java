package com.sprint.lending.repository;

import com.sprint.lending.entity.Repayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepaymentRepository extends JpaRepository<Repayment, Long> {

}