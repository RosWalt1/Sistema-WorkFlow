import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface WorkflowNode {
  id: string;
  tipo: 'INICIO' | 'ACTIVIDAD' | 'DECISION' | 'FIN';
  nombre: string;
  calle: string;
  posicionX: number;
  posicionY: number;
  condiciones: string;
}

export interface WorkflowConnection {
  origen: string;
  destino: string;
  condicion: string;
}

export interface DiagramJson {
  nodes: WorkflowNode[];
  connections: WorkflowConnection[];
}

export interface BusinessPolicy {
  id?: string;
  nombre: string;
  descripcion: string;
  estado?: 'BORRADOR' | 'ACTIVO' | 'INACTIVO';
  version?: number;
  diagramaJson: DiagramJson;
  lockedBy?: string;
  lockedAt?: string;
}

@Injectable({
  providedIn: 'root'
})
export class BusinessPolicyService {

  private apiUrl = 'http://localhost:8080/api/business-policies';

  constructor(private http: HttpClient) {}

  listar(): Observable<BusinessPolicy[]> {
    return this.http.get<BusinessPolicy[]>(this.apiUrl);
  }

  obtenerPorId(id: string): Observable<BusinessPolicy> {
    return this.http.get<BusinessPolicy>(`${this.apiUrl}/${id}`);
  }

  crear(policy: BusinessPolicy): Observable<BusinessPolicy> {
    return this.http.post<BusinessPolicy>(this.apiUrl, policy);
  }

  actualizar(id: string, policy: BusinessPolicy): Observable<BusinessPolicy> {
    return this.http.put<BusinessPolicy>(`${this.apiUrl}/${id}`, policy);
  }

  eliminar(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  activar(id: string): Observable<BusinessPolicy> {
    return this.http.patch<BusinessPolicy>(`${this.apiUrl}/${id}/activate`, {});
  }
}