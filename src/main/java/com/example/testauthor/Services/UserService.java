package com.example.testauthor.Services;

import com.example.testauthor.component.JwtTokenUtil;
import com.example.testauthor.model.Role;
import com.example.testauthor.model.User;
import com.example.testauthor.repositories.RoleRepo;
import com.example.testauthor.repositories.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserService {

    private final UserRepo userRepo;


    private final RoleRepo roleRepo;


    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtil jwtTokenUtil;

    public User save(User user) {
        Optional<Role> role = roleRepo.findById(Long.valueOf(2));
        Role roleObj = role.get();
        user.setRole(roleObj);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    @Deprecated
    public String login(String username, String password) throws Exception {
        Optional<User> userOptional = userRepo.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                username, password, user.getAuthorities()
        );

        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return jwtTokenUtil.generateToken(user);
    }




}
