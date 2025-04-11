package com.example.demo.Service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.demo.Model.MyAppUser;
import com.example.demo.Model.Project;
import com.example.demo.Repository.MyAppUserRepository;
import com.example.demo.Repository.ProjectRepository;

@Service
public class ProjectService {

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private MyAppUserRepository myAppUserRepository;

	public boolean canRegisterProject() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();

		MyAppUser user = myAppUserRepository.findByEmail(username).orElse(null);

		if (user == null) {
			throw new RuntimeException("User not found");
		}

		// Check payment status and subscription validity
		return "PAID".equalsIgnoreCase(user.getPaymentStatus())
				&& (user.getSubscriptionEndDate() == null || !user.getSubscriptionEndDate().isBefore(LocalDate.now()));
	}

	public boolean registerProject(Project project) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();

		MyAppUser user = myAppUserRepository.findByEmail(username).orElse(null);

		if (user == null) {
			return false;
		}

		// Set the user and save the project
		project.setUser(user);
		projectRepository.save(project);
		return true;
	}

	public Project getProjectById(Long projectId) {
		return projectRepository.findById(projectId).orElse(null);
	}
}
