package com.alvaro.empresas.passagens.security.configurations.exceptions;

import com.alvaro.empresas.passagens.security.configurations.exceptions.Exeptions.CustomAutenticationEntryPoint;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.stereotype.Component;

@Component
public class ExceptionsHandlerCustomConfiguration {
    public void loadConfiguration(ExceptionHandlingConfigurer<HttpSecurity> configurer) {
        configurer.authenticationEntryPoint(new CustomAutenticationEntryPoint());
    }
}
