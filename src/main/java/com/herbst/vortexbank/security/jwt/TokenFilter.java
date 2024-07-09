package com.herbst.vortexbank.security.jwt;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

@AllArgsConstructor
@NoArgsConstructor
public class TokenFilter extends GenericFilter {

    @Autowired
    private TokenProvider tokenProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String token = tokenProvider.resolveToken((HttpServletRequest) servletRequest);
        if(token != null && tokenProvider.validateToken(token)){
            Authentication auth = tokenProvider.getAuthentication(token);
            if(auth != null){
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
