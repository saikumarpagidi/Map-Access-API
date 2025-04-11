package com.example.demo.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.Model.MyAppUser;
import com.example.demo.Model.Payment;
import com.example.demo.Repository.MyAppUserRepository;
import com.example.demo.Repository.PaymentRepository;
import com.example.demo.dto.PaymentRequest;

@Service
public class PaymentService {

	@Autowired
	private RazorpayService razorpayService;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private MyAppUserRepository myAppUserRepository;

	public ResponseEntity<?> processCreateOrder(PaymentRequest paymentRequest) {
		try {
			// Validate user
			Optional<MyAppUser> userOptional = myAppUserRepository.findByUsername(paymentRequest.getUsername());
			if (userOptional.isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not registered.");
			}

			MyAppUser user = userOptional.get();
			if (!user.getEmail().equals(paymentRequest.getEmail())) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body("Username and email do not match for the logged-in user.");
			}

			// Create Razorpay order
			String amountInPaise = String.valueOf(Integer.parseInt(paymentRequest.getAmount().trim()) * 100);
			String orderDetails = razorpayService.createOrder(amountInPaise, "INR",
					paymentRequest.getUsername() + "_receipt");
			JSONObject orderDetailsJson = new JSONObject(orderDetails);

			// Save payment details
			Payment payment = new Payment();
			payment.setUsername(paymentRequest.getUsername());
			payment.setEmail(paymentRequest.getEmail());
			payment.setAddress(paymentRequest.getAddress());
			payment.setSubscriptionPlan(paymentRequest.getPlan());
			payment.setAmount(Integer.parseInt(paymentRequest.getAmount().trim()));
			payment.setPaymentStatus("PENDING");
			payment.setPaymentId(orderDetailsJson.getString("id"));
			paymentRepository.save(payment);

			return ResponseEntity.ok(orderDetailsJson.toMap());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error creating payment order: " + e.getMessage());
		}
	}

	public ResponseEntity<?> processPaymentSuccess(Map<String, String> paymentDetails) {
		try {
			String paymentId = paymentDetails.get("razorpay_payment_id");
			String orderId = paymentDetails.get("razorpay_order_id");

			Optional<Payment> paymentOptional = paymentRepository.findByPaymentId(orderId);
			if (paymentOptional.isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payment details.");
			}

			Payment payment = paymentOptional.get();
			payment.setPaymentStatus("SUCCESS");
			payment.setPaymentId(paymentId);
			paymentRepository.save(payment);

			Optional<MyAppUser> userOptional = myAppUserRepository.findByUsername(payment.getUsername());
			if (userOptional.isPresent()) {
				MyAppUser user = userOptional.get();
				user.setPaymentStatus("PAID");

				// Set subscription start and end dates
				LocalDate startDate = LocalDate.now();
				user.setSubscriptionStartDate(startDate);

				switch (payment.getSubscriptionPlan()) {
				case "3-months":
					user.setSubscriptionEndDate(startDate.plusMonths(3));
					break;
				case "6-months":
					user.setSubscriptionEndDate(startDate.plusMonths(6));
					break;
				case "1-year":
					user.setSubscriptionEndDate(startDate.plusYears(1));
					break;
				}

				myAppUserRepository.save(user);
			}

			return ResponseEntity.ok("Payment successful and user subscription updated!");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error processing payment success: " + e.getMessage());
		}
	}
}
