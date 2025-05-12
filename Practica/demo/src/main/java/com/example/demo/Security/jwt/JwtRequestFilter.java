package com.example.demo.Security.jwt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 1. Primero intenta por Authorization header
            String token = getJwtFromRequest(request);

// 2. Si no hay token en header, intenta por cookie
            if (!StringUtils.hasText(token)) {
                token = getJwtFromCookie(request);
            }


            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {

                String username = jwtTokenProvider.getUsername(token);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            LOG.error("Exception processing JWT Token",ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtToken(HttpServletRequest request, boolean fromCookie) {

        if (fromCookie) {
            return getJwtFromCookie(request);
        } else {
            return getJwtFromRequest(request);
        }
    }

    private String getJwtFromRequest(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {

            String accessToken = bearerToken.substring(7);
            if (accessToken == null) {
                return null;
            }

            return SecurityCipher.decrypt(accessToken);
        }
        return null;
    }

    private String getJwtFromCookie(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return "";
        }

        for (Cookie cookie : cookies) {
            if (JwtCookieManager.ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                String accessToken = cookie.getValue();
                if (accessToken == null) {
                    return null;
                }

                return SecurityCipher.decrypt(accessToken);
            }
        }
        return null;
    }
}

