package com.sprint.lending.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/borrower")
public class BorrowerPageController {

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
    public String borrowerProfilePage() {
        return "borrower/profile";
    }

}
