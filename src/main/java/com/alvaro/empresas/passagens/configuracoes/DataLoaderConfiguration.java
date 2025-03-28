package com.alvaro.empresas.passagens.configuracoes;

import com.alvaro.empresas.passagens.services.DataLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

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
