package com.example.demo.Service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.demo.Model.MyAppUser;
import com.example.demo.Model.Project;
import com.example.demo.Repository.MyAppUserRepository;
import com.example.demo.Repository.ProjectRepository;

@Service
public class DashboardService {

	private final MyAppUserRepository userRepository;
	private final ProjectRepository projectRepository;

	public DashboardService(MyAppUserRepository userRepository, ProjectRepository projectRepository) {
		this.userRepository = userRepository;
		this.projectRepository = projectRepository;
	}

	public MyAppUser getAuthenticatedUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String currentEmail = auth.getName();
		return userRepository.findByEmail(currentEmail).orElseThrow(() -> new RuntimeException("User not found"));
	}

	public boolean isSubscriptionPaid(MyAppUser user) {
		return "PAID".equalsIgnoreCase(user.getPaymentStatus());
	}

	public boolean isSubscriptionExpired(MyAppUser user) {
		return user.getSubscriptionEndDate() != null && user.getSubscriptionEndDate().isBefore(LocalDate.now());
	}

	public List<Project> getUserProjects(MyAppUser user) {
		return projectRepository.findByUserInstituteId(user.getInstituteId());
	}

	public long getTotalProjectsCount(MyAppUser user) {
		return projectRepository.countByUserInstituteId(user.getInstituteId());
	}
}
