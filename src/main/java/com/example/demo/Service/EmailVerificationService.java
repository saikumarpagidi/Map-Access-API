package com.example.demo.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Model.EmailVerification;
import com.example.demo.Model.MyAppUser;
import com.example.demo.Repository.EmailVerificationRepository;
import com.example.demo.Repository.MyAppUserRepository;

@Service
public class EmailVerificationService {

	@Autowired
	private EmailVerificationRepository verificationRepository;

	@Autowired
	private MyAppUserRepository userRepository;

	@Autowired
	private EmailService emailService;

	public String createVerificationToken(MyAppUser user) {
		String token = UUID.randomUUID().toString();
		LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(15);

		EmailVerification verification = verificationRepository.findByEmail(user.getEmail())
				.orElse(new EmailVerification());

		verification.setUser(user);
		verification.setEmail(user.getEmail());
		verification.setToken(token);
		verification.setExpiryDate(expiryDate);
		verification.setVerified(false);

		verificationRepository.save(verification);
		return token;
	}

	public Optional<MyAppUser> verifyToken(String token) {
		Optional<EmailVerification> verification = verificationRepository.findByToken(token);

		if (verification.isPresent() && verification.get().getExpiryDate().isAfter(LocalDateTime.now())
				&& !verification.get().isVerified()) {
			EmailVerification verified = verification.get();
			verified.setVerified(true);
			verificationRepository.save(verified);
			return Optional.of(verified.getUser());
		}
		return Optional.empty();
	}

	public void resendVerificationLink(String email) {
		Optional<MyAppUser> userOptional = userRepository.findByEmail(email);

		if (userOptional.isEmpty()) {
			throw new IllegalArgumentException("Email not found. Please check your email.");
		}

		MyAppUser user = userOptional.get();

		if (user.getEmailVerification() != null && user.getEmailVerification().isVerified()) {
			throw new IllegalArgumentException("User already verified. Please log in.");
		}

		String token = createVerificationToken(user);
		sendVerificationEmail(email, token);
	}

	public void sendVerificationEmail(String email, String token) {
		String link = "http://localhost:8080/verification/confirm?token=" + token;
		String body = "Click the link to verify your email: " + link + "\n\nThis link will expire in 15 minutes.";
		emailService.sendEmail(email, "Email Verification", body);
	}
}
