package com.sprint.lending.controller;

import com.sprint.lending.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/borrower")
public class BorrowerPageController {

    private final UserRepository userRepository;

    public BorrowerPageController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String borrowerDashboard() {
        return "borrower/dashboard";
    }

    @GetMapping("/apply-loan")
    public String applyLoanPage() {
        return "borrower/apply-loan";
    }

    @GetMapping("/loans")
    public String myLoansPage() {
        return "borrower/loans";
    }

    @GetMapping("/repayments")
    public String repaymentsHistoryPage() {
        return "borrower/repayments";
    }

    @GetMapping("/profile")
    public String borrowerProfilePage(Authentication authentication, Model model) {

        String email = authentication.getName();

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));

        model.addAttribute("user", user);

        return "borrower/profile";
    }


}
