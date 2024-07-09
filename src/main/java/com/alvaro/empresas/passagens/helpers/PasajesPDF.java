package com.alvaro.empresas.passagens.helpers;

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
import java.time.LocalDateTime;

public class PasajesPDF {
    private PDDocument document;
    private PDRectangle rectangle;
    private float width = 0f;
    private float startX = 0f;
    private float startY = 0f;
    private final PDType1Font standardFont;
    private final PDType1Font standardFontBold;
    private final int standardFontSize = 12;

    public PasajesPDF() {
        document = new PDDocument();
        rectangle = new PDRectangle(12 * 72 / 2.54f, 21.0f * 72 / 2.54f); // 12 cm largura e altura padrão A4
        float margin = 72 / 2.54f; // 1 cm de margem em cada lado
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

    private void addParada(PDPageContentStream contentStream, String title, ParadaModel parada) throws IOException {
        addRowBold(contentStream, title, standardFontSize);
        contentStream.showText("Ciudad: " + parada.getLugar().getCiudad().getNombre());
        contentStream.newLine();
        contentStream.showText("Departamento: " + parada.getLugar().getCiudad().getDepartamento().getNombre());
        contentStream.newLine();
        contentStream.showText("Lugar: " + parada.getLugar().getNombre());
        contentStream.newLine();
    }

    public void addPasaje(PasajeModel model, String empresaName, ParadaModel salida, ParadaModel destino) throws IOException {
        PDPage page = new PDPage(rectangle);
        document.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.beginText();
        contentStream.setFont(standardFont, 12);
        contentStream.setLeading(14.5f);
        contentStream.newLineAtOffset(startX, startY);

        addRowBold(contentStream, "Empresa: " + empresaName, 13);
        addRowBold(contentStream, "Datos del Viaje", 13);
        addParada(contentStream, "Origen", salida);
        addParada(contentStream, "Destino", destino);
        var dataHora = FormatarDataHora.getDataHoraToString(salida.getDataHora());
        contentStream.showText("Fecha y Hora: " + dataHora.data() + " - " + dataHora.hora());
        contentStream.newLine();
        contentStream.showText("Piso: piso " + model.getPrecio().getNPiso());
        contentStream.newLine();
        contentStream.showText("Silla: " + model.getNSilla());
        contentStream.newLine();
        addRowBold(contentStream, "Datos del Pasajero", 13);
        contentStream.showText("Nombre: " + model.getNombre());
        contentStream.newLine();
        contentStream.showText("Carnet: " + model.getCarnet());
        contentStream.newLine();
        contentStream.endText();
        contentStream.close();
    }

    public byte[] closeAndGetBytes() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save(outputStream);
        document.close();
        return outputStream.toByteArray();
    }
}
