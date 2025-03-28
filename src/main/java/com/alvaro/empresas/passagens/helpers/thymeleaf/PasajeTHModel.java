package com.alvaro.empresas.passagens.helpers.thymeleaf;

import com.alvaro.empresas.passagens.models.PassagemModel;
import org.thymeleaf.context.Context;

public record PasajeTHModel(
        String empresa,
        String fechaHora,
        Integer piso,
        Integer nsilla,
        Integer carril,
        String nome,
        String carnet,
        String nascimiento,
        ParadaTHModel origen,
        ParadaTHModel destino,
        String precio,
        String metodoPago,
        Float descuento) {
    public PasajeTHModel(String empresa, PassagemModel model, String fechaHora, String metodoPago) {
        this(
                empresa,
                fechaHora,
                model.getPreco().getNPiso(),
                model.getNAssento(),
                model.getSaida().getPlataforma(),
                model.getNome(),
                model.getDocumento(),
                model.getNascimento().toString(),
                new ParadaTHModel(model.getSaida()),
                new ParadaTHModel(model.getDestino()),
                model.getPrecoPago().toString(),
                metodoPago,
                0f);
    }

    public Context toContextThymeleaf() {
        Context context = new Context();
        context.setVariable("empresa", this.empresa);
        context.setVariable("fechaHora", this.fechaHora);
        context.setVariable("piso", this.piso);
        context.setVariable("nsilla", this.nsilla);
        context.setVariable("carril", this.carril);
        context.setVariable("nome", this.nome);
        context.setVariable("carnet", this.carnet);
        context.setVariable("nascimiento", this.nascimiento);
        context.setVariable("origen", this.origen);
        context.setVariable("destino", this.destino);
        context.setVariable("precio", this.precio);
        context.setVariable("metodoPago", this.metodoPago);
        context.setVariable("descuento", this.descuento);
        return context;
    }
}
