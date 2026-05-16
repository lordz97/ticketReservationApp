package org.example.ticketReservation.service;

import lombok.RequiredArgsConstructor;
import org.example.ticketReservation.domain.User;
import org.example.ticketReservation.domain.UserRole;
import org.example.ticketReservation.dto.AuthResponseDto;
import org.example.ticketReservation.dto.LoginRequestDto;
import org.example.ticketReservation.dto.RegisterRequestDto;
import org.example.ticketReservation.exception.UserAlreadyExistsException;
import org.example.ticketReservation.repository.UserRepository;
import org.example.ticketReservation.security.CustomUserDetailsService;
import org.example.ticketReservation.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    public AuthResponseDto register(RegisterRequestDto requestDto) {
        if(userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User already exists");
        }
        User user = User.builder()
                .name(requestDto.getName())
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .build();

        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails);

        return AuthResponseDto.builder().token(token).build();
    }

    public AuthResponseDto authentication(LoginRequestDto loginDto){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getEmail());
        String token = jwtService.generateToken(userDetails);

        return AuthResponseDto.builder()
                .token(token)
                .build();
    }
}
