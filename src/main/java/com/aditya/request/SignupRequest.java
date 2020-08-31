package com.aditya.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class SignupRequest {
    private String username;
    private String password;
    private String email;
    private Set<String> roles;
}
