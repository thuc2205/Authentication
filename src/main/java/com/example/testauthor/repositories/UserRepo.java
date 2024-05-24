package com.example.testauthor.repositories;

import com.example.testauthor.model.Role;
import com.example.testauthor.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {


    Optional<User> findByUsername(String username);

    List<Role> findRolesByUsername(String username);
}
