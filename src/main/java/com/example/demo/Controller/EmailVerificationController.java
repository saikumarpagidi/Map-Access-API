package com.example.demo.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.Model.MyAppUser;
import com.example.demo.Service.EmailVerificationService;

@Controller
@RequestMapping("/verification")
public class EmailVerificationController {

	@Autowired
	private EmailVerificationService emailVerificationService;

	// Confirm Email Verification
	@GetMapping("/confirm")
	public String confirmEmail(@RequestParam String token, Model model) {
		Optional<MyAppUser> user = emailVerificationService.verifyToken(token);

		if (user.isPresent()) {
			model.addAttribute("message", "Email successfully verified! Please log in.");
			return "verification-success";
		}

		model.addAttribute("error", "Invalid or expired verification link. Please request a new one.");
		return "resend-verification";
	}

	// Render the resend verification page
	@GetMapping("/resend")
	public String showResendVerificationPage() {
		return "resend-verification";
	}

	// Resend Verification Link API
	@PostMapping("/resend-link")
	public ResponseEntity<String> resendVerificationLink(@RequestParam String email) {
		try {
			emailVerificationService.resendVerificationLink(email);
			return ResponseEntity.ok("Verification link sent. Please check your email.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
		}
	}
}
