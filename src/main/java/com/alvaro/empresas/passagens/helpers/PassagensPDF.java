package com.alvaro.empresas.passagens.helpers;

import com.alvaro.empresas.passagens.enums.TipoPagamento;
import com.alvaro.empresas.passagens.models.PassagemModel;
import com.alvaro.empresas.passagens.paradas.models.ParadaModel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PassagensPDF {
    private PDDocument documento;
    private PDRectangle retangulo;
    private float largura = 0f;
    private float inicioX = 0f;
    private float inicioY = 0f;
    private final PDType1Font fonteNormal;
    private final PDType1Font fonteNegrito;
    private final int tamanhoFontePadrao = 8;

    public PassagensPDF() {
        documento = new PDDocument();
        retangulo = new PDRectangle(5.08f * 72 / 2.54f, 15.0f * 72 / 2.54f); // 12 cm largura e altura padrão A4
        float margem = (0.5f * 72) / 2.54f; // 1 cm de margem em cada lado
        largura = retangulo.getWidth() - 2 * margem;
        inicioX = retangulo.getLowerLeftX() + margem;
        inicioY = retangulo.getUpperRightY() - margem;
        fonteNormal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        fonteNegrito = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    }

    private void addRowBold(PDPageContentStream fluxoConteudo, String conteudo, int tamanhoFonte) throws IOException {
        fluxoConteudo.setFont(fonteNegrito, tamanhoFonte);
        fluxoConteudo.showText(conteudo);
        fluxoConteudo.newLine();
        fluxoConteudo.setFont(fonteNormal, tamanhoFontePadrao);
    }

    public void showLongTextAndNewLine(PDPageContentStream fluxoConteudo, String texto) throws IOException {
        List<String> linhas = this.getLines(texto);
        for (String linha : linhas) {
            fluxoConteudo.showText(linha);
            fluxoConteudo.newLine();
        }
    }

    private void addParada(PDPageContentStream fluxoConteudo, String titulo, ParadaModel parada) throws IOException {
        addRowBold(fluxoConteudo, titulo, tamanhoFontePadrao);
        showLongTextAndNewLine(fluxoConteudo, "Cidade: " + parada.getLugar().getCidade().getNome());
        showLongTextAndNewLine(fluxoConteudo, "Departamento: " + parada.getLugar().getCidade().getDepartamento().getNome());
        showLongTextAndNewLine(fluxoConteudo, "Lugar: " + parada.getLugar().getNome());
    }

    public void addPassagem(PassagemModel modelo, String nomeEmpresa, TipoPagamento metodo) throws IOException {
        PDPage pagina = new PDPage(retangulo);
        documento.addPage(pagina);
        var saida = modelo.getSaida();
        var destino = modelo.getDestino();


        PDPageContentStream fluxoConteudo = new PDPageContentStream(documento, pagina);
        fluxoConteudo.beginText();
        fluxoConteudo.setFont(fonteNormal, tamanhoFontePadrao);
        fluxoConteudo.setLeading(14.5f);
        fluxoConteudo.newLineAtOffset(inicioX, inicioY);

        addRowBold(fluxoConteudo, "Empresa: " + nomeEmpresa, tamanhoFontePadrao + 1);
        addRowBold(fluxoConteudo, "Dados da Viagem", tamanhoFontePadrao + 1);

        var dataHora = DateAuxiliarFunctions.getDataHoraFromDateTime(saida.getDataHora());
        fluxoConteudo.showText("Data e Hora: " + dataHora.data() + " - " + dataHora.hora());
        fluxoConteudo.newLine();
        fluxoConteudo.showText("Corredor: " + saida.getPlataforma());
        fluxoConteudo.newLine();
        fluxoConteudo.showText("Andar: andar " + modelo.getPreco().getNPiso());
        fluxoConteudo.newLine();
        fluxoConteudo.showText("Assento: " + modelo.getNAssento());
        fluxoConteudo.newLine();

        addParada(fluxoConteudo, "Origem", saida);
        addParada(fluxoConteudo, "Destino", destino);

        addRowBold(fluxoConteudo, "Dados do Passageiro", tamanhoFontePadrao + 1);
        showLongTextAndNewLine(fluxoConteudo, "Nome: " + modelo.getNome());
        showLongTextAndNewLine(fluxoConteudo, "Documento: " + modelo.getDocumento());

        addRowBold(fluxoConteudo, "Dados do Pagamento", tamanhoFontePadrao + 1);
        fluxoConteudo.showText("Preço: " + modelo.getPrecoPago().toString() + " Bs");
        fluxoConteudo.newLine();
        fluxoConteudo.showText("Método de Pagamento: " + metodo.toString());
        fluxoConteudo.newLine();
        fluxoConteudo.showText("Desconto: 0 Bs");

        fluxoConteudo.endText();
        fluxoConteudo.close();
    }

    public byte[] closePdfAndToBytes() throws IOException {
        ByteArrayOutputStream fluxoSaida = new ByteArrayOutputStream();
        documento.save(fluxoSaida);
        documento.close();
        return fluxoSaida.toByteArray();
    }

    private List<String> getLines(String texto) throws IOException {
        List<String> linhas = new ArrayList<>();
        String[] palavras = texto.split(" ");
        StringBuilder linha = new StringBuilder();

        for (String palavra : palavras) {
            if (this.fonteNormal.getStringWidth(linha.toString() + " " + palavra) / 1000 * this.tamanhoFontePadrao > this.largura) {
                linhas.add(linha.toString());
                linha = new StringBuilder();
            }
            if (!linha.isEmpty()) linha.append(" ");
            linha.append(palavra);
        }
        linhas.add(linha.toString());

        return linhas;
    }

}