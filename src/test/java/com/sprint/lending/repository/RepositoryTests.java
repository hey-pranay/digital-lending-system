
package com.sprint.lending.repository;

import com.sprint.lending.entity.User;
import com.sprint.lending.entity.Role;
import com.sprint.lending.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import com.sprint.lending.entity.BorrowerProfile;
import com.sprint.lending.entity.EmploymentType;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import com.sprint.lending.entity.Loan;
import com.sprint.lending.entity.LoanStatus;
import com.sprint.lending.entity.BorrowerProfile;
import java.math.BigDecimal;

@SpringBootTest
@Transactional
class RepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BorrowerProfileRepository borrowerProfileRepository;

    @Autowired
    private LoanRepository loanRepository;
    @Test
    void testSaveAndFindLoan() {

        // Create User
        User user = new User();
        user.setName("Loan Test User");
        user.setEmail("loan.test@example.com");
        user.setPassword("temporary-test-password");
        user.setRole(Role.BORROWER);

        User savedUser = userRepository.save(user);

        // Create BorrowerProfile
        BorrowerProfile profile = new BorrowerProfile();
        profile.setPhone("9876543210");
        profile.setAddress("Pune");
        profile.setMonthlyIncome(new BigDecimal("50000.00"));
        profile.setEmploymentType(EmploymentType.SALARIED);
        profile.setUser(savedUser);

        BorrowerProfile savedProfile =
                borrowerProfileRepository.save(profile);

        // Create Loan
        Loan loan = new Loan();
        loan.setLoanAmount(new BigDecimal("100000.00"));
        loan.setInterestRate(new BigDecimal("10.00"));
        loan.setTenureMonths(12);
        loan.setEmiAmount(new BigDecimal("8791.59"));
        loan.setStatus(LoanStatus.APPLIED);
        loan.setBorrowerProfile(savedProfile);

        // Save Loan
        Loan savedLoan = loanRepository.save(loan);

        // Verify Loan
        assertNotNull(savedLoan.getId());

        Loan retrievedLoan =
                loanRepository.findById(savedLoan.getId()).orElse(null);

        assertNotNull(retrievedLoan);
        assertEquals(new BigDecimal("100000.00"), retrievedLoan.getLoanAmount());
        assertEquals(new BigDecimal("10.00"), retrievedLoan.getInterestRate());
        assertEquals(12, retrievedLoan.getTenureMonths());
        assertEquals(LoanStatus.APPLIED, retrievedLoan.getStatus());

        // Verify BorrowerProfile relationship
        assertNotNull(retrievedLoan.getBorrowerProfile());
        assertEquals(savedProfile.getId(),
                retrievedLoan.getBorrowerProfile().getId());
    }
}
