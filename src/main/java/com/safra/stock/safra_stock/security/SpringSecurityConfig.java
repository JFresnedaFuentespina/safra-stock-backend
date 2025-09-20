package com.safra.stock.safra_stock.security;

import java.util.Arrays;

import org.springframework.web.filter.CorsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
// import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.safra.stock.safra_stock.security.filter.JwtAuthenticationFilter;
import com.safra.stock.safra_stock.security.filter.JwtValidationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig {

    // @Autowired
    // private AuthenticationConfiguration authenticationConfiguration;

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.authenticationProvider(authenticationProvider());
        return authBuilder.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtAuthFilter = new JwtAuthenticationFilter(authenticationManager(http));
        jwtAuthFilter.setFilterProcessesUrl("/safra-stock/login");
        return http.authorizeHttpRequests((authz) -> authz
                .requestMatchers(HttpMethod.GET, "/favicon.ico").permitAll()
                .requestMatchers(HttpMethod.GET, "/safra-stock/products").permitAll()
                .requestMatchers(HttpMethod.GET, "/safra-stock/products/{id}").permitAll()
                .requestMatchers(HttpMethod.PUT, "/safra-stock/products/{id}").permitAll()
                .requestMatchers(HttpMethod.PUT, "/safra-stock/products/disable/*").permitAll()
                .requestMatchers(HttpMethod.PUT, "/safra-stock/products/enable/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/safra-stock/users").permitAll()
                .requestMatchers(HttpMethod.GET, "/safra-stock/users/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/safra-stock/users/**").permitAll()
                .requestMatchers(HttpMethod.PUT, "/safra-stock/users/*").permitAll()
                .requestMatchers(HttpMethod.PUT, "/safra-stock/users/disable/*").permitAll()
                .requestMatchers(HttpMethod.PUT, "/safra-stock/users/enable/*").permitAll()
                .requestMatchers(HttpMethod.GET, "/safra-stock/users/get-users-in-local").permitAll()
                .requestMatchers(HttpMethod.POST, "/safra-stock/users/register").permitAll()
                .requestMatchers(HttpMethod.GET, "/safra-stock/orders").permitAll()
                .requestMatchers(HttpMethod.POST, "/safra-stock/orders").permitAll()
                .requestMatchers(HttpMethod.PUT, "/safra-stock/orders").permitAll()
                .requestMatchers(HttpMethod.PUT, "/safra-stock/orders/disable/*").permitAll()
                .requestMatchers(HttpMethod.PUT, "/safra-stock/orders/enable/*").permitAll()
                .requestMatchers(HttpMethod.POST, "/safra-stock/orders/send-order-notification").permitAll()
                .requestMatchers(HttpMethod.GET, "/safra-stock/locales").permitAll()
                .requestMatchers(HttpMethod.GET, "/safra-stock/locales/*").permitAll()
                .requestMatchers(HttpMethod.POST, "/safra-stock/locales").permitAll()
                .requestMatchers(HttpMethod.PUT, "/safra-stock/locales").permitAll()
                .requestMatchers(HttpMethod.PUT, "/safra-stock/locales/*").permitAll()
                .requestMatchers(HttpMethod.POST, "/safra-stock/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/safra-stock/stock").permitAll()
                .requestMatchers(HttpMethod.POST, "/safra-stock/stock/batch").permitAll()
                .requestMatchers(HttpMethod.GET, "/safra-stock/stock").permitAll()
                .requestMatchers(HttpMethod.GET, "/safra-stock/discarded").permitAll()
                .requestMatchers(HttpMethod.POST, "/safra-stock/discarded").permitAll()
                .anyRequest().authenticated())
                .addFilter(jwtAuthFilter)
                .addFilter(new JwtValidationFilter(authenticationManager(http)))
                .csrf(config -> config.disable())
                .cors(cors -> cors.configurationSource(configurationSource()))
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList("http://145.1.207.226:4200"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        FilterRegistrationBean<CorsFilter> corsBean = new FilterRegistrationBean<>(
                new CorsFilter(configurationSource()));
        corsBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return corsBean;
    }

}
