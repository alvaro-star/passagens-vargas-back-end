package com.alvaro.empresas.passagens.helpers;

import com.alvaro.empresas.passagens.enums.MetodoPagamentoEnum;
import com.alvaro.empresas.passagens.models.PasajeModel;
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

public class PasajesPDF {
    private PDDocument document;
    private PDRectangle rectangle;
    private float width = 0f;
    private float startX = 0f;
    private float startY = 0f;
    private final PDType1Font standardFont;
    private final PDType1Font standardFontBold;
    private final int standardFontSize = 8;

    public PasajesPDF() {
        document = new PDDocument();
        rectangle = new PDRectangle(5.08f * 72 / 2.54f, 21.0f * 72 / 2.54f); // 12 cm largura e altura padrão A4
        float margin = (0.5f * 72) / 2.54f; // 1 cm de margem em cada lado
        width = rectangle.getWidth() - 2 * margin;
        startX = rectangle.getLowerLeftX() + margin;
        startY = rectangle.getUpperRightY() - margin;
        standardFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
        standardFontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    }

    private void addRowBold(PDPageContentStream contentStream, String content, int fontSize) throws IOException {
        contentStream.setFont(standardFontBold, fontSize);
        contentStream.showText(content);
        contentStream.newLine();
        contentStream.setFont(standardFont, standardFontSize);
    }

    public void showLongTextAndNewLine(PDPageContentStream contentStream, String text) throws IOException {
        List<String> lines = this.getLines(text);
        for (String line : lines) {
            contentStream.showText(line);
            contentStream.newLine();
        }
    }

    private void addParada(PDPageContentStream contentStream, String title, ParadaModel parada) throws IOException {
        addRowBold(contentStream, title, standardFontSize);
        showLongTextAndNewLine(contentStream, "Ciudad: " + parada.getLugar().getCiudad().getNombre());
        showLongTextAndNewLine(contentStream, "Departamento: " + parada.getLugar().getCiudad().getDepartamento().getNombre());
        showLongTextAndNewLine(contentStream, "Lugar: " + parada.getLugar().getNombre());
    }

    public void addPasaje(PasajeModel model, String empresaName, ParadaModel salida, ParadaModel destino, MetodoPagamentoEnum metodo) throws IOException {
        PDPage page = new PDPage(rectangle);
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.beginText();
        contentStream.setFont(standardFont, standardFontSize);
        contentStream.setLeading(14.5f);
        contentStream.newLineAtOffset(startX, startY);

        addRowBold(contentStream, "Empresa: " + empresaName, standardFontSize + 1);
        addRowBold(contentStream, "Datos del Viaje", standardFontSize + 1);

        var dataHora = DateAuxiliarFunctions.getDataHoraToString(salida.getDataHora());
        contentStream.showText("Fecha y Hora: " + dataHora.data() + " - " + dataHora.hora());
        contentStream.newLine();
        contentStream.showText("Carril: " + salida.getPlataforma());
        contentStream.newLine();
        contentStream.showText("Piso: piso " + model.getPrecio().getNPiso());
        contentStream.newLine();
        contentStream.showText("Asiento: " + model.getNSilla());
        contentStream.newLine();

        addParada(contentStream, "Origen", salida);
        addParada(contentStream, "Destino", destino);

        addRowBold(contentStream, "Datos del Pasajero", standardFontSize + 1);
        showLongTextAndNewLine(contentStream, "Nombre: " + model.getNombre());
        showLongTextAndNewLine(contentStream, "Carnet: " + model.getCarnet());

        addRowBold(contentStream, "Datos del Pago", standardFontSize + 1);
        contentStream.showText("Precio: " + model.getPrecioPagado().toString() + " Bs");
        contentStream.newLine();
        contentStream.showText("Metodo de Pago: " + metodo.toString());
        contentStream.newLine();
        contentStream.showText("Descuento: 0 Bs");

        contentStream.endText();
        contentStream.close();
    }

    public byte[] closeAndGetBytes() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save(outputStream);
        document.close();
        return outputStream.toByteArray();
    }

    private List<String> getLines(String text) throws IOException {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            if (this.standardFont.getStringWidth(line.toString() + " " + word) / 1000 * this.standardFontSize > this.width) {
                lines.add(line.toString());
                line = new StringBuilder();
            }
            if (!line.isEmpty()) line.append(" ");
            line.append(word);
        }
        lines.add(line.toString());

        return lines;
    }

}
