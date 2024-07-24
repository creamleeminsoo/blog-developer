package me.leeminsoo.blogdeveloper.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import me.leeminsoo.blogdeveloper.config.jwt.TokenProvider;
import me.leeminsoo.blogdeveloper.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import me.leeminsoo.blogdeveloper.domain.User;
import me.leeminsoo.blogdeveloper.dto.user.AddUserRequest;
import me.leeminsoo.blogdeveloper.dto.user.AuthResponse;
import me.leeminsoo.blogdeveloper.service.RefreshTokenService;
import me.leeminsoo.blogdeveloper.service.UserService;
import me.leeminsoo.blogdeveloper.util.CookieUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


import java.time.Duration;

@RequiredArgsConstructor
@Controller
public class UserApiController {
    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository;

    @PostMapping("/api/signup")
    public ResponseEntity<String> signup(@RequestBody AddUserRequest request) {
        userService.save(request);
        return ResponseEntity.ok("회원가입에 성공하였습니다");
    }

    @PostMapping("/api/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AddUserRequest request) {
        User user = userService.findByEmail(request.getEmail());
        if (user != null && passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            String accessToken = tokenProvider.generateToken(user, Duration.ofHours(2));
            String refreshToken = tokenProvider.generateToken(user,Duration.ofHours(14));
            refreshTokenService.refreshTokenSave(user.getId(),refreshToken);


            return ResponseEntity.ok(new AuthResponse(accessToken,refreshToken));
        }
        return ResponseEntity.status(401).body(new AuthResponse("인증정보가 올바르지 않습니다"));
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request,HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request,response
        ,SecurityContextHolder.getContext().getAuthentication());

        CookieUtil.deleteCookie(request, response, "access_token");
        CookieUtil.deleteCookie(request, response, "refresh_token");

        response.setStatus(HttpServletResponse.SC_OK);
    }
}
