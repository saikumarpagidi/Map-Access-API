package com.example.demo.Model;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "login")
public class MyAppUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long instituteId;

	@Column(unique = true, nullable = false)
	private String username;

	@Column(nullable = false)
	private String institute_name;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(unique = true, nullable = false)
	private String phone;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String department_name;

	@Column(nullable = false)
	private String paymentStatus = "PENDING";

	private LocalDate subscriptionStartDate;

	private LocalDate subscriptionEndDate;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private EmailVerification emailVerification;

	// Helper methods to manage relationships
	public void addEmailVerification(EmailVerification verification) {
		verification.setUser(this);
		this.emailVerification = verification;
	}

	public void removeEmailVerification() {
		if (this.emailVerification != null) {
			this.emailVerification.setUser(null);
			this.emailVerification = null;
		}
	}

	// Getters and Setters

	public Long getInstituteId() {
		return instituteId;
	}

	public EmailVerification getEmailVerification() {
		return emailVerification;
	}

	public void setEmailVerification(EmailVerification emailVerification) {
		this.emailVerification = emailVerification;
	}

	public void setInstituteId(Long instituteId) {
		this.instituteId = instituteId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getInstitute_name() {
		return institute_name;
	}

	public void setInstitute_name(String institute_name) {
		this.institute_name = institute_name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDepartment_name() {
		return department_name;
	}

	public void setDepartment_name(String department_name) {
		this.department_name = department_name;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public LocalDate getSubscriptionStartDate() {
		return subscriptionStartDate;
	}

	public void setSubscriptionStartDate(LocalDate subscriptionStartDate) {
		this.subscriptionStartDate = subscriptionStartDate;
	}

	public LocalDate getSubscriptionEndDate() {
		return subscriptionEndDate;
	}

	public void setSubscriptionEndDate(LocalDate subscriptionEndDate) {
		this.subscriptionEndDate = subscriptionEndDate;
	}

}
