package com.example.demo.Service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;

@Service
public class RazorpayService {

	private RazorpayClient client;

	public RazorpayService(@Value("${razorpay.key_id}") String keyId, @Value("${razorpay.key_secret}") String keySecret)
			throws Exception {
		this.client = new RazorpayClient(keyId, keySecret);
	}

	public String createOrder(String amount, String currency, String receipt) throws Exception {
		JSONObject options = new JSONObject();
		options.put("amount", Integer.parseInt(amount.trim())); // Amount in paise
		options.put("currency", currency);
		options.put("receipt", receipt);

		Order order = client.orders.create(options);
		return order.toString();
	}
}
