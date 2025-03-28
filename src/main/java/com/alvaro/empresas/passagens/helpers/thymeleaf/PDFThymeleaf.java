package com.alvaro.empresas.passagens.helpers.thymeleaf;

import com.alvaro.empresas.passagens.configuracoes.exceptions.CustomExceptions.RestRuntimeException;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.ByteArrayOutputStream;

@Service
@Slf4j
public class PDFThymeleaf {
    @Value("${api.thymeleaf.templates.locale}")
    private String classpath;

    public byte[] generatePDFByTemplate(String templateName, Context context, PageSize pageSize) {
        String html = "";
        try {
            html = parseThymeleafTemplate(templateName, context);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RestRuntimeException(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro na solicitação");
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);

        PdfDocument pdfDocument = new PdfDocument(writer);
        pdfDocument.setDefaultPageSize(pageSize);
        ConverterProperties converterProperties = new ConverterProperties();
        HtmlConverter.convertToPdf(html, pdfDocument, converterProperties);
        pdfDocument.close();
        return byteArrayOutputStream.toByteArray();
    }

    private String parseThymeleafTemplate(String templateName, Context context) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine.process(classpath + templateName, context);
    }
}
