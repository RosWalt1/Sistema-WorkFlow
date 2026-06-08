package com.workflow.service;

import com.workflow.model.ReportFormat;
import com.workflow.model.ReportIntent;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Locale;

@Service
public class DynamicReportInterpreterService {

    public ReportIntent detectIntent(String query) {
        String text = normalize(query);

        if (containsAll(text, "tareas", "pendientes")) {
            return ReportIntent.TASKS_PENDING;
        }

        if (containsAll(text, "tareas", "completadas")) {
            return ReportIntent.TASKS_COMPLETED;
        }

        if (containsAny(text, "procesos activos", "procesos en proceso", "tramites activos", "tramites en proceso")) {
            return ReportIntent.PROCESSES_ACTIVE;
        }

        if (containsAny(text, "procesos finalizados", "tramites finalizados")) {
            return ReportIntent.PROCESSES_FINISHED;
        }

        if (containsAny(text, "procesos cancelados", "tramites cancelados", "procesos rechazados", "tramites rechazados")) {
            return ReportIntent.PROCESSES_CANCELLED;
        }

        if (containsAny(text, "anomalias abiertas", "riesgos abiertos")) {
            return ReportIntent.ANOMALIES_OPEN;
        }

        if (containsAny(text, "anomalias criticas", "riesgos criticos")) {
            return ReportIntent.ANOMALIES_CRITICAL;
        }

        if (containsAny(text, "cuellos de botella", "acumulacion de tareas")) {
            return ReportIntent.BOTTLENECKS;
        }

        if (containsAny(text, "politica mas utilizada", "politica mas usada")) {
            return ReportIntent.MOST_USED_POLICY;
        }

        if (containsAny(text, "tareas demoradas", "tareas con retraso", "procesos con retraso", "procesos mas retraso")) {
            return ReportIntent.DELAYED_TASKS;
        }

        return ReportIntent.UNKNOWN;
    }

    public ReportFormat detectFormat(String query) {
        String text = normalize(query);

        if (containsAny(text, "pdf")) {
            return ReportFormat.PDF;
        }

        if (containsAny(text, "excel", "xlsx")) {
            return ReportFormat.EXCEL;
        }

        if (containsAny(text, "word", "docx")) {
            return ReportFormat.WORD;
        }

        return ReportFormat.JSON;
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }

        String lower = value.toLowerCase(Locale.ROOT).trim();

        return Normalizer.normalize(lower, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    private boolean containsAll(String text, String... words) {
        for (String word : words) {
            if (!text.contains(word)) {
                return false;
            }
        }

        return true;
    }

    private boolean containsAny(String text, String... phrases) {
        for (String phrase : phrases) {
            if (text.contains(phrase)) {
                return true;
            }
        }

        return false;
    }
}