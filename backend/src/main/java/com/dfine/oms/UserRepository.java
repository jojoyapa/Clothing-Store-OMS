package com.dfine.dfineoms.repository;
import com.dfine.dfineoms.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {

    // Spring automatically writes the SQL query for this just by reading the method name
    AppUser findByEmail(String email);
}