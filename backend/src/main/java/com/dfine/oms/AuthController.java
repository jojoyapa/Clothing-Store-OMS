package com.dfine.dfineoms.controller;

import com.dfine.dfineoms.entity.AppUser;
import com.dfine.dfineoms.entity.Customer;
import com.dfine.dfineoms.repository.UserRepository;
import com.dfine.dfineoms.repository.CustomerRepository;
import com.dfine.dfineoms.dto.CustomerRegistrationRequest;
import com.dfine.dfineoms.dto.LoginRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // marks a class as a REST API controller
@RequestMapping("/api/auth") // maps incoming HTTP requests to specific classes or methods based on the URL path
public class AuthController {

    @Autowired // automatically find, create, and inject the required object (bean) into the class
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/customers") // handle incoming HTTP GET requests
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @PostMapping("/register/customer") // handle incoming HTTP POST requests
    public Customer registerCustomer(@RequestBody CustomerRegistrationRequest request) {

        // AppUser entity
        AppUser newUser = new AppUser();
        newUser.setEmail(request.email);
        newUser.setPasswordHash(request.password); // saved as plain text temporarily
        newUser.setRole("CUSTOMER");
        newUser.setAccountStatus("ACTIVE");
        AppUser savedUser = userRepository.save(newUser);

        // Customer profile
        Customer customer = new Customer();
        customer.setEmail(request.email);
        customer.setFirstName(request.firstName);
        customer.setLastName(request.lastName);
        customer.setContactNumber(request.contactNumber);
        customer.setShippingAddress(request.shippingAddress);
        customer.setCity(request.city);
        customer.setDistrict(request.district);
        customer.setProvince(request.province);
        customer.setPostalCode(request.postalCode);
        customer.setAppUser(savedUser);

        return customerRepository.save(customer);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {

        Optional<AppUser> userOptional = userRepository.findByEmail(request.email);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        AppUser user = userOptional.get();

        // verify password (assuming you update registration to hash passwords with BCrypt)
        // plain text comparison to avoid breaking the frontend with Spring Security defaults
        if (!request.password.equals(user.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        if (!"ACTIVE".equals(user.getAccountStatus())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account is locked or suspended");
        }

        return ResponseEntity.ok("Login successful for role: " + user.getRole());
    }
}