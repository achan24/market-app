package ie.revalue.authenticatedbackend.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin("*")
public class PayPalController {

    @Value("${paypal.client.id}")
    private String paypalClientId;


    @GetMapping("/api/paypal-client-id")
    public Map<String, String> getPayPalClientId() {
        Map<String, String> response = new HashMap<>();
        response.put("clientId", paypalClientId);
        return response;
    }
}
