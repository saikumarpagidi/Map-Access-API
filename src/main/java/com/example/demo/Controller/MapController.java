package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Model.MyAppUser;
import com.example.demo.Service.MapService;

@Controller
public class MapController {

	@Autowired
	private MapService mapService;

	@GetMapping("/map")
	public String mapAccess(Model model, RedirectAttributes redirectAttributes) {
		try {
			MyAppUser user = mapService.getAuthenticatedUser();

			if (!mapService.isAccessAllowed(user)) {
				redirectAttributes.addFlashAttribute("error", mapService.getAccessDeniedMessage(user));
				return "redirect:/req/payment";
			}

			model.addAttribute("user", user);
			return "map"; // Thymeleaf template for map access
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("error", "An error occurred while accessing the map.");
			return "redirect:/dashboard";
		}
	}
}
