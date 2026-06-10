import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AgentAnalyzeRequest {
  mensaje: string;
  clienteId: string;
}

export interface AgentAnalyzeResponse {
  mensajeOriginal: string;
  intention: string;
  recommendedPolicyId?: string;
  recommendedPolicyName?: string;
  descripcion?: string;
  requiredDocuments: string[];
  estimatedDuration?: string;
  recommendedFor?: string;
  score: number;
  found: boolean;
}

export interface AgentStartRequest {
  policyId: string;
  clienteId: string;
  mensaje: string;
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

export interface AgentInteraction {
  id?: string;
  userId: string;
  clienteId: string;
  mensaje: string;
  intention: string;
  recommendedPolicyId?: string;
  recommendedPolicyName?: string;
  score?: number;
  processStarted: boolean;
  processInstanceId?: string;
  fecha: string;
}

@Injectable({
  providedIn: 'root'
})
export class AgentService {
  private apiUrl = 'http://localhost:8080/api/agent';

  constructor(private http: HttpClient) {}

  analyze(request: AgentAnalyzeRequest): Observable<AgentAnalyzeResponse> {
    return this.http.post<AgentAnalyzeResponse>(`${this.apiUrl}/analyze`, request);
  }

  start(request: AgentStartRequest): Observable<ProcessInstance> {
    return this.http.post<ProcessInstance>(`${this.apiUrl}/start`, request);
  }

  history(): Observable<AgentInteraction[]> {
    return this.http.get<AgentInteraction[]>(`${this.apiUrl}/history`);
  }
}