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
import com.example.demo.Security.jwt.JwtRequestFilter;
import com.example.demo.Security.jwt.UnauthorizedHandlerJwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class WebSecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private UnauthorizedHandlerJwt unauthorizedHandlerJwt;

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
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        http
                .securityMatcher("/api/**")
                .exceptionHandling(handling -> handling.authenticationEntryPoint(unauthorizedHandlerJwt));

        http
                .authorizeHttpRequests(authorize -> authorize
                        // PRIVATE ENDPOINTS
                        .requestMatchers(HttpMethod.GET,"/api/products").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,"/api/products/*").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,"/api/products/*/filter").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,"/api/product/*").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,"/api/product/*/image").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,"/api/filter").hasRole("USER")
                        .requestMatchers(HttpMethod.POST,"/api/product/new").hasRole("USER")
                        .requestMatchers(HttpMethod.POST,"/api/product/*/modify").hasRole("USER")
                        .requestMatchers(HttpMethod.POST,"/api/product/*/purchase").hasRole("USER")
                        .requestMatchers(HttpMethod.POST,"/api/product/*/review").hasRole("USER")
                        .requestMatchers(HttpMethod.POST,"/api/product/*/file").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT,"/api/product/*").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE,"/api/product/*").hasRole("USER")

                        // PUBLIC ENDPOINTS
                        .anyRequest().permitAll()
                );

        // Disable Form login Authentication
        http.formLogin(formLogin -> formLogin.disable());

        // Disable CSRF protection (it is difficult to implement in REST APIs)
        http.csrf(csrf -> csrf.disable());

        // Disable Basic Authentication
        http.httpBasic(httpBasic -> httpBasic.disable());

        // Stateless session
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Add JWT Token filter
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
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
                                "/products/",
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
                                "/reviews/delete",
                                "/product/{id}/purchase",
                                "/cart/add/{id}",
                                "/cart/add",
                                "/cart",
                                "/cart/checkout",
                                "/cart/remove",
                                "/editUser/*",
                                "/updateAccount",
                                "/deleteAccount",
                                "/product/{id}/review",
                                "/products/{id}/file"
                        ).hasAnyRole("USER")

                        // PRIVATE PAGES - ADMIN
                        .requestMatchers(
                                "/product/new",
                                "/newProduct",
                                "/product/{id}/modify",
                                "/deleteProduct/{id}",
                                "/showdeleteProduct/{id}",
                                "/userList",
                                "/admin/users",
                                "/admin/users/delete/*",
                                "/admin/**"
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