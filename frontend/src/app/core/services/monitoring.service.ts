import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface MonitoringSummaryResponse {
  totalProcesses: number;
  processesInProgress: number;
  processesFinished: number;
  processesCancelled: number;
  totalTasks: number;
  pendingTasks: number;
  completedTasks: number;
  cancelledTasks: number;
}

export interface WorkflowHistory {
  fecha: string;
  accion: string;
  detalle: string;
  nodeId?: string | null;
}

export interface TaskMonitoringResponse {
  taskId: string;
  processInstanceId: string;
  policyId: string;
  nodeId: string;
  nodeName: string;
  assignedTo: string;
  estado: 'PENDIENTE' | 'COMPLETADA' | 'CANCELADA';
  fechaCreacion?: string;
  fechaFinalizacion?: string | null;
}

export interface TaskDurationResponse {
  taskId: string;
  nodeId: string;
  nodeName: string;
  assignedTo: string;
  estado: 'PENDIENTE' | 'COMPLETADA' | 'CANCELADA';
  fechaCreacion?: string;
  fechaFinalizacion?: string | null;
  durationMinutes?: number | null;
}

export interface ProcessMonitoringDetailResponse {
  processInstanceId: string;
  policyId: string;
  clienteId?: string;
  currentNodeId?: string | null;
  currentNodeName?: string | null;
  estado: 'EN_PROCESO' | 'FINALIZADO' | 'CANCELADO';
  fechaInicio?: string;
  fechaFin?: string | null;
  totalDurationMinutes?: number | null;
  progressPercentage: number;
  totalTasks: number;
  completedTasks: number;
  pendingTasks: number;
  cancelledTasks: number;
  historial: WorkflowHistory[];
  taskDurations: TaskDurationResponse[];
}

@Injectable({
  providedIn: 'root'
})
export class MonitoringService {

  private apiUrl = 'http://localhost:8080/api/monitoring';

  constructor(private http: HttpClient) {}

  getSummary(): Observable<MonitoringSummaryResponse> {
    return this.http.get<MonitoringSummaryResponse>(`${this.apiUrl}/summary`);
  }

  getAllTasks(): Observable<TaskMonitoringResponse[]> {
    return this.http.get<TaskMonitoringResponse[]>(`${this.apiUrl}/tasks`);
  }

  getPendingTasks(): Observable<TaskMonitoringResponse[]> {
    return this.http.get<TaskMonitoringResponse[]>(`${this.apiUrl}/tasks/pending`);
  }

  getCompletedTasks(): Observable<TaskMonitoringResponse[]> {
    return this.http.get<TaskMonitoringResponse[]>(`${this.apiUrl}/tasks/completed`);
  }

  getProcessDetail(processInstanceId: string): Observable<ProcessMonitoringDetailResponse> {
    return this.http.get<ProcessMonitoringDetailResponse>(
      `${this.apiUrl}/process/${processInstanceId}/detail`
    );
  }
}