package me.leeminsoo.blogdeveloper.config.oauth;

import lombok.RequiredArgsConstructor;
import me.leeminsoo.blogdeveloper.config.oauth.memberinfo.GoogleMemberInfo;
import me.leeminsoo.blogdeveloper.config.oauth.memberinfo.KakaoMemberInfo;
import me.leeminsoo.blogdeveloper.config.oauth.memberinfo.NaverMemberInfo;
import me.leeminsoo.blogdeveloper.config.oauth.memberinfo.OAuth2MemberInfo;
import me.leeminsoo.blogdeveloper.domain.User;
import me.leeminsoo.blogdeveloper.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);
        OAuth2MemberInfo memberInfo = null;

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        if (registrationId.equals("google")) {
            memberInfo = new GoogleMemberInfo(user.getAttributes());
        }else if (registrationId.equals("naver")){
            memberInfo = new NaverMemberInfo((Map)user.getAttributes().get("response"));
        }else if (registrationId.equals("kakao")){
            Map<String, Object> kakaoAccount = (Map<String, Object>) user.getAttributes().get("kakao_account");
            memberInfo = new KakaoMemberInfo(kakaoAccount);
        }

        else {
            throw new OAuth2AuthenticationException("Unknown registration id: " + registrationId);
        }

        saveOrUpdate(memberInfo);
        return user;

    }

    private User saveOrUpdate(OAuth2MemberInfo memberInfo) {

        String email = memberInfo.getEmail();
        String name = memberInfo.getName();
        User user = userRepository.findByEmail(email)
                .map(entity -> entity.update(name))
                .orElse(User.builder()
                        .email(email)
                        .nickname(name)
                        .build());

        return userRepository.save(user);
    }
}
