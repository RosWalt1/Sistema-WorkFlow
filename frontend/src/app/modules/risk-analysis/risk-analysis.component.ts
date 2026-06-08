import { Component, OnInit } from '@angular/core';
import { NgClass, NgFor, NgIf } from '@angular/common';

import {
  Anomaly,
  BottleneckResponse,
  RiskAnalysisService,
  RiskDashboardResponse
} from '../../core/services/risk-analysis.service';

@Component({
  selector: 'app-risk-analysis',
  standalone: true,
  imports: [NgIf, NgFor, NgClass],
  templateUrl: './risk-analysis.component.html'
})
export class RiskAnalysisComponent implements OnInit {

  dashboard: RiskDashboardResponse | null = null;

  anomalies: Anomaly[] = [];

  bottlenecks: BottleneckResponse[] = [];

  loading = false;

  errorMessage = '';

  constructor(
    private riskService: RiskAnalysisService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.errorMessage = '';

    this.riskService.getDashboard().subscribe({
      next: (response) => {
        this.dashboard = response;
      }
    });

    this.riskService.getAnomalies().subscribe({
      next: (response) => {
        this.anomalies = response;
      }
    });

    this.riskService.getBottlenecks().subscribe({
      next: (response) => {
        this.bottlenecks = response;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'No se pudo cargar el motor de riesgo.';
        this.loading = false;
      }
    });
  }

  getSeverityClass(severity: string): string {

    switch (severity) {

      case 'CRITICA':
        return 'bg-red-500/10 text-red-300 border-red-500/30';

      case 'ALTA':
        return 'bg-orange-500/10 text-orange-300 border-orange-500/30';

      case 'MEDIA':
        return 'bg-yellow-500/10 text-yellow-300 border-yellow-500/30';

      default:
        return 'bg-emerald-500/10 text-emerald-300 border-emerald-500/30';
    }
  }

  formatDate(date?: string): string {

    if (!date) {
      return 'Sin fecha';
    }

    return new Date(date).toLocaleString();
  }
}