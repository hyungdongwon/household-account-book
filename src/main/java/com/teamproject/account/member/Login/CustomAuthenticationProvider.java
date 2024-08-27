package com.teamproject.account.member.Login;

import com.teamproject.account.member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberService memberService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        // 로그인 실패 횟수 확인
        int loginFailCount = memberService.count(username);
        if (loginFailCount == 5) {
            throw new BadCredentialsException("계정이 잠겼습니다. 관리자에게 문의하세요.");
        }
        // 사용자 정보를 로드
        UserDetails user = userDetailsService.loadUserByUsername(username);
        // 비밀번호 검증
        if (passwordEncoder.matches(password, user.getPassword())) {
            // 인증 성공 시 UsernamePasswordAuthenticationToken 반환
            return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
        } else {
            throw new BadCredentialsException("아이디 또는 비밀번호가 잘못되었습니다.");
        }
    }
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

