package com.teamproject.account.member.Login;

import com.teamproject.account.member.Member;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Arrays;
import java.util.Map;

public class CustomUserDetails extends User implements OAuth2User {
    private final Member member;
    private Map<String, Object> attributes;

    public CustomUserDetails(Member member, Map<String, Object> attributes) {
        super(member.getUsername(), member.getPassword(), Arrays.asList(new SimpleGrantedAuthority("일반유저")));
        this.member = member;
        this.attributes = attributes;
    }
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return this.getUsername();
    }

    public Long getMemberNo() {
        return member.getMemberNo(); // memberNo를 반환하는 메서드 추가
    }

    public String getMemberName() {
        return member.getMemberName(); // memberName을 반환하는 메서드 추가
    }
}
