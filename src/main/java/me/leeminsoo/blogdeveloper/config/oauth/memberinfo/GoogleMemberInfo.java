package me.leeminsoo.blogdeveloper.config.oauth.memberinfo;

import lombok.AllArgsConstructor;

import java.util.Map;
@AllArgsConstructor
public class GoogleMemberInfo implements OAuth2MemberInfo{
    private Map<String, Object> attributes;
    @Override
    public String getProviderId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
}
