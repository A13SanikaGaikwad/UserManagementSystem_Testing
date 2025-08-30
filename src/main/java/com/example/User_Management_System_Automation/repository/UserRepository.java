package com.example.User_Management_System_Automation.repository;

import com.example.User_Management_System_Automation.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
