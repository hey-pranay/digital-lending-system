package com.sprint.lending.controller;

import com.sprint.lending.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminControllerPage {

    private final UserRepository userRepository;

    public AdminControllerPage(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/applications")
    public String loanApplicationsPage() {
        return "admin/applications";
    }

    @GetMapping("/loans")
    public String allLoansPage() {
        return "admin/loans";
    }

    @GetMapping("/repayments")
    public String repaymentsPage() {
        return "admin/repayments";
    }

    @GetMapping("/profile")
    public String adminProfilePage(Authentication authentication, Model model) {

        String email = authentication.getName();

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);

        return "admin/profile";
    }


}
