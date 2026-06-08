import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type ReportIntent =
  | 'TASKS_PENDING'
  | 'TASKS_COMPLETED'
  | 'PROCESSES_ACTIVE'
  | 'PROCESSES_FINISHED'
  | 'PROCESSES_CANCELLED'
  | 'ANOMALIES_OPEN'
  | 'ANOMALIES_CRITICAL'
  | 'BOTTLENECKS'
  | 'MOST_USED_POLICY'
  | 'DELAYED_TASKS'
  | 'UNKNOWN';

export type ReportFormat = 'JSON' | 'PDF' | 'EXCEL' | 'WORD';

export interface DynamicReportRequest {
  query: string;
}

export interface ReportExportRequest {
  intent: ReportIntent;
  format: ReportFormat;
  title?: string;
}

export interface ReportColumnResponse {
  key: string;
  label: string;
}

export interface ReportRowResponse {
  values: Record<string, unknown>;
}

export interface DynamicReportResponse {
  title: string;
  originalQuery: string;
  intent: ReportIntent;
  detectedFormat: ReportFormat;
  generatedAt: string;
  columns: ReportColumnResponse[];
  rows: ReportRowResponse[];
}

@Injectable({
  providedIn: 'root'
})
export class ReportsService {

  private apiUrl = 'http://localhost:8080/api/reports';

  constructor(private http: HttpClient) {}

  generateDynamicReport(query: string): Observable<DynamicReportResponse> {
    return this.http.post<DynamicReportResponse>(`${this.apiUrl}/dynamic`, { query });
  }

  exportReport(request: ReportExportRequest): Observable<Blob> {
    return this.http.post(`${this.apiUrl}/export`, request, {
      responseType: 'blob'
    });
  }
}