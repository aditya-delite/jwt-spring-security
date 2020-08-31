package com.aditya.config;

import com.aditya.model.User;
import com.aditya.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user not found with username:" + username));
        UserDetailsImpl userDetailsImpl = new UserDetailsImpl();
        userDetailsImpl.setEmail(user.getEmail());
        userDetailsImpl.setPassword(user.getPassword());
        userDetailsImpl.setUsername(user.getUsername());
        userDetailsImpl.mapAuthorities(user);
        return userDetailsImpl;
    }
}
