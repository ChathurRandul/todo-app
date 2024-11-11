package com.cedar.todoapp.services;

import com.cedar.todoapp.dtos.LoginUserRequest;
import com.cedar.todoapp.dtos.RegisterUserRequest;
import com.cedar.todoapp.exceptions.UserNotFoundException;
import com.cedar.todoapp.exceptions.UsernameAlreadyExistsException;
import com.cedar.todoapp.models.User;
import com.cedar.todoapp.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public Integer register(@Valid RegisterUserRequest input) {
        if (userRepository.findByEmail(input.email()).isPresent()) {
            log.warn("Attempt to register with existing email: {}", input.email());
            throw new UsernameAlreadyExistsException("Username already exists: " + input.email());
        }

        User user = User.builder()
                .fullName(input.fullName())
                .email(input.email())
                .password(passwordEncoder.encode(input.password()))
                .build();

        User createdUser = userRepository.save(user);
        log.info("User registered successfully with email: {}", input.email());
        return createdUser.getId();
    }

    public User authenticate(@Valid LoginUserRequest input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.email(),
                        input.password()
                )
        );

        return userRepository.findByEmail(input.email())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + input.email()));
    }
}
