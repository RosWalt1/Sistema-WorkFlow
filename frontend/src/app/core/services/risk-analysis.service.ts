import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type RiskSeverity = 'BAJA' | 'MEDIA' | 'ALTA' | 'CRITICA';
export type AnomalyStatus = 'ABIERTA' | 'EN_REVISION' | 'RESUELTA' | 'DESCARTADA';

export interface RiskDashboardResponse {
  totalAnomalies: number;
  openAnomalies: number;
  resolvedAnomalies: number;
  lowRisk: number;
  mediumRisk: number;
  highRisk: number;
  criticalRisk: number;
  pendingTasks: number;
  delayedTasks: number;
  activeProcesses: number;
}

export interface Anomaly {
  id: string;
  processId: string;
  taskId?: string | null;
  reglaId: string;
  descripcion: string;
  fecha: string;
  estado: AnomalyStatus;
  severidad: RiskSeverity;
  accionRecomendada: string;
}

export interface ProcessRiskResponse {
  processId: string;
  policyId: string;
  clienteId?: string;
  estado: 'EN_PROCESO' | 'FINALIZADO' | 'CANCELADO';
  currentNodeId?: string | null;
  currentNodeName?: string | null;
  severidad: RiskSeverity;
  descripcionRiesgo: string;
  accionRecomendada: string;
  totalTasks: number;
  pendingTasks: number;
  completedTasks: number;
  delayedTasks: number;
}

export interface BottleneckResponse {
  assignedTo: string;
  pendingTasks: number;
  severidad: RiskSeverity;
  descripcion: string;
  accionRecomendada: string;
}

export interface RoutePredictionResponse {
  policyId: string;
  rutaEsperada: string[];
  rutaReal: string[];
  desviaciones: string[];
  fechaAnalisis: string;
  recomendacion: string;
}

@Injectable({
  providedIn: 'root'
})
export class RiskAnalysisService {
  private apiUrl = 'http://localhost:8080/api/risk';

  constructor(private http: HttpClient) {}

  getDashboard(): Observable<RiskDashboardResponse> {
    return this.http.get<RiskDashboardResponse>(`${this.apiUrl}/dashboard`);
  }

  getAnomalies(): Observable<Anomaly[]> {
    return this.http.get<Anomaly[]>(`${this.apiUrl}/anomalies`);
  }

  getOpenAnomalies(): Observable<Anomaly[]> {
    return this.http.get<Anomaly[]>(`${this.apiUrl}/anomalies/open`);
  }

  getAnomaliesByProcess(processId: string): Observable<Anomaly[]> {
    return this.http.get<Anomaly[]>(`${this.apiUrl}/anomalies/process/${processId}`);
  }

  analyzeProcessRisk(processId: string): Observable<ProcessRiskResponse> {
    return this.http.get<ProcessRiskResponse>(`${this.apiUrl}/process/${processId}`);
  }

  getBottlenecks(): Observable<BottleneckResponse[]> {
    return this.http.get<BottleneckResponse[]>(`${this.apiUrl}/bottlenecks`);
  }

  analyzeRoute(policyId: string): Observable<RoutePredictionResponse> {
    return this.http.post<RoutePredictionResponse>(`${this.apiUrl}/routes/analyze/${policyId}`, {});
  }
}