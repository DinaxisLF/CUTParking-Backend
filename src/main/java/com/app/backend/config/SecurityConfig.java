package com.app.backend.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/public", "/login", "/oauth2/**").permitAll()
                        .requestMatchers("/api/car/**", "/user/**").authenticated() // Protected paths
                )
                .logout(logout -> logout
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                        .clearAuthentication(true)      // Clear authentication
                        .invalidateHttpSession(true)    // Invalidate the session
                        .permitAll()
                )
                .oauth2Login(oauth -> oauth
                        .successHandler(successHandler())
                        .failureHandler((request, response, exception) -> response.sendError(HttpStatus.FORBIDDEN.value()))
                );   // Success handler for OAuth2 login

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new SimpleUrlAuthenticationSuccessHandler("/user/info");   // Redirect to user info after OAuth2 login
    }

}
