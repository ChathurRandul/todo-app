package com.cedar.todoapp.services;

import com.cedar.todoapp.dtos.LoginUserRequest;
import com.cedar.todoapp.dtos.RegisterUserRequest;
import com.cedar.todoapp.exceptions.UserNotFoundException;
import com.cedar.todoapp.exceptions.UsernameAlreadyExistsException;
import com.cedar.todoapp.models.User;
import com.cedar.todoapp.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterUserRequest registerRequest;
    private LoginUserRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        registerRequest = new RegisterUserRequest("John Doe", "john.doe@example.com", "password123");
        loginRequest = new LoginUserRequest("john.doe@example.com", "password123");

        user = User.builder()
                .id(1)
                .fullName("John Doe")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .build();
    }

    @Test
    void testRegister_ShouldRegisterUser_WhenEmailDoesNotExist() {
        when(userRepository.findByEmail(registerRequest.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerRequest.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        Integer userId = authenticationService.register(registerRequest);

        assertEquals(user.getId(), userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegister_ShouldThrowUsernameAlreadyExistsException_WhenEmailAlreadyExists() {
        when(userRepository.findByEmail(registerRequest.email())).thenReturn(Optional.of(user));

        assertThrows(UsernameAlreadyExistsException.class, () -> authenticationService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAuthenticate_ShouldReturnUser_WhenAuthenticationIsSuccessful() {
        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(user));

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
        when(authenticationManager.authenticate(authToken)).thenReturn(authToken);

        User authenticatedUser = authenticationService.authenticate(loginRequest);

        assertEquals(user, authenticatedUser);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(loginRequest.email());
    }

    @Test
    void testAuthenticate_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.empty());
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
        when(authenticationManager.authenticate(authToken)).thenReturn(authToken);

        assertThrows(UserNotFoundException.class, () -> authenticationService.authenticate(loginRequest));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByEmail(loginRequest.email());
    }
}
