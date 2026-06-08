package com.workflow.controller;

import com.workflow.dto.DynamicReportRequest;
import com.workflow.dto.DynamicReportResponse;
import com.workflow.dto.ReportExportRequest;
import com.workflow.model.ReportFormat;
import com.workflow.service.DynamicReportService;
import com.workflow.service.ExcelReportExporter;
import com.workflow.service.PdfReportExporter;
import com.workflow.service.WordReportExporter;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final DynamicReportService dynamicReportService;
    private final PdfReportExporter pdfReportExporter;
    private final ExcelReportExporter excelReportExporter;
    private final WordReportExporter wordReportExporter;

    public ReportController(
            DynamicReportService dynamicReportService,
            PdfReportExporter pdfReportExporter,
            ExcelReportExporter excelReportExporter,
            WordReportExporter wordReportExporter
    ) {
        this.dynamicReportService = dynamicReportService;
        this.pdfReportExporter = pdfReportExporter;
        this.excelReportExporter = excelReportExporter;
        this.wordReportExporter = wordReportExporter;
    }

    @PostMapping("/dynamic")
    public DynamicReportResponse generateDynamicReport(
            @Valid @RequestBody DynamicReportRequest request
    ) {
        return dynamicReportService.generateDynamicReport(request);
    }

    @PostMapping("/export")
    public ResponseEntity<byte[]> exportReport(
            @Valid @RequestBody ReportExportRequest request
    ) {
        DynamicReportResponse report = dynamicReportService.buildReport(
                request.getIntent(),
                request.getFormat(),
                request.getTitle()
        );

        byte[] fileContent;
        String fileName;
        String contentType;

        if (request.getFormat() == ReportFormat.PDF) {
            fileContent = pdfReportExporter.export(report);
            fileName = "reporte-dinamico.pdf";
            contentType = MediaType.APPLICATION_PDF_VALUE;
        } else if (request.getFormat() == ReportFormat.EXCEL) {
            fileContent = excelReportExporter.export(report);
            fileName = "reporte-dinamico.xlsx";
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else if (request.getFormat() == ReportFormat.WORD) {
            fileContent = wordReportExporter.export(report);
            fileName = "reporte-dinamico.docx";
            contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else {
            throw new RuntimeException("Formato de exportación no soportado");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(fileContent);
    }
}