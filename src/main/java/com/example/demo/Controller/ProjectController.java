package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Model.Project;
import com.example.demo.Service.ProjectService;

@Controller
public class ProjectController {

	@Autowired
	private ProjectService projectService;

	@GetMapping("/projects/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("project", new Project());
		return "project-registration";
	}

	@PostMapping("/projects/register")
	public String registerProject(@ModelAttribute Project project, RedirectAttributes redirectAttributes) {
		try {
			// Check if the user is allowed to register a project
			if (!projectService.canRegisterProject()) {
				redirectAttributes.addFlashAttribute("errorMessage",
						"You must have an active subscription to register a new project. Please purchase a subscription.");
				return "redirect:/projects/register";
			}

			// Register the project
			projectService.registerProject(project);
			redirectAttributes.addFlashAttribute("successMessage", "Project registered successfully!");
			return "redirect:/dashboard";
		} catch (RuntimeException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			return "redirect:/projects/register";
		}
	}

	@GetMapping("/projects/{projectId}")
	public String viewProjectDetails(@PathVariable Long projectId, Model model) {
		Project project = projectService.getProjectById(projectId);

		if (project == null) {
			throw new RuntimeException("Project not found");
		}

		model.addAttribute("project", project);
		return "project-details";
	}
}
