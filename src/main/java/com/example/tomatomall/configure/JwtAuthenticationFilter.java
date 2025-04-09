package com.example.tomatomall.configure;


import com.example.tomatomall.po.Account;
import com.example.tomatomall.utils.AuthUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthUtil tokenUtil;

    public JwtAuthenticationFilter(AuthUtil tokenUtil) {
        this.tokenUtil = tokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = getTokenFromRequest(request);

        if (token != null && tokenUtil.verifyToken(token)) {
            Account account = tokenUtil.getAccount(token);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(account, null, getAuthorities(account));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            request.getSession().setAttribute("currentUser", account);
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return request.getHeader("token"); // 兼容旧版token头
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Account account) {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }
}


