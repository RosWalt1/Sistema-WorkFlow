package com.workflow.service;

import com.workflow.dto.DynamicReportResponse;
import com.workflow.dto.ReportColumnResponse;
import com.workflow.dto.ReportRowResponse;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class WordReportExporter {

    public byte[] export(DynamicReportResponse report) {
        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            XWPFParagraph titleParagraph = document.createParagraph();
            XWPFRun titleRun = titleParagraph.createRun();
            titleRun.setBold(true);
            titleRun.setFontSize(16);
            titleRun.setText(report.getTitle());

            XWPFParagraph infoParagraph = document.createParagraph();
            XWPFRun infoRun = infoParagraph.createRun();
            infoRun.setText("Generado: " + report.getGeneratedAt());

            XWPFTable table = document.createTable(
                    Math.max(report.getRows().size() + 1, 2),
                    Math.max(report.getColumns().size(), 1)
            );

            for (int i = 0; i < report.getColumns().size(); i++) {
                table.getRow(0).getCell(i).setText(report.getColumns().get(i).getLabel());
            }

            for (int rowIndex = 0; rowIndex < report.getRows().size(); rowIndex++) {
                ReportRowResponse dataRow = report.getRows().get(rowIndex);

                for (int colIndex = 0; colIndex < report.getColumns().size(); colIndex++) {
                    ReportColumnResponse column = report.getColumns().get(colIndex);
                    Object value = dataRow.getValues().get(column.getKey());
                    table.getRow(rowIndex + 1).getCell(colIndex).setText(value == null ? "" : value.toString());
                }
            }

            document.write(outputStream);
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el archivo Word", e);
        }
    }
}