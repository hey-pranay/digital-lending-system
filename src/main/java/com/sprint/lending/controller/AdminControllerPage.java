package com.sprint.lending.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminControllerPage {

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
    public String adminProfilePage() {
        return "admin/profile";
    }


}
