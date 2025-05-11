package com.example.demo.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    private final RepositoryUserDetailsService userDetailService;

    // Inyección por constructor (mejor práctica que @Autowired)
    public WebSecurityConfig(RepositoryUserDetailsService userDetailService) {
        this.userDetailService = userDetailService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider());

        http
                .authorizeHttpRequests(authorize -> authorize
                        // PUBLIC PAGES
                        .requestMatchers(
                                "/css/*",
                                "/images/*",
                                "/",
                                "/register",
                                "/register/**",
                                "/products",
                                "/product/*",
                                "/filter",
                                "/products/filter",
                                "/products/loadMore",
                                "/product/*/image",
                                "/ubication",
                                "/loginerror",
                                "/web/register"

                        ).permitAll()

                        // PRIVATE PAGES - USER
                        .requestMatchers(
                                "/myAccount",
                                "/contact",
                                "/user/*/buys",
                                "/user/*/reviews",
                                "/product/{id}/purchase",
                                "/cart/add/{id}",
                                "/cart/add",
                                "/cart",
                                "/cart/checkout",
                                "/cart/remove",
                                "/product/{id}/review"
                        ).hasAnyRole("USER")

                        // PRIVATE PAGES - ADMIN
                        .requestMatchers(
                                "/product/new",
                                "/newProduct",
                                "/product/{id}/modify",
                                "/deleteProduct/{id}",
                                "/showdeleteProduct/{id}"
                        ).hasAnyRole("ADMIN")
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .failureUrl("/loginerror")
                        .defaultSuccessUrl("/")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }
}