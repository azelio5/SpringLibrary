package com.sbbc.sl.librarymanagement.service;

import com.sbbc.sl.librarymanagement.dto.LoginRequestDTO;
import com.sbbc.sl.librarymanagement.dto.LoginResponseDTO;
import com.sbbc.sl.librarymanagement.dto.RegisterRequestDTO;
import com.sbbc.sl.librarymanagement.entity.User;
import com.sbbc.sl.librarymanagement.jwt.JwtService;
import com.sbbc.sl.librarymanagement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public User registerNormalUser(RegisterRequestDTO registerRequestDTO) {
        logger.info("Регистрация обычного пользователя: {}", registerRequestDTO.getUsername());

        if (userRepository.findUserByUsername(registerRequestDTO.getUsername()).isPresent()) {
            logger.warn("Пользователь уже существует: {}", registerRequestDTO.getUsername());
            throw new RuntimeException("User already exists with username: " + registerRequestDTO.getUsername());
        }

        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");

        User user = new User();
        user.setUsername(registerRequestDTO.getUsername());
        user.setEmail(registerRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        user.setRoles(roles);

        logger.info("Пользователь успешно зарегистрирован: {}", user.getUsername());
        return userRepository.save(user);
    }

    public User registerAdminUser(RegisterRequestDTO registerRequestDTO) {
        logger.info("Регистрация администратора: {}", registerRequestDTO.getUsername());

        if (userRepository.findUserByUsername(registerRequestDTO.getUsername()).isPresent()) {
            logger.warn("Админ уже существует: {}", registerRequestDTO.getUsername());
            throw new RuntimeException("User already exists with username: " + registerRequestDTO.getUsername());
        }

        Set<String> roles = new HashSet<>();
        roles.add("ROLE_ADMIN");
        roles.add("ROLE_USER");

        User user = new User();
        user.setUsername(registerRequestDTO.getUsername());
        user.setEmail(registerRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        user.setRoles(roles);

        logger.info("Администратор успешно зарегистрирован: {}", user.getUsername());
        return userRepository.save(user);
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        logger.info("Попытка входа: {}", loginRequestDTO.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword())
        );

        User user = userRepository.findUserByUsername(loginRequestDTO.getUsername())
                .orElseThrow(() -> {
                    logger.error("Пользователь не найден: {}", loginRequestDTO.getUsername());
                    return new RuntimeException("User not found with username: " + loginRequestDTO.getUsername());
                });

        String token = jwtService.generateToken(user);

        logger.info("Пользователь вошёл: {}", user.getUsername());

        return LoginResponseDTO.builder()
                .token(token)
                .username(user.getUsername())
                .roles(user.getRoles())
                .build();
    }
}
