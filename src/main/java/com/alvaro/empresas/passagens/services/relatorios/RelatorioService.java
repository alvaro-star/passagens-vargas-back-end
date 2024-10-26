package com.alvaro.empresas.passagens.services.relatorios;

import com.alvaro.empresas.passagens.dtos.viajes.JPQL.PasajeJPQLBusca;
import com.alvaro.empresas.passagens.dtos.viajes.JPQL.ViajeDTOJPQLRelatorio;
import com.alvaro.empresas.passagens.enums.MetodoPagamentoEnum;
import com.alvaro.empresas.passagens.helpers.DateAuxiliarFunctions;
import com.alvaro.empresas.passagens.helpers.services.EmailService;
import com.alvaro.empresas.passagens.helpers.tymeleaf.CiudadTHModel;
import com.alvaro.empresas.passagens.helpers.tymeleaf.MetodoTHModel;
import com.alvaro.empresas.passagens.models.PrecioModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.services.LugarService;
import com.alvaro.empresas.passagens.repositories.PasajeRepository;
import com.alvaro.empresas.passagens.repositories.ViajeRepository;
import com.alvaro.empresas.passagens.services.EmpresaService;
import com.alvaro.empresas.passagens.services.validacao.TempoMaxViajeValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import org.springframework.ui.Model;
import org.springframework.ui.freemarker.SpringTemplateLoader;
import org.thymeleaf.context.Context;

import javax.swing.*;
import java.io.ByteArrayOutputStream;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RelatorioService {
    private final ViajeRepository viajeRepository;
    private final EmpresaService empresaService;
    private final PasajeRepository pasajeRepository;
    private final EmailService emailService;
    private DateAuxiliarFunctions dateAuxiliarFunctions;
    private final LugarService lugarService;

    @Autowired
    public RelatorioService(
            ViajeRepository viajeRepository,
            EmpresaService empresaService,
            PasajeRepository pasajeRepository,
            EmailService emailService,
            LugarService lugarService,
            SpringTemplateLoader templateLoader) {
        this.viajeRepository = viajeRepository;
        this.empresaService = empresaService;
        this.pasajeRepository = pasajeRepository;
        this.emailService = emailService;
        this.dateAuxiliarFunctions = new DateAuxiliarFunctions();
        this.lugarService = lugarService;
    }

    @Value("${api.viaje.max-time-viaje-day}")
    private Integer tempoMaxViajeDias;

    // Categorizar las ciudades por el numero de pasajeros que van a ella y no por os autobuses
    // Ordenar las lisdas de ciudades con base en el numero de pasajes vendidos
    public byte[] makeRelatorioMensual(UUID idEmpresa, Date dateAnalize, Model model) {
        var empresa = empresaService.findById(idEmpresa);
        LocalDateTime inicio = dateAuxiliarFunctions.getDateWithFirstDayOfMonth(dateAnalize);
        LocalDateTime fim = dateAuxiliarFunctions.getDateWithLastDayOfMonth(dateAnalize);
        List<ViajeDTOJPQLRelatorio> viajes = TempoMaxViajeValidation.findAllViajesFromEmpresaInInterval(viajeRepository, tempoMaxViajeDias, idEmpresa, inicio, fim);

        HashMap<Integer, Integer> salidasIdNPasajes = new HashMap<>(), destinosIdNPasajes = new HashMap<>();
        List<PasajeJPQLBusca> pasajesBD;
        RelatorioModel relatorio = new RelatorioModel(empresa);

        HashMap<String, HashMetodoPagamentoValor> pagamentosWeb = new HashMap<>(), pagamentosNoWeb = new HashMap<>();
        for (MetodoPagamentoEnum metodo : MetodoPagamentoEnum.values()) {
            pagamentosWeb.put(metodo.toString(), new HashMetodoPagamentoValor(metodo.toString(), 0.0));
            pagamentosNoWeb.put(metodo.toString(), new HashMetodoPagamentoValor(metodo.toString(), 0.0));
        }

        for (ViajeDTOJPQLRelatorio viaje : viajes) {
            relatorio.nViajes++;
            if (viaje.viaje().isCancelado())
                relatorio.nViajesCancelados++;

            for (PrecioModel precio : viaje.viaje().getPrecios()) {
                pasajesBD = pasajeRepository.getPasajesPagados(precio.getId());
                classificarPasajeFromPrecio(pasajesBD, salidasIdNPasajes, destinosIdNPasajes, pagamentosWeb, pagamentosNoWeb, relatorio);
            }
        }

        List<LugarModel> salidas = lugarService.findAllById(salidasIdNPasajes.keySet());
        List<LugarModel> destinos = lugarService.findAllById(destinosIdNPasajes.keySet());

        ordenarLugares(salidas, salidasIdNPasajes);
        ordenarLugares(destinos, destinosIdNPasajes);

        relatorio.setValorArrecadadoWeb(getValorTotalArrecadado(pagamentosWeb));
        relatorio.setValorArrecadadoNoWeb(getValorTotalArrecadado(pagamentosNoWeb));
        relatorio.setDineroPorMetodoNoWeb(pagamentosNoWeb);
        relatorio.setDineroPorMetodoWeb(pagamentosWeb);
        relatorio.setNMes(inicio.getMonthValue());
        relatorio.setNAno(inicio.getYear());

        List<CiudadTHModel> salidasThModels = new ArrayList<>(), destinosTHModels = new ArrayList<>();
        List<MetodoTHModel> metodos = new ArrayList<>();
        for (LugarModel salida : salidas)
            salidasThModels.add(new CiudadTHModel(salida.getCiudad().getNombre(), salidasIdNPasajes.get(salida.getId())));
        for (LugarModel destino : destinos)
            destinosTHModels.add(new CiudadTHModel(destino.getCiudad().getNombre(), destinosIdNPasajes.get(destino.getId())));

        for (MetodoPagamentoEnum value : MetodoPagamentoEnum.values()) {
            metodos.add(new MetodoTHModel(
                    value.toString(),
                    relatorio.getDineroPorMetodoWeb().get(value.toString()).valor,
                    relatorio.getDineroPorMetodoNoWeb().get(value.toString()).valor
            ));
        }

        return generatePdfFromHtml(relatorio, salidasThModels, destinosTHModels, metodos);
    }

    public void ordenarLugares(List<LugarModel> lugares, HashMap<Integer, Integer> lugaresNPasajes) {
        lugares.sort(Comparator.comparingInt(l -> lugaresNPasajes.get(l.getId())));
    }

    public void classificarPasajeFromPrecio(
            List<PasajeJPQLBusca> pasajes,
            HashMap<Integer, Integer> salidasId,
            HashMap<Integer, Integer> destinosId,
            HashMap<String, HashMetodoPagamentoValor> pagamentosWeb, HashMap<String, HashMetodoPagamentoValor> pagamentosNoWeb, RelatorioModel relatorio) {

        relatorio.nPasajesTotal += pasajes.size();
        for (PasajeJPQLBusca pasaje : pasajes) {
            addValueInHashMap(salidasId, pasaje.salidaLugarId());
            addValueInHashMap(destinosId, pasaje.destinoLugarId());
            if (pasaje.facturaRembolsoId() != null) {
                relatorio.nPasajesCancelados++;
                continue;
            }
            if (pasaje.compradoWeb()) {
                pagamentosWeb.get(pasaje.metodoPago().toString()).valor += pasaje.precioPagado().doubleValue();
                if (pasaje.enEfectivo())
                    emailService.mandarEmail("vargasalvaro248@gmail.com", "Web - Erro de Processamento", "Existe un pasaje que fue comprado en efectivo");
            } else {
                pagamentosNoWeb.get(pasaje.metodoPago().toString()).valor += pasaje.precioPagado().doubleValue();
            }
        }
    }

    public double getValorTotalArrecadado(HashMap<String, HashMetodoPagamentoValor> pagamento) {
        double soma = 0;
        for (HashMetodoPagamentoValor value : pagamento.values())
            soma += value.valor;
        return soma;
    }

    public void addValueInHashMap(HashMap<Integer, Integer> hashMap, Integer key) {
        Integer auxMap = hashMap.get(key);
        if (auxMap == null) hashMap.put(key, 0);
        else hashMap.put(key, auxMap + 1);
    }

    public byte[] generatePdfFromHtml(
            RelatorioModel relatorio,
            List<CiudadTHModel> salidasThModels,
            List<CiudadTHModel> destinosTHModels,
            List<MetodoTHModel> metodos
    ) {
        var conext = new Context();
        conext.setVariable("empresaNombre", relatorio.getEmpresa().getNombre());
        conext.setVariable("nMes", relatorio.getNMes());
        conext.setVariable("nAno", relatorio.getNAno());
        conext.setVariable("nViajes", relatorio.getNViajes());
        conext.setVariable("nViajesCancelados", relatorio.getNViajesCancelados());
        conext.setVariable("nPasajesVendidos", relatorio.getNPasajesTotal());
        conext.setVariable("nPasajesCancelados", relatorio.getNPasajesCancelados());
        conext.setVariable("salidas", salidasThModels);
        conext.setVariable("destinos", destinosTHModels);
        conext.setVariable("metodos", metodos);
        conext.setVariable("valorArrecadadoWeb", relatorio.getValorArrecadadoWeb());
        conext.setVariable("valorArrecadadoNoWeb", relatorio.getValorArrecadadoNoWeb());
        conext.setVariable("valorTotal", relatorio.getValorArrecadadoNoWeb() + relatorio.getValorArrecadadoWeb());

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        ConverterProperties converterProperties = new ConverterProperties();
        HtmlConverter.convertToPdf("Teste", pdfDocument, converterProperties);
        pdfDocument.close();
        return byteArrayOutputStream.toByteArray();
    }
}
