package com.alvaro.empresas.passagens.configuracoes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.alvaro.empresas.passagens.services.DataLoaderService;

@Profile({"h2", "mysql"})
@Configuration
public class DataLoaderConfiguration {
    @Autowired
    private DataLoaderService dataLoaderService;

    public String loadData() {
        dataLoaderService.loadDados();
        return "";
    }
}
