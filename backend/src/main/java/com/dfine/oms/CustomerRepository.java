package com.dfine.dfineoms.repository;
import com.dfine.dfineoms.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}