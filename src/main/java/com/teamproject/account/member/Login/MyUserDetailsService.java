package com.teamproject.account.member.Login;
import com.teamproject.account.member.Member;
import com.teamproject.account.member.MemberRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;

@RequiredArgsConstructor
@Service
public class MyUserDetailsService implements UserDetailsService,OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final MemberRepository memberRepository;
    private final HttpServletResponse response;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //사용자가 제출한 아이디가 String username 으로 알아서들어감
 /*     DB에서 username을 가진 유저를 찾아와서
        return new User(유저아이디, 비번, 권한) 해주세요*/
        Optional<Member> member = memberRepository.findByUsername(username);
        if(!member.isPresent()){
            throw new UsernameNotFoundException("아이디를 찾을수없음");
        }
        Member member1 = member.get();
        //권한을 집어넣을떄는 List타입이 GrantedAuthority 가 들어가야한다~~!
        List<GrantedAuthority> authority = new ArrayList<>();
        //권한을 추가할때는 new SimpleGrantedAuthority() 함수를 사용해야한다..
        authority.add(new SimpleGrantedAuthority("일반유저"));  
        return new CustomUserDetails(member1, null);
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();

        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId ="";
        String email = "";
        String name = "";

        if(provider.equals("google")){
             providerId = oAuth2User.getAttribute("sub"); // Google의 유저 ID
             email = oAuth2User.getAttribute("email");
             name = oAuth2User.getAttribute("name");
        }else{
             providerId = oAuth2User.getAttribute("id"); // 카카오톡의 경우 "id"로 유저 ID를 가져옵니다.
             email = oAuth2User.getAttribute("kakao_account.email");
             name = oAuth2User.getAttribute("properties.nickname");
        }

        Optional<Member> memberOptional = memberRepository.findByEmailOrUsername(email,email);
        Member member;

        if (memberOptional.isPresent()) {
            if(memberOptional.get().getProvider() == null){
                try {
                    response.setContentType("text/html; charset=UTF-8");
                    response.getWriter().write("<script>alert('이미 가입된 계정이 있습니다. 로그인하세요.'); location.href='/login';</script>");
                    response.getWriter().flush();
                } catch (IOException e) {
                    throw new OAuth2AuthenticationException("리다이렉트 중 에러 발생");
                }
                return null;
            }else{
                member = memberOptional.get();
            }
        } else {
            // Google 계정을 통해 신규 사용자 등록
            member = new Member();
            member.setUsername(email);
            member.setEmail(email);
            member.setMemberName(name);
            member.setProviderId(providerId);
            member.setProvider(provider);
            member.setPassword(generateRandomPassword());
            memberRepository.save(member);
        }
        // DefaultOAuth2User를 통해 OAuth2User와 UserDetails를 동시에 구현
        return new CustomUserDetails(member, oAuth2User.getAttributes());
    }

    private String generateRandomPassword() {
        int length = 12 + new Random().nextInt(3); // 12~14자리 문자열 생성
        StringBuilder sb = new StringBuilder(length);
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }

        return sb.toString();
    }


}
