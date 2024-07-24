package me.leeminsoo.blogdeveloper.dto.user;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String message;

    public AuthResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public AuthResponse(String message) {
        this.message = message;
    }



}
