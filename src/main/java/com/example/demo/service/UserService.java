package com.example.demo.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.exceptions.UserExistsException;
import com.example.demo.model.Account;
import com.example.demo.model.Pipeline;
import com.example.demo.model.Status;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.PipelineRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.response.UserResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountRepository accountRepository;
    private final PipelineService pipelineService;
    private final PipelineRepository pipelineRepository;
    private final StatusService statusService;
    

    private final EmailService emailService;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
    private static final int PASSWORD_LENGTH = 12;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);

        // Check if the user is present and handle accordingly
        User user = optionalUser.orElseThrow(() -> {
            log.error("User not found in the database: {}", username);
            return new UsernameNotFoundException("User not found in the database");
        });

        log.info("User found in the database: {}", username);

        Collection <SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    public User saveUser (User user) {
        log.info("Saving new user {} to the database", user.getName());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Role saveRole(Role role) {
        log.info("Saving new role {} to the database", role.getName());
        return roleRepository.save(role);
    }

    public void addRoleToUser(String username, String roleName) {
        log.info("Addin role {} to user {}", roleName, username);
        Optional<User> optionalUser = userRepository.findByUsername(username);
        User user = optionalUser.orElseThrow(() -> {
            log.error("User not found in the database: {}", username);
            return new UsernameNotFoundException("User not found in the database");
        });
        Role role = roleRepository.findByName(roleName);
        user.getRoles().add(role);
    }

    public User getUser(String username) {
        log.info("Fetcnig user {} ", username);
        Optional<User> optionalUser = userRepository.findByUsername(username);

        // Check if the user is present and handle accordingly
        User user = optionalUser.orElseThrow(() -> {
            log.error("User not found in the database: {}", username);
            return new UsernameNotFoundException("User not found in the database");
        });
        return user;
    }

    public Page<User> getUsers(Pageable pageable) {
        log.info("Fetcnig all user");
        return userRepository.findAll(pageable);
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optionalUser = userRepository.findByUsername(auth.getName());

        // Check if the user is present and handle accordingly
        User user = optionalUser.orElseThrow(() -> {
            log.error("User not found in the database: {}", auth.getName());
            return new UsernameNotFoundException("User not found in the database");
        });
        return user;
    }

    public User toUser(UserResponse userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setUsername(userDto.getUsername());
        return user;
    }

    public UserResponse toResponseDto (User user) {
        UserResponse userDto = new UserResponse();
        userDto.setName(user.getName());
        userDto.setUsername(user.getUsername());
        userDto.setRoles(user.getRoles());
        return userDto;
    }

    public List<UserResponse> getUsersResponse(Pageable pageable) {
        List<UserResponse> list = new ArrayList<>();
        Page<User> users = getUsers(pageable);
    
        for (User user : users) {
            list.add(toResponseDto(user));
        }
    
        return list;
    }

    public void registerNewUser(String email, String name) throws UserExistsException{
        Optional<User> existingUser = userRepository.findByUsername(email);
        if (existingUser.isPresent()) {
            throw new UserExistsException("User with mail "+email+" already exists");
        }

        String password = generateRandomPassword();

        Account account = new Account();
        account.setAccountName(email);

        account = accountRepository.save(account);

        Pipeline pipeline = pipelineService.createDefaultPipeline(account);
        List<Status> statuses = statusService.createDefaultStatuses(pipeline);
        pipeline.setStatuses(statuses);
        pipelineRepository.save(pipeline);

        User user = new User();
        user.setUsername(email);
        user.setName(name);
        user.setPassword(passwordEncoder.encode(password));
        user.setAccount(account);

        
        userRepository.save(user);
        
        emailService.sendRegistrationEmail(email, password);
    }

    public String generateRandomPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }
}
