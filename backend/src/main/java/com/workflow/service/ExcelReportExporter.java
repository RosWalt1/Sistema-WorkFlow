package com.workflow.service;

import com.workflow.dto.DynamicReportResponse;
import com.workflow.dto.ReportColumnResponse;
import com.workflow.dto.ReportRowResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class ExcelReportExporter {

    public byte[] export(DynamicReportResponse report) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Reporte");

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < report.getColumns().size(); i++) {
                headerRow.createCell(i).setCellValue(report.getColumns().get(i).getLabel());
            }

            int rowIndex = 1;
            for (ReportRowResponse dataRow : report.getRows()) {
                Row row = sheet.createRow(rowIndex++);

                for (int i = 0; i < report.getColumns().size(); i++) {
                    ReportColumnResponse column = report.getColumns().get(i);
                    Object value = dataRow.getValues().get(column.getKey());
                    row.createCell(i).setCellValue(value == null ? "" : value.toString());
                }
            }

            for (int i = 0; i < report.getColumns().size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el archivo Excel", e);
        }
    }
}