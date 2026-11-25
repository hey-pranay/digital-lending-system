
package com.sprint.lending.service;

import com.sprint.lending.dto.request.BorrowerProfileRequest;
import com.sprint.lending.entity.BorrowerProfile;
import com.sprint.lending.entity.Role;
import com.sprint.lending.entity.User;
import com.sprint.lending.repository.BorrowerProfileRepository;
import com.sprint.lending.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BorrowerProfileService {

    private final BorrowerProfileRepository borrowerProfileRepository;
    private final UserRepository userRepository;

    public BorrowerProfileService(
            BorrowerProfileRepository borrowerProfileRepository,
            UserRepository userRepository) {

        this.borrowerProfileRepository = borrowerProfileRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createProfile(
            String email,
            BorrowerProfileRequest request) {

        // Find the currently logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        )
                );

        // Only borrowers can create borrower profiles
        if (user.getRole() != Role.BORROWER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only borrowers can create borrower profiles"
            );
        }

        // Prevent duplicate borrower profiles
        boolean profileExists =
                borrowerProfileRepository
                        .findByUser_Email(email)
                        .isPresent();

        if (profileExists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Borrower profile already exists"
            );
        }

        // Create borrower profile
        BorrowerProfile profile = new BorrowerProfile();

        profile.setPhone(request.phone());
        profile.setAddress(request.address());
        profile.setMonthlyIncome(request.monthlyIncome());
        profile.setEmploymentType(request.employmentType());

        // Connect profile with the logged-in user
        profile.setUser(user);

        // Save profile
        borrowerProfileRepository.save(profile);
    }
}
