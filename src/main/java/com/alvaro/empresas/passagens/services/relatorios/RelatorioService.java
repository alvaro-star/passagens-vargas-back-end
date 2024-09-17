package com.alvaro.empresas.passagens.services.relatorios;

import com.alvaro.empresas.passagens.dtos.viajes.JPQL.PasajeJPQLBusca;
import com.alvaro.empresas.passagens.dtos.viajes.JPQL.ViajeDTOJPQLRelatorio;
import com.alvaro.empresas.passagens.enums.MetodoPagamentoEnum;
import com.alvaro.empresas.passagens.helpers.DateAuxiliarFunctions;
import com.alvaro.empresas.passagens.helpers.services.EmailService;
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
            LugarService lugarService) {
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
    public byte[] makeRelatorioMensual(UUID idEmpresa, Date dateAnalize) {
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
        return generatePdfFromHtml(
                relatorio,
                inicio.getMonthValue(),
                inicio.getYear(),
                salidas,
                salidasIdNPasajes,
                destinos,
                destinosIdNPasajes
        );
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
            int nMes,
            int nAno,
            List<LugarModel> salidas,
            HashMap<Integer, Integer> salidasId,
            List<LugarModel> destinos,
            HashMap<Integer, Integer> destinosId) {
        StringBuilder str = new StringBuilder();
        str.append("""
                <!DOCTYPE html>
                <html lang="pt-br">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Relatório de Viagens</title>
                    <style>
                        body {font-family: Arial, sans-serif;background-color: white;margin: 0;}
                        .container {background-color: #fff;padding: 20px;box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);max-width: 800px;margin: auto;}
                        h1,h2 {text-align: start;color: #333;}
                        table {width: 100%;border-collapse: collapse;margin-bottom: 20px;}
                        table,th,td {border: 1px solid #ddd;}
                        th,td {padding: 12px;text-align: left;}
                        th {background-color: #4CAF50;color: white;}
                        .total {text-align: right;font-weight: bold;}
                        .total-title {font-weight: bold;}
                        .total-row {background-color: #f9f9f9;text-align: start;}
                        ul {list-style-type: square;padding-left: 20px;}
                        .footer {text-align: center;font-size: 0.9em;color: #666;}
                        .text-right {text-align: right;}
                        .text-left {text-align: start;}
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>Relatorio de Ventas</h1>
                """);
        str.append(String.format("<h2>Empresa: %s</h2>", relatorio.getEmpresa().getNombre()));
        str.append(String.format("<p><strong>Mês de Faturação:</strong> %s %s</p>", nMes, nAno));
        str.append("""
                <h3>Datos delos Viajes Realizados</h3>
                        <table>
                            <thead>
                                <tr>
                                    <th>Nombre</th>
                                    <th>Valor (Unid)</th>
                                </tr>
                            </thead>
                            <tbody class="">
                """);
        str.append(String.format("<tr><td>N Viajes registrados</td><td class=\"text-right\">%s</td></tr>", relatorio.getNViajes()));
        str.append(String.format("<tr><td>N Viajes Cancelados</td><td class=\"text-right\">%s</td></tr>", relatorio.getNViajesCancelados()));
        str.append(String.format("<tr><td>N Pasajes Vendidos</td><td class=\"text-right\">%s</td></tr>", relatorio.getNPasajesTotal()));
        str.append(String.format("<tr><td>N Pasajes Rembolsados</td><td class=\"text-right\">%s</td></tr>", relatorio.getNPasajesCancelados()));
        str.append("""
                            </tbody>
                        </table>
                        <h3>Ciudades de Origen mas Compradas</h3>
                        <table>
                            <thead>
                                <tr><th>Ciudad</th><th>N Pasajes</th></tr>
                            </thead>
                            <tbody>
                """);
        for (LugarModel salida : salidas)
            str.append(String.format("<tr><td>%s</td><td>%s</td></tr>", salida.getCiudad().getNombre(), salidasId.get(salida.getId())));

        str.append("""
                    </tbody>
                        </table>
                        <h3>
                            Ciudades de Destino mas Compradas
                        </h3>
                        <table>
                            <thead>
                                <tr>
                                    <th>Ciudad</th>
                                    <th>N Pasajes</th>
                                </tr>
                            </thead>
                            <tbody>
                """);
        for (LugarModel destino : destinos)
            str.append(String.format("<tr><td>%s</td><td>%s</td></tr>", destino.getCiudad().getNombre(), destinosId.get(destino.getId())));
        str.append("""
                            </tbody>
                        </table>
                        <h3>Arrecadação por Métodos de Pagamento</h3>
                        <table>
                            <thead>
                                <tr>
                                    <th>Metodo de Pago</th>
                                    <th>En el Sitio Web</th>
                                    <th>En las boleterias</th>
                                </tr>
                            </thead>
                            <tbody>
                """);

        for (MetodoPagamentoEnum value : MetodoPagamentoEnum.values()) {
            str.append(String.format("<tr><td>%s</td><td class=\"text-right\">%s bs</td><td class=\"text-right\">%s bs</td></tr>",
                    value.toString(),
                    relatorio.getDineroPorMetodoWeb().get(value.toString()).valor,
                    relatorio.getDineroPorMetodoNoWeb().get(value.toString()).valor
            ));
        }
        str.append(String.format("""
                <tr class="total-row">
                    <td class="total-title">Total</td>
                    <td class="total">%s Bs</td>
                    <td class="total">%s Bs</td>
                </tr>
                """, relatorio.getValorArrecadadoWeb(), relatorio.getValorArrecadadoNoWeb()));
        str.append(String.format("""
                <tr class="total-row">
                    <td class="total-title" colspan="2">Suma Total</td>
                    <td class="total">%s Bs</td>
                </tr>
                """, relatorio.getValorArrecadadoWeb() + relatorio.getValorArrecadadoNoWeb()));
        str.append("""
                            </tbody>
                        </table>
                        <div class="footer">
                            <p>&copy; 2024 Viagens XYZ. Todos os direitos reservados.</p>
                        </div>
                    </div>
                </body>
                </html>
                """);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        ConverterProperties converterProperties = new ConverterProperties();
        HtmlConverter.convertToPdf(str.toString(), pdfDocument, converterProperties);
        pdfDocument.close();
        return byteArrayOutputStream.toByteArray();
    }
}
