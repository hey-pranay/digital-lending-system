
package com.sprint.lending.controller;

import com.sprint.lending.dto.request.RegisterRequest;
import com.sprint.lending.service.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthPageController {

    private final AuthService authService;

    public AuthPageController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginPage(Authentication authentication) {

        if (authentication != null && authentication.isAuthenticated()) {
            boolean isAdmin = authentication.getAuthorities()
                    .stream()
                    .anyMatch(authority ->
                            authority.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin) {
                return "redirect:/admin/dashboard";
            }

            return "redirect:/borrower/dashboard";
        }

        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }


    @PostMapping("/register")
    public String registerPage(
            @ModelAttribute RegisterRequest registerRequest,
            RedirectAttributes redirectAttributes
    ) {

        try {
            authService.register(registerRequest);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Registration successful! Please log in."
            );
            return "redirect:/login";
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            return "redirect:/register";
        }

    }
}
