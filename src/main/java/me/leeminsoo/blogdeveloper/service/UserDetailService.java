package me.leeminsoo.blogdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.leeminsoo.blogdeveloper.domain.User;
import me.leeminsoo.blogdeveloper.repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public User loadUserByUsername(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException((email)));
    }
}
