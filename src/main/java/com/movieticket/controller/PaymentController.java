package com.movieticket.controller;

import com.movieticket.dto.ApiResponse;
import com.movieticket.dto.PaymentRequest;
import com.movieticket.dto.PaymentResponse;
import com.movieticket.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/process")
    public ResponseEntity<ApiResponse> processPayment(@RequestBody PaymentRequest req) {
        PaymentResponse response = paymentService.processPayment(req);
        if (response.isSuccess()) {
            return ResponseEntity.ok(ApiResponse.ok("Payment authorized successfully", response));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error(response.getMessage()));
        }
    }
}
