    package com.example.testauthor.config;

    import com.example.testauthor.JwtFilter.JwtTokenFilter;
    import com.example.testauthor.Services.UserService;
    import com.example.testauthor.repositories.UserRepo;
    import lombok.RequiredArgsConstructor;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.http.HttpMethod;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.AuthenticationProvider;
    import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
    import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
    import org.springframework.security.core.userdetails.UserDetailsService;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.web.SecurityFilterChain;


    @Configuration
    @RequiredArgsConstructor
    public class SecurityConfig   {

        private final UserRepo userRepo;

        @Bean
        public UserDetailsService userDetailsService(){
            return username -> userRepo.findByUsername(username)
                    .orElseThrow()
                    ;
        }

        @Bean
        public AuthenticationProvider authenticationProvider(){
            DaoAuthenticationProvider authenticationProvider= new DaoAuthenticationProvider();
            authenticationProvider.setUserDetailsService(userDetailsService());
            authenticationProvider.setPasswordEncoder(passwordEncoder());
            return authenticationProvider;
        }
        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
            return configuration.getAuthenticationManager();
        }
       @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }





    }

