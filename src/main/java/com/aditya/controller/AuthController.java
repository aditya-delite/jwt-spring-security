package com.aditya.controller;

import com.aditya.config.JwtUtils;
import com.aditya.config.UserDetailsImpl;
import com.aditya.model.Role;
import com.aditya.model.RoleEnum;
import com.aditya.model.User;
import com.aditya.repository.RoleRepository;
import com.aditya.repository.UserRepository;
import com.aditya.request.LoginRequest;
import com.aditya.request.SignupRequest;
import com.aditya.response.JwtResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("email already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        Set<String> roles = request.getRoles();
        Set<Role> roleSet = new HashSet<>();
        if (roles == null) {
            Role role = roleRepository.findByRoleName(RoleEnum.ROLE_USER).orElseThrow(() -> new RuntimeException("Role not found"));
            roleSet.add(role);
        }
        roles.forEach(role -> {
            switch (role) {
                case "admin":
                    Role adminRole = roleRepository.findByRoleName(RoleEnum.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Role not found"));
                    roleSet.add(adminRole);
                    break;
                case "moderator":
                    Role moderatorRole = roleRepository.findByRoleName(RoleEnum.ROLE_MODERATOR).orElseThrow(() -> new RuntimeException("Role not found"));
                    roleSet.add(moderatorRole);
                    break;
                default:
                    Role defaultRole = roleRepository.findByRoleName(RoleEnum.ROLE_USER).orElseThrow(() -> new RuntimeException("Role not found"));
                    roleSet.add(defaultRole);
                    break;
            }
        });
        user.setRoles(roleSet);
        User save = userRepository.save(user);
        return ResponseEntity.ok("user successfully created");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
//                userDetails.get(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }
}
