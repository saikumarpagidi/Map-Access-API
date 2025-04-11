package com.example.demo.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Model.MyAppUser;
import com.example.demo.Repository.MyAppUserRepository;

@Service
public class ForgotPasswordService {

	@Autowired
	private MyAppUserRepository userRepository;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// Store OTP in-memory for simplicity (can be moved to a database for
	// production)
	private final Map<String, String> otpStorage = new HashMap<>();

	public String generateOtp() {
		return String.format("%06d", new Random().nextInt(999999));
	}

	public boolean isEmailExists(String email) {
		return userRepository.findByEmail(email).isPresent();
	}

	public void saveOtp(String email, String otp) {
		otpStorage.put(email, otp); // Save OTP temporarily
	}

	public void sendOtpEmail(String email, String otp) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("Reset Password OTP");
		message.setText("Your OTP for password reset is: " + otp);
		mailSender.send(message);
	}

	public boolean isOtpValid(String email, String otp) {
		String storedOtp = otpStorage.get(email);
		return storedOtp != null && storedOtp.equals(otp);
	}

	public boolean resetPassword(String email, String newPassword) {
		Optional<MyAppUser> userOptional = userRepository.findByEmail(email);
		if (userOptional.isPresent()) {
			MyAppUser user = userOptional.get();
			user.setPassword(passwordEncoder.encode(newPassword)); // Encode the password
			userRepository.save(user);
			return true;
		}
		return false;
	}

	public void clearOtp(String email) {
		otpStorage.remove(email); // Clean up the OTP
	}
}
