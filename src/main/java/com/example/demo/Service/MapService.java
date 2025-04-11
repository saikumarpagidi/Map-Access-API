package com.example.demo.Service;

import java.time.LocalDate;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.demo.Model.MyAppUser;
import com.example.demo.Repository.MyAppUserRepository;

@Service
public class MapService {

	private final MyAppUserRepository userRepository;

	public MapService(MyAppUserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public MyAppUser getAuthenticatedUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String currentEmail = auth.getName();
		return userRepository.findByEmail(currentEmail).orElseThrow(() -> new RuntimeException("User not found"));
	}

	public boolean isAccessAllowed(MyAppUser user) {
		return "PAID".equalsIgnoreCase(user.getPaymentStatus())
				&& (user.getSubscriptionEndDate() == null || !user.getSubscriptionEndDate().isBefore(LocalDate.now()));
	}

	public String getAccessDeniedMessage(MyAppUser user) {
		if (!"PAID".equalsIgnoreCase(user.getPaymentStatus())) {
			return "Access denied. Please purchase a subscription.";
		}

		if (user.getSubscriptionEndDate() != null && user.getSubscriptionEndDate().isBefore(LocalDate.now())) {
			return "Your subscription has expired. Please renew it.";
		}

		return "Access denied.";
	}
}
