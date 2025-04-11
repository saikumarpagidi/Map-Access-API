package com.example.demo.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Model.Payment;
import com.razorpay.Order;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	Payment findByUsername(String username);

	Optional<Payment> findByPaymentId(String orderId);

	static String save(Order order) {
		// TODO Auto-generated method stub
		return null;
	}
}
