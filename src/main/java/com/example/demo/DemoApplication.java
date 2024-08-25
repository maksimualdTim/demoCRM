package com.example.demo;

import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.model.Account;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.service.AccountService;
import com.example.demo.service.UserServiceImpl;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

	@Bean
	CommandLineRunner run (UserServiceImpl userService, AccountService accountService) {
		return args -> {

        Account account = accountService.createAccount("firstAccount");

        
        userService.saveRole(new Role(null, "USER"));
        userService.saveRole(new Role(null, "MANAGER"));
        userService.saveRole(new Role(null, "ADMIN"));
        userService.saveRole(new Role(null, "SUPER_ADMIN"));

        userService.saveUser(new User(null, "John Travolta", "john", "1234", new ArrayList<>(), account));
        userService.saveUser(new User(null, "Will Smith", "will", "1234", new ArrayList<>(), account));
        userService.saveUser(new User(null, "Jim Carrey", "jim", "1234", new ArrayList<>(), account));
        userService.saveUser(new User(null, "Arnold Schwarzenegger", "arnold", "1234", new ArrayList<>(), account));

        userService.addRoleToUser("john", "USER");
        userService.addRoleToUser("john", "MANAGER");
        userService.addRoleToUser("will", "MANAGER");
        userService.addRoleToUser("jim", "ADMIN");
        userService.addRoleToUser("arnold", "SUPER_ADMIN");
        userService.addRoleToUser("arnold", "ADMIN");
        userService.addRoleToUser("arnold", "USER");
		};
	}

}
