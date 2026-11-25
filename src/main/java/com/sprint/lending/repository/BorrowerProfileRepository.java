package com.sprint.lending.repository;

import com.sprint.lending.entity.BorrowerProfile;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BorrowerProfileRepository
        extends JpaRepository<BorrowerProfile, Long> {

//    These methods allow your service to identify the logged-in borrower,
//    retrieve their monthly income and protect against simultaneous duplicate applications

    Optional<BorrowerProfile> findByUser_Email(
            String email
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT b FROM BorrowerProfile b
            WHERE b.user.email = :email
            """)
    Optional<BorrowerProfile> findByEmailForUpdate(
            @Param("email") String email
    );
}