package com.example.demo.Controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogoutController {

	@GetMapping("/req/logout")
	public String logout() {
		SecurityContextHolder.clearContext(); // Clear the security context to log the user out
		return "redirect:/req/login"; // Redirect the user to the login page
	}
}
