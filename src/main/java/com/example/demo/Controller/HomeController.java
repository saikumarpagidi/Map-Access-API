package com.example.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/")
	public String index() {
		return "index"; // Returns the index.html template
	}

	@GetMapping("/req/login")
	public String login() {
		return "login"; // The name of the login HTML page
	}

	@GetMapping("/req/payment")
	public String payment() {
		return "payment";
	}

	@GetMapping("/projects/new")
	public String ProjectSignup() {
		return "project-registration";

	}

}
