package com.alvaro.empresas.passagens.helpers.thymeleaf;

import com.alvaro.empresas.passagens.models.PassagemModel;
import org.thymeleaf.context.Context;

public record PassagemTHModel(
        String empresa,
        String dataHora,
        Integer piso,
        Integer nAssento,
        Integer plataforma,
        String nome,
        String cpf,
        String nascimento,
        ParadaTHModel origen,
        ParadaTHModel destino,
        String preco,
        String metodoPago,
        Float descuento) {
    public PassagemTHModel(String empresa, PassagemModel model, String fechaHora, String metodoPago) {
        this(
                empresa,
                fechaHora,
                model.getPreco().getNPiso(),
                model.getNAssento(),
                model.getSaida().getPlataforma(),
                model.getNome(),
                model.getCpf(),
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
        context.setVariable("fechaHora", this.dataHora);
        context.setVariable("piso", this.piso);
        context.setVariable("nsilla", this.nAssento);
        context.setVariable("carril", this.plataforma);
        context.setVariable("nome", this.nome);
        context.setVariable("carnet", this.cpf);
        context.setVariable("nascimiento", this.nascimento);
        context.setVariable("origen", this.origen);
        context.setVariable("destino", this.destino);
        context.setVariable("preco", this.preco);
        context.setVariable("metodoPago", this.metodoPago);
        context.setVariable("descuento", this.descuento);
        return context;
    }
}
