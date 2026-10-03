package com.dfine.dfineoms.repository;

import com.dfine.dfineoms.entity.StoreStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreStaffRepository extends JpaRepository<StoreStaff, Long> {
}