package com.example.testauthor.JwtFilter;

import com.example.testauthor.component.JwtTokenUtil;
import com.example.testauthor.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenUtil jwtTokenUtil;


    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            if (isByPassToken(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = null;
            String authenticationHeader = request.getHeader("Authorization");

            if (authenticationHeader != null && authenticationHeader.startsWith("Bearer ")) {
                token = authenticationHeader.substring(7);
            } else {
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if ("token".equals(cookie.getName())) {
                            token = cookie.getValue();
                            break;
                        }
                    }
                }
            }

            if (token == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            String phoneNumber = jwtTokenUtil.extractPhoneNumber(token);

            if (phoneNumber == null || SecurityContextHolder.getContext().getAuthentication() != null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }

            com.example.testauthor.model.User userDetails = (User) userDetailsService.loadUserByUsername(phoneNumber);

            if (userDetails == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
                return;
            }

            System.out.println("User loaded: " + userDetails.getUsername() + " with roles: " + userDetails.getAuthorities());


            // Tạo đối tượng Authentication từ UserDetails
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // Đặt Authentication vào SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            System.out.println("Authentication set in SecurityContextHolder: " + SecurityContextHolder.getContext().getAuthentication());

            // Tiếp tục chuỗi filter
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
    }

    private boolean isByPassToken(@NonNull HttpServletRequest request) {
        final List<Map<String, String>> byPassTokens = new ArrayList<>();
        byPassTokens.add(Map.of("login", "GET"));
        byPassTokens.add(Map.of("login/go", "POST"));
        byPassTokens.add(Map.of("registerr", "GET"));
        byPassTokens.add(Map.of("register", "POST"));

        for (Map<String, String> token : byPassTokens) {
            String path = token.keySet().iterator().next();
            String method = token.get(path);
            if (request.getServletPath().contains(path) && request.getMethod().equals(method)) {
                return true;
            }
        }
        return false;
    }

}
