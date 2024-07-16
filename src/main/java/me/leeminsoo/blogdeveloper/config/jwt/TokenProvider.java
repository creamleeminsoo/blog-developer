package me.leeminsoo.blogdeveloper.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;


import me.leeminsoo.blogdeveloper.domain.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class TokenProvider {
    private final JwtProperties jwtProperties;

    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }

    //jwt 토근 생성메서드
    private String makeToken(Date expiry, User user) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) //헤더타입은 JWT
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setSubject(user.getEmail()) //토큰제목:user 이메일
                .claim("id",user.getId()) //클레임에 유저id저장
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey()) //서명: HS256함수로 해싱, 비밀키 설정
                .compact();
    }
    //JWT 토큰 유효성 검증 메서드
    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())// 비밀키로 복호화
                    .parseClaimsJws(token);

            return true;
        } catch (Exception e) { //복호화 과정에서 에러나면 false리턴
            return false;
        }
    }
    //토큰으로 인증정보 가져오는 메서드
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token); //복호화된 토큰에서 클레임가져옴
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails. //spring security에 있는 User 객체가져옴
                User(claims.getSubject(), "",authorities),token, authorities);
    }
    //토근으로 유저 id가져오는 메서드
    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }
    //클레임을 복호화해주는 메서드
    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }
}
