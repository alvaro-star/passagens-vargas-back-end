package com.alvaro.empresas.passagens.security;

import com.alvaro.empresas.passagens.security.configurations.CorsCustomConfiguration;
import com.alvaro.empresas.passagens.security.configurations.RoutesConfiguration;
import com.alvaro.empresas.passagens.security.configurations.exceptions.ExceptionsHandlerCustomConfiguration;
import com.alvaro.empresas.passagens.security.jwt.SecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class MainSecurity {

    @Autowired
    private SecurityFilter securityFilter;
    @Autowired
    private Environment env;
    @Autowired
    private RoutesConfiguration routesConfiguration;
    @Autowired
    private CorsCustomConfiguration corsCustomConfiguration;
    @Autowired
    private ExceptionsHandlerCustomConfiguration exceptionsHandlerConfiguration;

    private boolean isProfileActive(String name) {
        for (String activeProfile : env.getActiveProfiles())
            if (activeProfile.equals(name))
                return true;
        return false;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        var http = httpSecurity
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        if (isProfileActive("h2") || isProfileActive("mysql")) {
            http.authorizeHttpRequests(routesConfiguration::loadDevRoutes);
            http.csrf(AbstractHttpConfigurer::disable);
            http.cors(corsCustomConfiguration::loadDevCors);
            http.headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        } else if (isProfileActive("prod")) {
            http.cors(corsCustomConfiguration::loadProdCors);
            http.authorizeHttpRequests(routesConfiguration::loadProdRoutes);
        } else http.authorizeHttpRequests(routesConfiguration::loadDefaultRoutes);
        http.exceptionHandling(exceptionsHandlerConfiguration::loadConfiguration);
        http.addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
