package com.example.demo.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.Service.MyAppUserService;

@Configuration
public class SecurityConfig {

	private final MyAppUserService appUserService;

	public SecurityConfig( MyAppUserService appUserService) {
		this.appUserService = appUserService;
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(appUserService);
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf(csrf -> csrf.disable()) // Disable CSRF protection for simplicity (ensure this is reviewed in
													// production)
				.formLogin(form -> form.loginPage("/req/login").permitAll().defaultSuccessUrl("/dashboard")
						.failureHandler((request, response, exception) -> {
							String errorMessage = "Invalid email or password. Please try again.";

							// Check if the exception contains an email verification issue
							if (exception.getMessage().contains("Email not verified")) {
								errorMessage = "Please complete your email verification before logging in.";
							}

							// Add the error message to the session and redirect to the login page
							request.getSession().setAttribute("error", errorMessage);
							response.sendRedirect("/req/login?error=true");
						}))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/", "/index", "/req/signup", "/reset-password", "/req/login",
								"/forgot-password", "/verification/**", "/css/**", "/js/**")
						.permitAll() // Allow access to signup, login, verification pages, and static resources
						.anyRequest().authenticated() // Require authentication for all other requests
				).logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/req/login?logout=true")
						.invalidateHttpSession(true).deleteCookies("JSESSIONID").permitAll());

		return httpSecurity.build();
	}
}
