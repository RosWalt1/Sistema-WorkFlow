package com.workflow.service;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.workflow.dto.DynamicReportResponse;
import com.workflow.dto.ReportColumnResponse;
import com.workflow.dto.ReportRowResponse;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfReportExporter {

    public byte[] export(DynamicReportResponse report) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, outputStream);

            document.open();

            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            document.add(new Paragraph(report.getTitle(), titleFont));
            document.add(new Paragraph("Generado: " + report.getGeneratedAt()));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(Math.max(report.getColumns().size(), 1));
            table.setWidthPercentage(100);

            for (ReportColumnResponse column : report.getColumns()) {
                PdfPCell cell = new PdfPCell(new Paragraph(column.getLabel()));
                table.addCell(cell);
            }

            for (ReportRowResponse row : report.getRows()) {
                for (ReportColumnResponse column : report.getColumns()) {
                    Object value = row.getValues().get(column.getKey());
                    table.addCell(value == null ? "" : value.toString());
                }
            }

            document.add(table);
            document.close();

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el archivo PDF", e);
        }
    }
}