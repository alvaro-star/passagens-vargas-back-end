package com.alvaro.empresas.passagens.services.relatorios;

import com.alvaro.empresas.passagens.dtos.viajes.JPQL.PasajeJPQLBusca;
import com.alvaro.empresas.passagens.dtos.viajes.JPQL.ViajeDTOJPQLRelatorio;
import com.alvaro.empresas.passagens.enums.TipoPagamento;
import com.alvaro.empresas.passagens.helpers.DateAuxiliarFunctions;
import com.alvaro.empresas.passagens.helpers.services.EmailService;
import com.alvaro.empresas.passagens.helpers.thymeleaf.CiudadTHModel;
import com.alvaro.empresas.passagens.helpers.thymeleaf.MetodoTHModel;
import com.alvaro.empresas.passagens.helpers.thymeleaf.PDFThymeleaf;
import com.alvaro.empresas.passagens.models.PrecioModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.services.LugarService;
import com.alvaro.empresas.passagens.repositories.PasajeRepository;
import com.alvaro.empresas.passagens.services.EmpresaService;
import com.alvaro.empresas.passagens.services.validacao.TiempoViajeService;
import com.itextpdf.kernel.geom.PageSize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RelatorioService {
    private final EmpresaService empresaService;
    private final PasajeRepository pasajeRepository;
    private final EmailService emailService;
    private final DateAuxiliarFunctions dateAuxiliarFunctions;
    private final PDFThymeleaf pdfThymeleaf;
    private final LugarService lugarService;
    private final TiempoViajeService tiempoViajeService;

    @Autowired
    public RelatorioService(EmpresaService empresaService, PasajeRepository pasajeRepository, EmailService emailService, LugarService lugarService, TiempoViajeService tiempoViajeService, PDFThymeleaf pdfThymeleaf) {
        this.empresaService = empresaService;
        this.pasajeRepository = pasajeRepository;
        this.emailService = emailService;
        this.dateAuxiliarFunctions = new DateAuxiliarFunctions();
        this.lugarService = lugarService;
        this.tiempoViajeService = tiempoViajeService;
        this.pdfThymeleaf = pdfThymeleaf;
    }

    @Value("${api.viaje.max-time-viaje-day}")
    private Integer tempoMaxViajeDias;

    // Categorizar las ciudades por el numero de pasajeros que van a ella y no por os autobuses
    // Ordenar las lisdas de ciudades con base en el numero de pasajes vendidos
    public byte[] makeRelatorioMensual(UUID idEmpresa, Date dateAnalize, Model model) {
        var empresa = empresaService.findById(idEmpresa);
        LocalDateTime inicio = dateAuxiliarFunctions.getFirstDayOfMonthDate(dateAnalize);
        LocalDateTime fim = dateAuxiliarFunctions.getLastDayOfMonthDate(dateAnalize);
        List<ViajeDTOJPQLRelatorio> viajes = tiempoViajeService.findViajesFromEmpresa(empresa, inicio, fim);

        HashMap<Integer, Integer> salidasIdNPasajes = new HashMap<>(), destinosIdNPasajes = new HashMap<>();
        List<PasajeJPQLBusca> pasajesBD;
        RelatorioModel relatorio = new RelatorioModel(empresa);

        HashMap<String, HashMetodoPagamentoValor> pagamentosWeb = new HashMap<>(), pagamentosNoWeb = new HashMap<>();
        for (TipoPagamento metodo : TipoPagamento.values()) {
            pagamentosWeb.put(metodo.toString(), new HashMetodoPagamentoValor(metodo.toString(), 0.0));
            pagamentosNoWeb.put(metodo.toString(), new HashMetodoPagamentoValor(metodo.toString(), 0.0));
        }

        for (ViajeDTOJPQLRelatorio viaje : viajes) {
            relatorio.nViajes++;
            if (viaje.viaje().isCancelado()) relatorio.nViajesCancelados++;

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

        for (TipoPagamento value : TipoPagamento.values()) {
            metodos.add(new MetodoTHModel(value.toString(), relatorio.getDineroPorMetodoWeb().get(value.toString()).valor, relatorio.getDineroPorMetodoNoWeb().get(value.toString()).valor));
        }

        return generatePdfFromHtml(relatorio, salidasThModels, destinosTHModels, metodos);
    }

    public void ordenarLugares(List<LugarModel> lugares, HashMap<Integer, Integer> lugaresNPasajes) {
        lugares.sort(Comparator.comparingInt(l -> lugaresNPasajes.get(l.getId())));
    }

    public void classificarPasajeFromPrecio(List<PasajeJPQLBusca> pasajes, HashMap<Integer, Integer> salidasId, HashMap<Integer, Integer> destinosId, HashMap<String, HashMetodoPagamentoValor> pagamentosWeb, HashMap<String, HashMetodoPagamentoValor> pagamentosNoWeb, RelatorioModel relatorio) {

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

    public byte[] generatePdfFromHtml(RelatorioModel relatorio, List<CiudadTHModel> salidasThModels, List<CiudadTHModel> destinosTHModels, List<MetodoTHModel> metodos) {
        var context = new Context();
        context.setVariable("empresaNombre", relatorio.getEmpresa().getNombre());
        context.setVariable("nMes", relatorio.getNMes());
        context.setVariable("nAno", relatorio.getNAno());
        context.setVariable("nViajes", relatorio.getNViajes());
        context.setVariable("nViajesCancelados", relatorio.getNViajesCancelados());
        context.setVariable("nPasajesVendidos", relatorio.getNPasajesTotal());
        context.setVariable("nPasajesCancelados", relatorio.getNPasajesCancelados());
        context.setVariable("salidas", salidasThModels);
        context.setVariable("destinos", destinosTHModels);
        context.setVariable("metodos", metodos);
        context.setVariable("valorArrecadadoWeb", relatorio.getValorArrecadadoWeb());
        context.setVariable("valorArrecadadoNoWeb", relatorio.getValorArrecadadoNoWeb());
        context.setVariable("valorTotal", relatorio.getValorArrecadadoNoWeb() + relatorio.getValorArrecadadoWeb());
        return pdfThymeleaf.generatePDFByTemplate("/empresa/relatorio", context, PageSize.A4);
    }
}
