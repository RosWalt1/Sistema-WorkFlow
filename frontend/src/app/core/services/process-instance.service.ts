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
  currentNodeId?: string | null;
  currentNodeName?: string | null;
  estado: 'EN_PROCESO' | 'FINALIZADO' | 'CANCELADO';
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

  listar(): Observable<ProcessInstance[]> {
    return this.http.get<ProcessInstance[]>(this.apiUrl);
  }

  obtenerPorId(id: string): Observable<ProcessInstance> {
    return this.http.get<ProcessInstance>(`${this.apiUrl}/${id}`);
  }

  iniciarDesdeAgente(request: StartProcessFromAgentRequest): Observable<ProcessInstance> {
    return this.http.post<ProcessInstance>(`${this.apiUrl}/start-from-agent`, request);
  }
}