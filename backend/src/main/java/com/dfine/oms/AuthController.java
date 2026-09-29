package com.dfine.dfineoms.controller;
import com.dfine.dfineoms.dto.CustomerRegistrationRequest;
import com.dfine.dfineoms.entity.AppUser;
import com.dfine.dfineoms.entity.Customer;
import com.dfine.dfineoms.repository.UserRepository;
import com.dfine.dfineoms.repository.CustomerRepository;
import com.dfine.dfineoms.dto.CustomerRegistrationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController // marks a class as a REST API controller.
@RequestMapping("/api/auth") // maps incoming HTTP requests
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
        newUser.setPasswordHash(request.password);
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
}