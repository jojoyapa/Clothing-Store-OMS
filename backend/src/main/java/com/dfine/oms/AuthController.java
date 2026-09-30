package com.dfine.dfineoms.controller;

import com.dfine.dfineoms.entity.AppUser;
import com.dfine.dfineoms.entity.Customer;
import com.dfine.dfineoms.entity.UserLoginAudit;
import com.dfine.dfineoms.repository.UserRepository;
import com.dfine.dfineoms.repository.CustomerRepository;
import com.dfine.dfineoms.repository.UserLoginAuditRepository;
import com.dfine.dfineoms.dto.CustomerRegistrationRequest;
import com.dfine.dfineoms.dto.LoginRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserLoginAuditRepository auditRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/customers")
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @PostMapping("/register/customer")
    public Customer registerCustomer(@RequestBody CustomerRegistrationRequest request) {

        AppUser newUser = new AppUser();
        newUser.setEmail(request.email);
        newUser.setPasswordHash(passwordEncoder.encode(request.password)); // passwords are securely hashed
        newUser.setRole("CUSTOMER");
        newUser.setAccountStatus("ACTIVE");
        AppUser savedUser = userRepository.save(newUser);

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
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ipAddress = httpRequest.getRemoteAddr();

        Optional<AppUser> userOptional = userRepository.findByEmail(request.email);

        if (userOptional.isEmpty()) {
            logAudit(null, request.email, ipAddress, "FAILED_BAD_CREDENTIALS");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        AppUser user = userOptional.get();

        // secure BCrypt comparison instead of plain text
        if (!passwordEncoder.matches(request.password, user.getPasswordHash())) {
            logAudit(user, request.email, ipAddress, "FAILED_BAD_CREDENTIALS");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        if (!"ACTIVE".equals(user.getAccountStatus())) {
            logAudit(user, request.email, ipAddress, "FAILED_LOCKED");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account is locked or suspended");
        }

        logAudit(user, request.email, ipAddress, "SUCCESS");
        return ResponseEntity.ok("Login successful for role: " + user.getRole());
    }

    private void logAudit(AppUser user, String email, String ip, String status) {
        UserLoginAudit audit = new UserLoginAudit();
        audit.setAppUser(user);
        audit.setAttemptedEmail(email);
        audit.setIpAddress(ip);
        audit.setLoginStatus(status);
        audit.setLoginTime(LocalDateTime.now());
        auditRepository.save(audit);
    }
}