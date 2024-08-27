    package com.teamproject.account;
    import com.teamproject.account.member.Login.CustomAuthenticationFailureHandler;
    import com.teamproject.account.member.Login.CustomAuthenticationSuccessHandler;
    import com.teamproject.account.member.Login.MyUserDetailsService;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.authentication.AuthenticationFailureHandler;
    import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

    @Configuration
    @EnableWebSecurity
    public class SecurityConfig {
        private final MyUserDetailsService myUserDetailsService;

        public SecurityConfig(MyUserDetailsService myUserDetailsService) {
            this.myUserDetailsService = myUserDetailsService;
        }
        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
            return authenticationConfiguration.getAuthenticationManager();
        }
        @Bean
        PasswordEncoder passwordEncoder(){
            return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            //FilterChain : 모든유저의 요청과 서버의 응답사이에 자동으로 실행해주고 싶은코드를 담는곳
            http.csrf((csrf) -> csrf.disable());
            //csrf를 비활성화 하는코드 (테스트할떄는 끄자 귀차늠)
            http.authorizeHttpRequests((authorize) ->
                    authorize.requestMatchers("/","/login","/member/join","/member/email-verify"
                            ,"/member/email-verify2","/member/email-token-verify","/presigned-url","/loginSuccess",
                            "/loginFail","/member/joinProc","/joinProc2/{email}","/logout").permitAll()
                    .anyRequest().authenticated()
                    //특정 페이지 로그인검사 할지말지 결정하는코드 .permitAll()함수는 아무나 접속허용 /**는 모든Url을 의미 즉
                    //위코드는 모든 Url 에서 모든유저는 접속허용한다는 뜻
            );
            http.formLogin((formLogin)
                    -> formLogin.loginPage("/login")
                    .successHandler(customAuthenticationSuccessHandler())
                    .failureHandler(customAuthenticationFailureHandler())
                    //.failureUrl("/loginFail")
            );
            http.logout(logout
                    -> logout
                    .logoutUrl("/logout") //해당 url로 가면 로그아웃됨
                    .logoutSuccessUrl("/") //로그아웃 성공시 페이지
                    .invalidateHttpSession(true) // 세션 무효화
                    .deleteCookies("JSESSIONID") // 쿠키 삭제
            );
            http.oauth2Login(oauth2 -> oauth2
                    .loginPage("/login")
                    .defaultSuccessUrl("/")
                    .userInfoEndpoint(userInfo -> userInfo
                            .userService(myUserDetailsService) // OAuth2UserService 설정
                    )
            );
            // 인증되지 않은 사용자가 보호된 URL에 접근할 경우 로그인 페이지로 리다이렉트
            http.exceptionHandling((exceptions) ->
                    exceptions
                            .authenticationEntryPoint((request, response, authException) ->
                                    response.sendRedirect("/login?error=unauthorized")
                            )
            );
            return http.build();
        }
        @Bean
        public AuthenticationFailureHandler customAuthenticationFailureHandler() {
            return new CustomAuthenticationFailureHandler();
        }

        @Bean
        public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
            return new CustomAuthenticationSuccessHandler();
        }
    }
