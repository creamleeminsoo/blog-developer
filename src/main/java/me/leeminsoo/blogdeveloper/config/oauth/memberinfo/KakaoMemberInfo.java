package me.leeminsoo.blogdeveloper.config.oauth.memberinfo;

import java.util.Map;

public class KakaoMemberInfo implements OAuth2MemberInfo {
    public KakaoMemberInfo(Map<String, Object> attributes){
        this.attributes = attributes;
    }
    private Map<String, Object> attributes;

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getName() {
        return (String) attributes.get("nickname");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
}
