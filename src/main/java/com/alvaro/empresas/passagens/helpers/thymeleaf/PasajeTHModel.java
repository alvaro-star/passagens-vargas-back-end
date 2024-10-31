package com.alvaro.empresas.passagens.helpers.thymeleaf;

import com.alvaro.empresas.passagens.models.PasajeModel;
import org.thymeleaf.context.Context;

public record PasajeTHModel(
        String empresa,
        String fechaHora,
        Integer piso,
        Integer nsilla,
        Integer carril,
        String nombre,
        String carnet,
        String nascimiento,
        ParadaTHModel origen,
        ParadaTHModel destino,
        String precio,
        String metodoPago,
        Float descuento
) {
    public PasajeTHModel(String empresa, PasajeModel model, String fechaHora, String metodoPago) {
        this(
                empresa,
                fechaHora,
                model.getPrecio().getNPiso(),
                model.getNSilla(),
                model.getSalida().getPlataforma(),
                model.getNombre(),
                model.getCarnet(),
                model.getNascimento().toString(),
                new ParadaTHModel(model.getSalida()),
                new ParadaTHModel(model.getDestino()),
                model.getPrecioPagado().toString(),
                metodoPago,
                0f
        );
    }

    public Context toContextThymeleaf() {
        Context context = new Context();
        context.setVariable("empresa", this.empresa);
        context.setVariable("fechaHora", this.fechaHora);
        context.setVariable("piso", this.piso);
        context.setVariable("nsilla", this.nsilla);
        context.setVariable("carril", this.carril);
        context.setVariable("nombre", this.nombre);
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
