import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface StartProcessFromAgentRequest {
  policyId: string;
  clienteId: string;
  userId: string;
  initialData: Record<string, unknown>;
}

export interface WorkflowHistory {
  fecha: string;
  accion: string;
  detalle: string;
  nodeId?: string | null;
}

export interface ProcessInstance {
  id?: string;
  policyId: string;
  clienteId?: string;
  estado: 'EN_PROCESO' | 'COMPLETADO' | 'CANCELADO';
  fechaInicio?: string;
  fechaFin?: string;
  historial: WorkflowHistory[];
}

@Injectable({
  providedIn: 'root'
})
export class ProcessInstanceService {

  private apiUrl = 'http://localhost:8080/api/process';

  constructor(private http: HttpClient) {}

  iniciarDesdeAgente(request: StartProcessFromAgentRequest): Observable<ProcessInstance> {
    return this.http.post<ProcessInstance>(`${this.apiUrl}/start-from-agent`, request);
  }
}