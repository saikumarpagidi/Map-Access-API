package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.demo.Model.MyAppUser;
import com.example.demo.Service.UserService;

@Controller
public class RegistrationController {

	@Autowired
	private UserService userService;

	@GetMapping("/req/signup")
	public String signup() {
		return "signup"; // Return signup page
	}

	@PostMapping(value = "/req/signup", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> createUser(@RequestBody MyAppUser user) {
		try {
			String responseMessage = userService.registerUser(user);
			return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
}
