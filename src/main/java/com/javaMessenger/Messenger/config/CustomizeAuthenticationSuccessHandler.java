package com.javaMessenger.Messenger.config;

import com.javaMessenger.Messenger.domain.User;
import com.javaMessenger.Messenger.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Handler of success authentication: starting WebSocket
 *
 * @author dmitry
 */
@Component
public class CustomizeAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private CustomUserDetailsService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        //set our response to OK status
        response.setStatus(HttpServletResponse.SC_OK);

        for (GrantedAuthority auth : authentication.getAuthorities()) {
            if ("ADMIN".equals(auth.getAuthority())) {
                Authentication underAuth = SecurityContextHolder.getContext().getAuthentication();
                User username = userService.findUserByEmail(underAuth.getName());

                //String username = SecurityContextHolder.getContext().getAuthentication().getName();

                //Open WebSocket onLogin
                request.getSession().setAttribute("username", username.getFullname());

                response.sendRedirect("/dashboard?email=" + username.getEmail());
            }
        }
    }

}
