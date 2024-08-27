package com.teamproject.account.member.Login;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.HashMap;
import java.util.Map;

public class MemberTypeCheck {

    public Map<String, Object> check(Authentication auth) {
        return printUserInfo(auth);
    }

    private Map<String, Object> printUserInfo(Authentication auth) {
        // Authentication 객체에서 principal 추출
        Object principal = auth.getPrincipal();
        // 사용자 타입에 따라 정보 출력
        if (principal instanceof UserDetails) {
            return printUserDetails((UserDetails) principal);
        } else if (principal instanceof OAuth2User) {
            return printOAuth2UserDetails((OAuth2User) principal);
        }else {
            // 기본적으로 알 수 없는 사용자 타입을 반환
            Map<String, Object> unknownUser = new HashMap<>();
            unknownUser.put("error", "Unknown user type: " + principal.getClass().getName());
            return unknownUser;
        }
    }
    private Map<String, Object> printUserDetails(UserDetails userDetails) {
        Map<String, Object> userInfo = new HashMap<>();
        // 일반 로그인 사용자 정보 처리
        userInfo.put("username", userDetails.getUsername());
        userInfo.put("authorities", userDetails.getAuthorities());

        if (userDetails instanceof CustomUserDetails) {
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            userInfo.put("memberNo", customUserDetails.getMemberNo());
        }
        return userInfo;
    }

    private Map<String, Object> printOAuth2UserDetails(OAuth2User oAuth2User) {
        Map<String, Object> userInfo = new HashMap<>();
        // OAuth2 로그인 사용자 정보 처리
        userInfo.put("oauth2Username", oAuth2User.getAttribute("name"));
        userInfo.put("email", oAuth2User.getAttribute("email"));

        if (oAuth2User instanceof CustomUserDetails) {
            CustomUserDetails customUserDetails = (CustomUserDetails) oAuth2User;
            userInfo.put("memberNo", customUserDetails.getMemberNo());
        }
        return userInfo;
    }
}

