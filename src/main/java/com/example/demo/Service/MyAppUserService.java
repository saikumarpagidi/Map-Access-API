package com.example.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.Model.EmailVerification;
import com.example.demo.Model.MyAppUser;
import com.example.demo.Repository.EmailVerificationRepository;
import com.example.demo.Repository.MyAppUserRepository;

@Service
public class MyAppUserService implements UserDetailsService {

	@Autowired
	private MyAppUserRepository repository;

	@Autowired
	private EmailVerificationRepository verificationRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// Fetch user details from the database
		MyAppUser user = repository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

		// Check if the email is verified
		EmailVerification verification = verificationRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Verification details not found for: " + email));

		if (!verification.isVerified()) {
			throw new UsernameNotFoundException("Email not verified for: " + email);
		}

		// Map user to UserDetails with roles
		return User.builder().username(user.getEmail()).password(user.getPassword())
				.roles(user.getPaymentStatus().equals("PAID") ? "PAID_USER" : "PENDING_USER").build();
	}
}