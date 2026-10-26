package com.movieticket.service;

import com.movieticket.dto.PaymentRequest;
import com.movieticket.dto.PaymentResponse;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {

    public PaymentResponse processPayment(PaymentRequest req) {
        if (req.getAmount() == null || req.getAmount() <= 0) {
            return new PaymentResponse(false, null, "Invalid payment amount");
        }

        String method = req.getPaymentMethod();
        if (method == null) {
            method = "CREDIT_CARD";
        }

        // Validate method specific details
        if ("CREDIT_CARD".equalsIgnoreCase(method) || "DEBIT_CARD".equalsIgnoreCase(method)) {
            String card = req.getCardNumber() != null ? req.getCardNumber().replaceAll("\\s+", "") : "";
            if (card.length() < 12) {
                return new PaymentResponse(false, null, "Invalid card number (must have at least 12 digits)");
            }
        } else if ("UPI".equalsIgnoreCase(method)) {
            if (req.getUpiId() == null || !req.getUpiId().contains("@")) {
                return new PaymentResponse(false, null, "Invalid UPI ID (e.g. yourname@okaxis)");
            }
        } else if ("NET_BANKING".equalsIgnoreCase(method)) {
            if (req.getBankName() == null || req.getBankName().trim().isEmpty()) {
                return new PaymentResponse(false, null, "Please select your bank for Net Banking");
            }
        }

        // Generate Transaction ID
        String txnId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() + "-" + System.currentTimeMillis() % 10000;
        return new PaymentResponse(true, txnId, "Payment authorized successfully via " + method.replace("_", " "));
    }
}
