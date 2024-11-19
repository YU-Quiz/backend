package yuquiz.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import yuquiz.common.jwt.filter.JwtAuthenticationFilter;
import yuquiz.common.jwt.filter.JwtExceptionFilter;
import yuquiz.domain.user.entity.Role;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtExceptionFilter jwtExceptionFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/v1/auth/**", "/swagger-resources/**", "/swagger-ui/**",
            "/v3/api-docs/**", "/webjars/**", "/error", "/api/v1/users/verify-username",
            "/api/v1/users/verify-nickname", "/api/v1/users/email/**", "/ws/**"
    };
    private static final String[] ADMIN_ENDPOINTS = {"/api/v1/admin/**"};

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)

                .cors((cors) -> cors.configurationSource(corsConfigurationSource))

                .formLogin(AbstractHttpConfigurer::disable)

                .httpBasic(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                                .requestMatchers(ADMIN_ENDPOINTS).hasAnyRole(String.valueOf(Role.ADMIN))
                                .anyRequest().authenticated())

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                .addFilterBefore(jwtExceptionFilter, JwtAuthenticationFilter.class);;

        return http.build();
    }
}
