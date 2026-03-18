package org.example.ticketReservation.controller;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.example.ticketReservation.domain.User;
import org.example.ticketReservation.dto.LoginRequest;
import org.example.ticketReservation.dto.UserDto;
import org.example.ticketReservation.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request){
        return userService.login(request.getEmail(), request.getPassword())
                .map(user -> ResponseEntity.ok("Login Successful! Token generation starting "+ user.getName()))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Email or Password"));
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserDto userDto){
        User user = User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .build();
        return ResponseEntity.ok(userService.registerUser(user));
    }

}
