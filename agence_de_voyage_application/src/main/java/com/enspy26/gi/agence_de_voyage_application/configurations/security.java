package com.enspy26.gi.agence_de_voyage_application.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.enspy26.gi.annulation_reservation.configurations.JWtFilter;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class security {

    private JWtFilter jWtFilter;
    private BCryptPasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity https) throws Exception {

        https.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(
                        (auth) -> {
                            auth.requestMatchers("/utilisateur/inscription").permitAll()
                                    .requestMatchers("/utilisateur/connexion").permitAll()
                                    .requestMatchers("/utilisateur/profil/*").permitAll()
                                    .requestMatchers("/utilisateur/test").permitAll()
                                    .requestMatchers("/voyage/**").permitAll()
                                    .requestMatchers("/voyage/all").permitAll()
                                    .requestMatchers("/voyage/byId/*").permitAll()
                                    .requestMatchers("/reservation/payer").permitAll()
                                    .requestMatchers("/historique/**").permitAll()
                                    .requestMatchers("/coupon/**").permitAll()
                                    .requestMatchers("/swagger-ui/**").permitAll()
                                    .requestMatchers("/v3/api-docs/**").permitAll()
                                    .requestMatchers("/organizations/**").permitAll()
                                    .requestMatchers("/agence").permitAll()
                                    .requestMatchers("/agence/**").permitAll()
                                    .requestMatchers("/statistics/**").permitAll()
                                    .requestMatchers("/notification/**").permitAll()
                                    .requestMatchers("/ws/**").permitAll()
                                    .requestMatchers("/contact/**").permitAll()
                                    .anyRequest().authenticated();
                        })
                .sessionManagement(httpSecuritySessionManagementConfiguger -> httpSecuritySessionManagementConfiguger
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jWtFilter, UsernamePasswordAuthenticationFilter.class);

        return https.build();
    }

    // Pour l'authentification on fournit a Spring Security un Gestionnaire
    // d'authentification, l'AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Pour que le gestionnaire d'authentification fonctionne correctement on lui
    // fournir un fournisseur d'authentification
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder);

        return daoAuthenticationProvider;
    }
}
