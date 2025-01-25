package com.alphasolutions.eventapi.configuration;
import com.alphasolutions.eventapi.service.CustomUserDetailsService;
import com.alphasolutions.eventapi.filter.WebSocketFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    CustomUserDetailsService userDetailsService;
    CorsConfig corsConfig;
    public SecurityConfig(CorsConfig corsConfig, CustomUserDetailsService userDetailsService) {
        this.corsConfig = corsConfig;
        this.userDetailsService = userDetailsService;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


        http
                .csrf((csrf) -> csrf.disable())
                .cors((Customizer.withDefaults()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests.requestMatchers("/api/auth/**","/login","/api/connection/**","/api/**","/ws/**","api/connection/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(new WebSocketFilter(userDetailsService), UsernamePasswordAuthenticationFilter.class)
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
