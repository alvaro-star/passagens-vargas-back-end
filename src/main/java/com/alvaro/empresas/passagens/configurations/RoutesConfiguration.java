package com.alvaro.empresas.passagens.configurations;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

public class RoutesConfiguration {
    private static void loadCommonRoutes(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry routesConfigurations) {
        routesConfigurations
                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/refresh").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/forget_password").permitAll()
                .requestMatchers(HttpMethod.PUT, "/auth/reset_password").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/validar").permitAll()
                .requestMatchers(HttpMethod.POST, "/viajes").permitAll()
                .requestMatchers(HttpMethod.GET, "/viajes/{id}").permitAll()
                .requestMatchers(HttpMethod.POST, "/pasajes").permitAll()
                .requestMatchers(HttpMethod.GET, "/ciudades/{nombre}/like").permitAll();
    }

    public static void loadDevRoutes(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry routesConfigurations) {
        loadCommonRoutes(routesConfigurations);
        routesConfigurations.requestMatchers(HttpMethod.GET, "/facturas/{idEmpresa}").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                .anyRequest().authenticated();
    }

    public static void loadProdRoutes(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry routesConfigurations) {
        loadCommonRoutes(routesConfigurations);
        routesConfigurations.anyRequest().authenticated();
    }
}
