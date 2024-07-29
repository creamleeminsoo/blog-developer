package me.leeminsoo.blogdeveloper.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.util.SerializationUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CookieUtil {

    private static final String COOKIE_NAME = "article_view";

    public static void setViewCookie(HttpServletRequest request, HttpServletResponse response, Long articleId) {
        Set<Long> viewedArticleIds = getCurrentViewedArticles(request);

        viewedArticleIds.add(articleId);

        String updatedViewArticles = viewedArticleIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        String encodedValue = Base64.getUrlEncoder().encodeToString(updatedViewArticles.getBytes());
        long todayEndSecond = LocalDate.now().atTime(LocalTime.MAX).toEpochSecond(ZoneOffset.UTC);
        long currentSecond = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        addCookie(response, COOKIE_NAME, encodedValue, (int)(todayEndSecond-currentSecond));
    }

    private static Set<Long> getCurrentViewedArticles(HttpServletRequest request) {
        Set<Long> viewedArticleIds = new HashSet<>();
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return viewedArticleIds;
        }
        for (Cookie cookie : cookies) {
            if (COOKIE_NAME.equals(cookie.getName())) {
                String cookieValue = cookie.getValue();
                if (cookieValue != null && !cookieValue.isEmpty()) {
                    String decodedValue = new String(Base64.getUrlDecoder().decode(cookieValue));
                    viewedArticleIds.addAll(
                            Arrays.stream(decodedValue.split(","))
                                    .map(Long::parseLong)
                                    .collect(Collectors.toSet())
                    );
                }
                break;
            }
        }
        return viewedArticleIds;
    }

    public static boolean checkIfViewed(HttpServletRequest request, Long articleId) {
        Set<Long> viewedArticleIds = getCurrentViewedArticles(request);
        return viewedArticleIds.contains(articleId);
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie); //HTTP 응답에 쿠키 추가
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response
            , String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0); //쿠키 실제로 삭제 못하므로 만료기간을 0으로 해서 생성동시에 만료
                response.addCookie(cookie);
            }
        }
    }

    //쿠키 직렬화 메서드 쿠키(객체) -> String 으로 변경해서 쿠키 value에 들어갈수있게함
    public static String serialize(Object obj) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(obj));
    }

    // 쿠키 역직렬화 메서드 String value -> 쿠키(객체)
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(
                SerializationUtils.deserialize(
                        Base64.getUrlDecoder().decode(cookie.getValue())
                )
        );
    }
}