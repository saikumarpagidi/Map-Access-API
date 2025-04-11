package com.example.demo.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Model.MyAppUser;
import com.example.demo.Model.Project;
import com.example.demo.Service.DashboardService;

@Controller
public class DashboardController {

	@Autowired
	private DashboardService dashboardService;

	@GetMapping("/dashboard")
	public String dashboard(Model model, RedirectAttributes redirectAttributes) {
		try {
			MyAppUser user = dashboardService.getAuthenticatedUser();

			model.addAttribute("isPaid", dashboardService.isSubscriptionPaid(user));
			model.addAttribute("isExpired", dashboardService.isSubscriptionExpired(user));
			model.addAttribute("user", user);

			if (user.getSubscriptionEndDate() != null) {
				model.addAttribute("formattedEndDate", user.getSubscriptionEndDate().toString());
			}

			List<Project> projects = dashboardService.getUserProjects(user);
			long totalProjectsCount = dashboardService.getTotalProjectsCount(user);

			model.addAttribute("projects", projects);
			model.addAttribute("totalProjectsCount", totalProjectsCount);

			return "dashboard";
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("error", "An error occurred.");
			return "redirect:/login";
		}
	}
}
