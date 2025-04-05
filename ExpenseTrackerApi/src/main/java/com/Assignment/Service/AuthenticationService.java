package com.Assignment.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Assignment.Dto.AuthenticationRequest;
import com.Assignment.Dto.AuthenticationResponse;
import com.Assignment.Dto.RegisterRequest;
import com.Assignment.Entity.User;
import com.Assignment.Exception.ExpenseErrorCode;
import com.Assignment.Exception.ExpenseException;
import com.Assignment.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.Assignment.Security.JwtService;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public ResponseEntity<?> register(RegisterRequest request) {
    	if (userRepository.existsByEmail(request.getEmail())) {
            throw new ExpenseException(ExpenseErrorCode.EMAIL_EXISTS);
        }
        if (request.getPassword().length() < 8) {
            throw new ExpenseException(ExpenseErrorCode.WEAK_PASSWORD);
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(true);
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );
            
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ExpenseException(ExpenseErrorCode.USER_NOT_FOUND));
                    
                if (!user.isActive()) {
                    throw new ExpenseException(ExpenseErrorCode.ACCOUNT_DISABLED);
                }
            
            return new AuthenticationResponse(jwtService.generateToken(user));
            
        } catch (BadCredentialsException e) {
        	throw new ExpenseException(ExpenseErrorCode.INVALID_CREDENTIALS);
        }
    }
}