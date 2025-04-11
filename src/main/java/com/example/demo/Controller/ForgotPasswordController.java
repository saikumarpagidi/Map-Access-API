package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.Service.ForgotPasswordService;

@Controller
public class ForgotPasswordController {

	@Autowired
	private ForgotPasswordService forgotPasswordService;

	@GetMapping("/forgot-password")
	public String showForgotPasswordPage() {
		return "forgot-password"; // Thymeleaf template for entering registered email
	}

	@PostMapping("/forgot-password")
	public String processForgotPassword(@RequestParam("email") String email, Model model) {
		if (!forgotPasswordService.isEmailExists(email)) {
			model.addAttribute("errorMessage", "Email ID does not exist.");
			return "forgot-password";
		}

		String otp = forgotPasswordService.generateOtp();
		forgotPasswordService.saveOtp(email, otp); // Save OTP temporarily

		forgotPasswordService.sendOtpEmail(email, otp);

		model.addAttribute("email", email);
		return "reset-password"; // Redirect to reset password page
	}

	@PostMapping("/reset-password")
	public String resetPassword(@RequestParam("email") String email, @RequestParam("otp") String otp,
			@RequestParam("newPassword") String newPassword, @RequestParam("confirmPassword") String confirmPassword,
			Model model) {

		if (!forgotPasswordService.isOtpValid(email, otp)) {
			model.addAttribute("errorMessage", "Wrong OTP.");
			model.addAttribute("email", email);
			return "reset-password";
		}

		if (!newPassword.equals(confirmPassword)) {
			model.addAttribute("errorMessage", "Passwords must match.");
			model.addAttribute("email", email);
			return "reset-password";
		}

		if (forgotPasswordService.resetPassword(email, newPassword)) {
			forgotPasswordService.clearOtp(email); // Clean up the OTP
			model.addAttribute("successMessage", "Your password was successfully changed!");
			return "reset-password";
		} else {
			model.addAttribute("errorMessage", "There was an error resetting your password.");
			return "reset-password";
		}
	}
}
