package com.sprint.lending.repository;

import com.sprint.lending.entity.BorrowerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowerProfileRepository extends JpaRepository<BorrowerProfile, Long> {

}