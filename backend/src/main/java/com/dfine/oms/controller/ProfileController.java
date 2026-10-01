package com.dfine.dfineoms.controller;

import com.dfine.dfineoms.entity.Customer;
import com.dfine.dfineoms.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController // marks a class as a REST API controller
@RequestMapping("/api/profile") // maps incoming HTTP requests to specific classes or methods based on the URL path
public class ProfileController {

    @Autowired // automatically find, create, and inject the required object (bean) into your class
    private CustomerRepository customerRepository;

    // fetch the logged-in user's profile data
    @GetMapping
    public ResponseEntity<?> getCustomerProfile(Principal principal) {
        // the principal object holds the email extracted from the JWT
        Optional<Customer> customerOpt = customerRepository.findByEmail(principal.getName());

        if (customerOpt.isPresent()) {
            return ResponseEntity.ok(customerOpt.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not found");
    }

    // update the logged-in user's shipping and contact details
    @PutMapping
    public ResponseEntity<?> updateCustomerProfile(@RequestBody Customer updateData, Principal principal) {
        Optional<Customer> existingCustomerOpt = customerRepository.findByEmail(principal.getName());

        if (existingCustomerOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not found");
        }

        Customer existingCustomer = existingCustomerOpt.get();

        existingCustomer.setFirstName(updateData.getFirstName());
        existingCustomer.setLastName(updateData.getLastName());
        existingCustomer.setContactNumber(updateData.getContactNumber());
        existingCustomer.setShippingAddress(updateData.getShippingAddress());
        existingCustomer.setCity(updateData.getCity());
        existingCustomer.setDistrict(updateData.getDistrict());
        existingCustomer.setProvince(updateData.getProvince());
        existingCustomer.setPostalCode(updateData.getPostalCode());

        customerRepository.save(existingCustomer);
        return ResponseEntity.ok("Profile updated successfully");
    }
}