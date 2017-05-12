package com.shortcuts.example.single_signon.shortcuts;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Encapsulate the steps required to validate a customer token by callback to a Salon Resource Server;
 */
@Service
public class CustomerTokenValidationService {

    public boolean validate(AuthenticateCustomerBody authenticateCustomerBody) throws Exception {

        URI callbackUri = getCallbackUri(authenticateCustomerBody.getTokenType());

        // prepare the callback request to the Salon Resource Server
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", String.format("Bearer %s", authenticateCustomerBody.getAccessToken()));
        RequestEntity<String> request = new RequestEntity<>(headers, HttpMethod.GET, callbackUri);

        // make the HTTP request to the Salon Resource Server
        ResponseEntity<String> response = new RestTemplate().exchange(request, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            // validation of customer token succeeded
            return true;
        } else {
            // validation of customer token failed
            return false;
        }

    }

    private URI getCallbackUri(String tokenType) throws URISyntaxException {
        return new URI("http://localhost:8080/validate");
    }

}
