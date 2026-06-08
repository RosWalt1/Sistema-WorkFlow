import { Component, NgZone } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
    DynamicReportResponse,
    ReportFormat,
    ReportsService
} from '../../core/services/reports.service';

@Component({
    selector: 'app-reports',
    standalone: true,
    imports: [NgIf, NgFor, FormsModule],
    templateUrl: './reports.component.html'
})
export class ReportsComponent {

    query = '';

    report: DynamicReportResponse | null = null;

    loading = false;

    errorMessage = '';

    recognition: any;

    speechSupported = false;

    constructor(
        private reportsService: ReportsService,
        private ngZone: NgZone
    ) {
        const SpeechRecognition =
            (window as any).SpeechRecognition ||
            (window as any).webkitSpeechRecognition;

        if (SpeechRecognition) {
            this.speechSupported = true;

            this.recognition = new SpeechRecognition();

this.recognition.lang = 'es-ES';
this.recognition.continuous = true;
this.recognition.interimResults = false;

this.recognition.onstart = () => {
  console.log('MICROFONO INICIADO');
};

this.recognition.onend = () => {
  console.log('MICROFONO FINALIZADO');
};

this.recognition.onerror = (event: any) => {
  console.error('ERROR VOZ:', event);
};

this.recognition.onresult = (event: any) => {
  let transcript = '';

  for (let i = event.resultIndex; i < event.results.length; i++) {
    transcript += event.results[i][0].transcript;
  }

  console.log('TEXTO:', transcript);

  this.ngZone.run(() => {
    this.query = transcript.trim();
  });

  this.recognition.stop();
};
        }
    }

    generateReport(): void {
        if (!this.query.trim()) {
            return;
        }

        this.loading = true;
        this.errorMessage = '';

        this.reportsService.generateDynamicReport(this.query).subscribe({
            next: (response) => {
                this.report = response;
                this.loading = false;
            },
            error: () => {
                this.errorMessage = 'No se pudo generar el reporte.';
                this.loading = false;
            }
        });
    }

    startVoiceRecognition(): void {
        if (!this.recognition) {
            this.errorMessage = 'Tu navegador no soporta reconocimiento de voz.';
            return;
        }

        this.errorMessage = '';

        try {
            this.recognition.start();
        } catch {
            this.errorMessage = 'El reconocimiento de voz ya está activo. Espera unos segundos e intenta de nuevo.';
        }
    }

    export(format: ReportFormat): void {

        if (!this.report) {
            return;
        }

        this.reportsService.exportReport({
            intent: this.report.intent,
            format,
            title: this.report.title
        }).subscribe(blob => {

            const url = window.URL.createObjectURL(blob);

            const link = document.createElement('a');

            link.href = url;

            if (format === 'PDF') {
                link.download = 'reporte.pdf';
            } else if (format === 'EXCEL') {
                link.download = 'reporte.xlsx';
            } else {
                link.download = 'reporte.docx';
            }

            link.click();

            window.URL.revokeObjectURL(url);
        });
    }

    getColumnKeys(): string[] {
        if (!this.report) {
            return [];
        }

        return this.report.columns.map(column => column.key);
    }
}