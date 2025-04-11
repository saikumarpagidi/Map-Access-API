package com.example.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Model.MyAppUser;
import com.example.demo.Repository.MyAppUserRepository;

@Service
public class UserService {

	@Autowired
	private MyAppUserRepository myAppUserRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private EmailVerificationService emailVerificationService;

	public String registerUser(MyAppUser user) throws Exception {
		// Check if the username already exists
		if (myAppUserRepository.findByUsername(user.getUsername()).isPresent()) {
			throw new Exception("Error: Username already exists.");
		}

		// Check if the email already exists
		if (myAppUserRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new Exception("Error: Email already exists.");
		}

		// Encrypt the user's password
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		// Set default payment status
		user.setPaymentStatus("PENDING");

		// Save the user to the database
		MyAppUser savedUser = myAppUserRepository.save(user);

		// Generate the email verification token
		String token = emailVerificationService.createVerificationToken(savedUser);

		// Send the email verification email
		emailVerificationService.sendVerificationEmail(savedUser.getEmail(), token);

		// Return success message
		return "User registered successfully. Please verify your email to complete the process.";
	}
}
