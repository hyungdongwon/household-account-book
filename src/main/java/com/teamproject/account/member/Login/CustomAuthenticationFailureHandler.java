package com.teamproject.account.member.Login;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        // 로그인 시도한 id 값 가져오기
        String username = request.getParameter("username");
        // 가져온 id 값 활용하기
        System.out.println("Login failed for user: " + username);
        request.getSession().setAttribute("loginFailUsername", username);
        // 실패 후 리다이렉트 처리
        response.sendRedirect("/loginFail");
    }
}
