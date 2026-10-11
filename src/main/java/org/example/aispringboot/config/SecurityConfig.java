package org.example.aispringboot.config;

import jakarta.annotation.Resource;
import org.example.aispringboot.service.UserService;
import org.example.aispringboot.util.JwtAuthticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.AntPathMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private static final String[] PUBLIC_URLS =
            {"/api/user/login", "/api/user/logout", "/", "/api/user/add",
                    "/api/knowledge/article/page", "/api/knowledge/article/*"};

    @Resource
    private UserService userService;

    public static Boolean isPublicPath(String requestURL){
        for(String publicURL:PUBLIC_URLS){
            if(antPathMatcher.match(publicURL,requestURL)){
                return true;
            }
        }
        return false;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(
                auth->auth.requestMatchers(PUBLIC_URLS).permitAll()
                        .anyRequest().authenticated()
        )
        //添加JWT过滤器
        .addFilterBefore(new JwtAuthticationFilter(userService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
