package com.sbbc.sl.librarymanagement.controller;

import com.sbbc.sl.librarymanagement.dto.LoginRequestDTO;
import com.sbbc.sl.librarymanagement.dto.LoginResponseDTO;
import com.sbbc.sl.librarymanagement.dto.RegisterRequestDTO;
import com.sbbc.sl.librarymanagement.entity.User;
import com.sbbc.sl.librarymanagement.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/registernormaluser")
    public ResponseEntity<User> addNormalUser(@RequestBody RegisterRequestDTO registerRequestDTO) {
          return ResponseEntity.ok(authenticationService.registerNormalUser(registerRequestDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(authenticationService.login(loginRequestDTO));
    }

}
