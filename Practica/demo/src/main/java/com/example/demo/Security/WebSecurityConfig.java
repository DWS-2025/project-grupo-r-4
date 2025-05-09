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
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/signup").permitAll()
                        .requestMatchers("/products").permitAll()
                        .requestMatchers("/myAccount").permitAll()
                        .requestMatchers("/register").permitAll()
                        .requestMatchers("/contact").permitAll()
                        .requestMatchers("/searchmatches").permitAll()
                        .requestMatchers("/searchTeam").permitAll()
                        .requestMatchers("/css/*").permitAll()
                        .requestMatchers("/images/*").permitAll()
                        .requestMatchers("/js/main.js").permitAll()
                        .requestMatchers("/teams/*/image").permitAll()
                        .requestMatchers("/error").permitAll()


                        // PRIVATE PAGES
                        .requestMatchers("/newcompetition").hasAnyRole("ADMIN")
                        .requestMatchers("/newteam").hasAnyRole("ADMIN")
                        .requestMatchers("/teams/*/file").hasAnyRole("USER")
                        .requestMatchers("/teams/{id}/delete").hasAnyRole("ADMIN")
                        .requestMatchers("/removedTeam").hasAnyRole("ADMIN")
                        .requestMatchers("/players/*/edit").hasAnyRole("ADMIN")
                        .requestMatchers("/players/*/delete").hasAnyRole("ADMIN")
                        .requestMatchers("/removedPlayer").hasAnyRole("ADMIN")
                        .requestMatchers("/players/*/favorite").hasAnyRole("USER")
                        .requestMatchers("/players/*/unfavorite").hasAnyRole("USER")
                        .requestMatchers("/players/*/update").hasAnyRole("ADMIN")
                        .requestMatchers("/favoritesPlayers").hasAnyRole("USER")
                        .requestMatchers("/newplayer").hasAnyRole("USER")
                        .requestMatchers("/matches/*/comments").hasAnyRole("USER")
                        .requestMatchers("/matches/*").hasAnyRole("USER")
                        .requestMatchers("/matches/*/delete").hasAnyRole("ADMIN")
                        .requestMatchers("/removedMatch").hasAnyRole("ADMIN")
                        .requestMatchers("/matches/*/comments/*/delete").hasAnyRole("USER")
                        .requestMatchers("/matches/*/events").hasAnyRole("ADMIN")
                        .requestMatchers("/matches/*/events/new").hasAnyRole("ADMIN")
                        .requestMatchers("/newmatch").hasAnyRole("ADMIN")
                        .requestMatchers("/user/").hasAnyRole("USER")
                        .requestMatchers("/edituser").hasAnyRole("USER")
                        .requestMatchers("/deleteuser").hasAnyRole("USER")
                        .requestMatchers("/removedUser").hasAnyRole("USER")
                        .requestMatchers("/users").hasAnyRole("ADMIN")
                        .requestMatchers("/deleteuser/**").hasAnyRole("ADMIN")
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
