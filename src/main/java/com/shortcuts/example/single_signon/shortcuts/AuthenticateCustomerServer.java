package com.shortcuts.example.single_signon.shortcuts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shortcuts.example.single_signon.salon.AuthenticationCredentials;
import com.shortcuts.example.single_signon.salon.AuthenticationTokenService;
import com.shortcuts.example.single_signon.salon.ValidatedCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * This is an example Shortcuts server which provides an endpoint
 * for salons to authenticate customers.
 */
@RestController
public class AuthenticateCustomerServer {

    @Autowired
    private CustomerTokenValidationService customerTokenValidationService;

    @RequestMapping(value = "/authenticate_customer", method = RequestMethod.POST)
    private ResponseEntity<String> authenticate(RequestEntity<String> request) throws IOException {

        try {

            // get the token from the Authorization header
            String authorizationHeader = request.getHeaders().get("Authorization").get(0);

            if (validateOAuthToken(authorizationHeader)) {

                // Salon credentials are good

                // deserialize request body
                String body = request.getBody();
                AuthenticateCustomerBody authenticateCustomerBody = new ObjectMapper().readValue(body, AuthenticateCustomerBody.class);

                if (validateCustomerToken(authenticateCustomerBody)) {
                    // customer token is good
                    return new ResponseEntity<>("this body would normally contain a full Shortcuts response", HttpStatus.OK);
                }

            }

        } catch (Exception e) {
            // unauthorized, fall through
            e.printStackTrace();
        }

        // authentication failed, return error
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Naive example of OAuth validation
     *
     * @param authorizationHeader
     * @return a boolean indicating if teh OAuth credentials were valid.
     */
    private boolean validateOAuthToken(String authorizationHeader) {
        return !StringUtils.isEmpty(authorizationHeader);
    }

    /**
     * An example implementation of customer token validation using
     * callback to the customer's Salon Resource Server.
     *
     * @param authenticateCustomerBody
     * @return a boolean indicating whether the Salon Resource Server validated the customer token.
     */
    private boolean validateCustomerToken(AuthenticateCustomerBody authenticateCustomerBody) throws Exception {
        return customerTokenValidationService.validate(authenticateCustomerBody);
    }

}
