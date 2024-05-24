    package com.example.testauthor.config;

    import com.example.testauthor.JwtFilter.JwtTokenFilter;
    import lombok.RequiredArgsConstructor;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.http.HttpMethod;
    import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
    import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
    import org.springframework.stereotype.Component;
    import org.springframework.web.cors.CorsConfiguration;
    import org.springframework.web.cors.CorsConfigurationSource;
    import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
    import org.springframework.web.servlet.config.annotation.CorsRegistry;
    import org.springframework.web.servlet.config.annotation.EnableWebMvc;
    import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

    @Configuration
    @EnableMethodSecurity
    @RequiredArgsConstructor
    public class WebsecurityConfig {

        private final JwtTokenFilter jwtTokenFilter;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(authorize -> authorize
                            .requestMatchers(HttpMethod.POST, "/login/go").permitAll()
                            .requestMatchers(HttpMethod.POST, "/register").permitAll()
                            .requestMatchers(HttpMethod.GET, "/registerr").permitAll()
                            .requestMatchers(HttpMethod.GET, "/product").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/product/delete/**").hasRole("ADMIN")
                            .requestMatchers(HttpMethod.GET, "/login").permitAll()
                            .anyRequest().authenticated() // Yêu cầu xác thực cho tất cả các yêu cầu khác
                    )
                    .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
            return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.addAllowedOrigin("*"); // Cho phép tất cả các nguồn gốc
            configuration.addAllowedMethod("*"); // Cho phép tất cả các phương thức HTTP
            configuration.addAllowedHeader("*"); // Cho phép tất cả các tiêu đề
            configuration.setAllowCredentials(true); // Cho phép gửi cookie
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            return source;
        }
    }
