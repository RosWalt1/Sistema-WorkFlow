import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CompleteTaskRequest {
  userId: string;
  formData: Record<string, unknown>;
  respuestas: Record<string, unknown>;
}

export interface WorkflowTask {
  id?: string;
  processInstanceId: string;
  policyId: string;
  nodeId: string;
  nodeName: string;
  assignedTo: string;
  estado: 'PENDIENTE' | 'COMPLETADA' | 'CANCELADA';
  fechaCreacion?: string;
  fechaFinalizacion?: string | null;
}

@Injectable({
  providedIn: 'root'
})
export class TaskService {

  private apiUrl = 'http://localhost:8080/api/tasks';

  constructor(private http: HttpClient) {}

  listar(): Observable<WorkflowTask[]> {
    return this.http.get<WorkflowTask[]>(this.apiUrl);
  }

  listarPendientes(): Observable<WorkflowTask[]> {
    return this.http.get<WorkflowTask[]>(`${this.apiUrl}/pending`);
  }

  listarPorProceso(processInstanceId: string): Observable<WorkflowTask[]> {
    return this.http.get<WorkflowTask[]>(`${this.apiUrl}/process/${processInstanceId}`);
  }

  completar(id: string, request: CompleteTaskRequest): Observable<WorkflowTask> {
    return this.http.post<WorkflowTask>(`${this.apiUrl}/${id}/complete`, request);
  }
}