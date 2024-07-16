package me.leeminsoo.blogdeveloper.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

public class CookieUtil {

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge){
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie); //HTTP 응답에 쿠키 추가
    }
    public static void deleteCookie(HttpServletRequest request,HttpServletResponse response
    , String name){
        Cookie[] cookies = request.getCookies();
        if(cookies == null){
            return;
        }
        for(Cookie cookie : cookies){
            if(name.equals(cookie.getName())){
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0); //쿠키 실제로 삭제 못하므로 만료기간을 0으로 해서 생성동시에 만료
                response.addCookie(cookie);
            }
        }
    }
    //쿠키 직렬화 메서드 쿠키(객체) -> String 으로 변경해서 쿠키 value에 들어갈수있게함
    public static String serialize(Object obj){
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(obj));
    }
    // 쿠키 역직렬화 메서드 String value -> 쿠키(객체)
    public static <T> T deserialize(Cookie cookie, Class<T> cls){
        return cls.cast(
                SerializationUtils.deserialize(
                        Base64.getUrlDecoder().decode(cookie.getValue())
                )
        );
    }
}