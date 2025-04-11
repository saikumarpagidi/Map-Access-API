package com.example.demo.Service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.Model.MyAppUser;
import com.example.demo.Repository.MyAppUserRepository;

@Service
public class SubscriptionScheduler {

	@Autowired
	private MyAppUserRepository userRepository;

//	@Scheduled(fixedRate = 20000)
	@Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
	public void updateExpiredSubscriptions() {
		List<MyAppUser> users = userRepository.findAll();
		LocalDate today = LocalDate.now();

		for (MyAppUser user : users) {
			if (user.getSubscriptionEndDate() != null && user.getSubscriptionEndDate().isBefore(today)) {
				user.setPaymentStatus("PENDING"); // Update status to "PENDING"
				user.setSubscriptionStartDate(null);
				user.setSubscriptionEndDate(null);
				userRepository.save(user);
			}
		}
	}
}
