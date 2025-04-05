package com.alvaro.empresas.passagens.pagamentos.services.relatorios;

import java.time.LocalDateTime;
import java.util.*;

import com.alvaro.empresas.passagens.models.PassagemModel;
import com.alvaro.empresas.passagens.models.ViagemModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import com.alvaro.empresas.passagens.enums.TipoPagamento;
import com.alvaro.empresas.passagens.helpers.DateTimeUtils;
import com.alvaro.empresas.passagens.services.EmailService;
import com.alvaro.empresas.passagens.helpers.thymeleaf.CidadeTHModel;
import com.alvaro.empresas.passagens.helpers.thymeleaf.MetodoTHModel;
import com.alvaro.empresas.passagens.helpers.thymeleaf.PDFThymeleaf;
import com.alvaro.empresas.passagens.models.PrecoModel;
import com.alvaro.empresas.passagens.paradas.models.LugarModel;
import com.alvaro.empresas.passagens.paradas.repositories.LugarRepository;
import com.alvaro.empresas.passagens.repositories.EmpresaRepository;
import com.alvaro.empresas.passagens.repositories.PassagemRepository;
import com.alvaro.empresas.passagens.configuracoes.validations.services.TempoViagemService;
import com.itextpdf.kernel.geom.PageSize;

@Service
public class RelatorioService {
    @Autowired
    private EmpresaRepository empresaRepository;
    @Autowired
    private PassagemRepository passagemRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PDFThymeleaf pdfThymeleaf;
    @Autowired
    private LugarRepository lugarRepository;
    @Autowired
    private TempoViagemService tempoViagemService;

    @Value("${api.viaje.max-time-viaje-day}")
    private Integer tempoMaxViagemDias;

    public byte[] makeRelatorioMensal(UUID empresaId, String mesAnalise) {
        var empresa = empresaRepository.findByIdOrThr(empresaId);
        Integer[] anoMes = DateTimeUtils.splitAnoMonth(mesAnalise);
        LocalDateTime inicio = DateTimeUtils.getFirstDayOfMonth(anoMes);
        LocalDateTime fim = DateTimeUtils.getLastDayOfMonth(anoMes);
        List<ViagemModel> viagens = tempoViagemService.findViagensFromEmpresa(empresa, inicio, fim);

        HashMap<Integer, Integer> saidasIdNPassagens = new HashMap<>(), destinosIdNPassagens = new HashMap<>();
        List<PassagemModel> passagens;
        RelatorioModel relatorio = new RelatorioModel(empresa);

        HashMap<String, HashMetodoPagamentoValor> pagamentosWeb = new HashMap<>(), pagamentosNaoWeb = new HashMap<>();
        for (TipoPagamento metodo : TipoPagamento.values()) {
            pagamentosWeb.put(metodo.toString(), new HashMetodoPagamentoValor(metodo.toString(), 0.0));
            pagamentosNaoWeb.put(metodo.toString(), new HashMetodoPagamentoValor(metodo.toString(), 0.0));
        }


        for (ViagemModel viagem : viagens) {
            relatorio.nViagens++;
            if (viagem.isCancelado())
                relatorio.nViagensCanceladas++;

            for (PrecoModel preco : viagem.getPrecos()) {
                passagens = passagemRepository.getPassagensPagas(preco.getId());
                classificarPassagemFromPreco(passagens, saidasIdNPassagens, destinosIdNPassagens, pagamentosWeb,
                        pagamentosNaoWeb, relatorio);
            }
        }

        List<LugarModel> saidas = lugarRepository.findAllById(saidasIdNPassagens.keySet());
        List<LugarModel> destinos = lugarRepository.findAllById(destinosIdNPassagens.keySet());

        ordenarLugares(saidas, saidasIdNPassagens);
        ordenarLugares(destinos, destinosIdNPassagens);

        relatorio.setValorArrecadadoWeb(getValorTotalArrecadado(pagamentosWeb));
        relatorio.setValorArrecadadoNaoWeb(getValorTotalArrecadado(pagamentosNaoWeb));
        relatorio.setDinheiroPorMetodoNaoWeb(pagamentosNaoWeb);
        relatorio.setDinheiroPorMetodoWeb(pagamentosWeb);
        relatorio.setNMes(inicio.getMonthValue());
        relatorio.setNAno(inicio.getYear());

        List<CidadeTHModel> saidasThModels = new ArrayList<>(), destinosThModels = new ArrayList<>();
        List<MetodoTHModel> metodos = new ArrayList<>();
        for (LugarModel saida : saidas)
            saidasThModels.add(new CidadeTHModel(saida.getCidade().getNome(), saidasIdNPassagens.get(saida.getId())));
        for (LugarModel destino : destinos)
            destinosThModels
                    .add(new CidadeTHModel(destino.getCidade().getNome(), destinosIdNPassagens.get(destino.getId())));

        for (TipoPagamento value : TipoPagamento.values()) {
            metodos.add(
                    new MetodoTHModel(value.toString(), relatorio.getDinheiroPorMetodoWeb().get(value.toString()).valor,
                            relatorio.getDinheiroPorMetodoNaoWeb().get(value.toString()).valor));
        }

        return generatePdfFromHtml(relatorio, saidasThModels, destinosThModels, metodos);
    }

    public void ordenarLugares(List<LugarModel> lugares, HashMap<Integer, Integer> lugaresNPassagens) {
        lugares.sort(Comparator.comparingInt(l -> lugaresNPassagens.get(l.getId())));
    }

    public void classificarPassagemFromPreco(List<PassagemModel> passagens, HashMap<Integer, Integer> saidasId,
                                             HashMap<Integer, Integer> destinosId, HashMap<String, HashMetodoPagamentoValor> pagamentosWeb,
                                             HashMap<String, HashMetodoPagamentoValor> pagamentosNaoWeb, RelatorioModel relatorio) {

        relatorio.nPassagensTotal += passagens.size();
        for (PassagemModel passagem : passagens) {
            addValueInHashMap(saidasId, passagem.getSaida().getId());
            addValueInHashMap(destinosId, passagem.getDestino().getId());
            if (passagem.getFaturaReembolsoId() != null) {
                relatorio.nPassagensCanceladas++;
                continue;
            }
            if (passagem.getCompradoWeb()) {
                pagamentosWeb.get(passagem.getMetodoPagamento().toString()).valor += passagem.getPrecoPago().doubleValue();
                if (passagem.getEmDinheiro())
                    emailService.mandarEmail("vargasaveo248@gmail.com", "Web - Erro de Processamento",
                            "Existe uma passagem que foi comprada em dinheiro");
            } else {
                pagamentosNaoWeb.get(passagem.getMetodoPagamento().toString()).valor += passagem.getPrecoPago().doubleValue();
            }
        }
    }

    public double getValorTotalArrecadado(HashMap<String, HashMetodoPagamentoValor> pagamento) {
        double sum = 0;
        for (HashMetodoPagamentoValor value : pagamento.values())
            sum += value.valor;
        return sum;
    }

    public void addValueInHashMap(HashMap<Integer, Integer> hashMap, Integer key) {
        Integer auxMap = hashMap.get(key);
        if (auxMap == null)
            hashMap.put(key, 0);
        else
            hashMap.put(key, auxMap + 1);
    }

    public byte[] generatePdfFromHtml(RelatorioModel relatorio, List<CidadeTHModel> saidasThModels,
                                      List<CidadeTHModel> destinosThModels, List<MetodoTHModel> metodos) {
        var context = new Context();
        context.setVariable("empresaNome", relatorio.getEmpresa().getNome());
        context.setVariable("nMes", relatorio.getNMes());
        context.setVariable("nAno", relatorio.getNAno());
        context.setVariable("nViagens", relatorio.getNViagens());
        context.setVariable("nViagensCanceladas", relatorio.getNViagensCanceladas());
        context.setVariable("nPassagensVendidas", relatorio.getNPassagensTotal());
        context.setVariable("nPassagensCanceladas", relatorio.getNPassagensCanceladas());
        context.setVariable("saidas", saidasThModels);
        context.setVariable("destinos", destinosThModels);
        context.setVariable("metodos", metodos);
        context.setVariable("valorArrecadadoWeb", relatorio.getValorArrecadadoWeb());
        context.setVariable("valorArrecadadoNaoWeb", relatorio.getValorArrecadadoNaoWeb());
        context.setVariable("valorTotal", relatorio.getValorArrecadadoNaoWeb() + relatorio.getValorArrecadadoWeb());
        return pdfThymeleaf.generatePDFByTemplate("empresa/relatorio", context, PageSize.A4);
    }
}