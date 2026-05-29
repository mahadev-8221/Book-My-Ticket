package com.jsp.book.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.razorpay.Utils;
import com.jsp.book.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class WebhookController {

	@Value("${razorpay.webhook-secret}")
	private String webhookSecret;

	@Autowired
	private UserService userService;

	@PostMapping("/payment-webhook")
	public ResponseEntity<String> handleWebhook(@RequestBody String payload,
			@RequestHeader("X-Razorpay-Signature") String signature) {

		try {
			// 1. Verify the signature to ensure it's from Razorpay
			boolean isValid = Utils.verifyWebhookSignature(payload, signature, webhookSecret);

			if (!isValid) {
				log.error("Invalid Webhook Signature!");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Signature");
			}

			// 2. Parse the payload
			JSONObject json = new JSONObject(payload);
			String event = json.getString("event");

			if ("payment.captured".equals(event)) {
				JSONObject payment = json.getJSONObject("payload").getJSONObject("payment").getJSONObject("entity");
				String orderId = payment.getString("order_id");
				String paymentId = payment.getString("id");

				log.info("Payment Captured for Order: {}", orderId);
				
				// 3. Complete the booking in the background
				userService.completeBooking(orderId, paymentId);
			}

			return ResponseEntity.ok("OK");

		} catch (Exception e) {
			log.error("Error processing webhook", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
		}
	}
}
