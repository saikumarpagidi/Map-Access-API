package com.example.demo.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Service.PaymentService;
import com.example.demo.dto.PaymentRequest;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

	@Autowired
	private PaymentService paymentService;

	@PostMapping(value = "/create", produces = "application/json")
	public ResponseEntity<?> createOrder(@RequestBody PaymentRequest paymentRequest) {
		return paymentService.processCreateOrder(paymentRequest);
	}

	@PostMapping("/success")
	public ResponseEntity<?> paymentSuccess(@RequestBody Map<String, String> paymentDetails) {
		return paymentService.processPaymentSuccess(paymentDetails);
	}
}
