package com.example.demo.controller;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.UserServiceImpl;

import lombok.Data;

import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
	private JwtEncoder encoder;

	@Autowired
	private JwtDecoder decoder;

	@Autowired
	private UserServiceImpl userServiceImpl;

	@PostMapping("/register")
	public String register(@RequestBody String entity) {
		
		return entity;
	}
	

	@PostMapping("/login")
	public TokenResponse login(Authentication authentication) {
		Instant now = Instant.now();
		long accessTokenExpiry = 3600L; // 1 hour
        long refreshTokenExpiry = 2592000L; // 30 days
       // create Access Token
	   String accessToken = generateToken(authentication, now, accessTokenExpiry);

	   // create Refresh Token
	   String refreshToken = generateToken(authentication, now, refreshTokenExpiry);

		// @formatter:on
		return new TokenResponse(accessToken, refreshToken);
	}

    @PostMapping("/refresh")
    public TokenResponse refresh(@RequestBody String refreshToken) {

        Jwt decodedJwt = decoder.decode(refreshToken);
        String username = decodedJwt.getSubject();

        // Check the refresh token's validity and fetch user details
		UserDetails userDetails = userServiceImpl.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, userDetails);

        Instant now = Instant.now();
        long accessTokenExpiry = 3600L; // 1 hour

        String accessToken = generateToken(authentication, now, accessTokenExpiry);
        // Generate new refresh token if needed or use the same one

        return new TokenResponse(accessToken, refreshToken); // Optionally, generate a new refresh token
    }

	private String generateToken(Authentication authentication, Instant now, long expiry) {
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .subject(authentication.getName())
                .claim("roles", roles)
                .build();

        return this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}

@Data
class TokenResponse {
    private String accessToken;
    private String refreshToken;

	public TokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}