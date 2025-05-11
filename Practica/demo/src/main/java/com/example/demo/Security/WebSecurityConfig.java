package com.example.demo.Security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;





@Configuration
public class WebSecurityConfig {

    @Autowired
    public RepositoryUserDetailsService userDetailService;

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
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        http
                .authorizeHttpRequests(authorize -> authorize
                        // PUBLIC PAGES
                        .requestMatchers("/css/*").permitAll()
                        .requestMatchers("/images/*").permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/register").permitAll()
                        .requestMatchers("/products").permitAll()
                        .requestMatchers("product/*").permitAll()
                        .requestMatchers("/filter").permitAll()
                        .requestMatchers("/products/filter").permitAll()
                        .requestMatchers("/products/loadMore").permitAll()
                        .requestMatchers("/product/*/image").permitAll()
                        .requestMatchers("/ubication").permitAll()
                        .requestMatchers("/loginerror").permitAll()


                        // PRIVATE PAGES
                        .requestMatchers("/myAccount").hasAnyRole("USER")
                        .requestMatchers("/contact").hasAnyRole("USER")
                        .requestMatchers("/user/*/buys").hasAnyRole("USER")
                        .requestMatchers("/user/*/reviews").hasAnyRole("USER")
                        .requestMatchers("/product/{id}/purchase").hasAnyRole("USER")
                        .requestMatchers("/cart/add/{id}").hasAnyRole("USER")///////
                        .requestMatchers("/cart/add").hasAnyRole("USER")///////
                        .requestMatchers("/cart").hasAnyRole("USER")
                        .requestMatchers("/cart/checkout").hasAnyRole("USER")////////////
                        .requestMatchers("/cart/remove").hasAnyRole("USER")//////////////
                        .requestMatchers("/product/{id}/review").hasAnyRole("USER")
                        .requestMatchers("/product/new").hasAnyRole("ADMIN")
                        .requestMatchers("/newProduct").hasAnyRole("ADMIN")
                        .requestMatchers("/product/{id}/modify").hasAnyRole("ADMIN")
                        .requestMatchers("/deleteProduct/{id}").hasAnyRole("ADMIN")
                        .requestMatchers("/showdeleteProduct/{id}").hasAnyRole("ADMIN")






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
