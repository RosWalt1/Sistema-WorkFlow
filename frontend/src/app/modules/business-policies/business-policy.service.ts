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
  bpmnXml?: string;

  keywords?: string[];
  requiredDocuments?: string[];
  estimatedDuration?: string;
  recommendedFor?: string;

  lockedBy?: string;
  lockedAt?: string;
}

export interface PolicyVersion {
  id: string;
  policyId: string;
  versionNumber: number;
  policySnapshot?: BusinessPolicy;
  diagramJson?: DiagramJson;
  createdBy: string;
  createdAt: string;
  description?: string;
}

export interface PolicyComment {
  id: string;
  policyId: string;
  userId: string;
  userName?: string;
  message?: string;
  comentario?: string;
  resolved?: boolean;
  createdAt?: string;
  resolvedAt?: string;
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

  lock(id: string, userId: string): Observable<BusinessPolicy> {
    return this.http.post<BusinessPolicy>(`${this.apiUrl}/${id}/lock`, { userId });
  }

  unlock(id: string, userId: string): Observable<BusinessPolicy> {
    return this.http.post<BusinessPolicy>(`${this.apiUrl}/${id}/unlock`, { userId });
  }

  createVersion(id: string, createdBy: string, description: string): Observable<PolicyVersion> {
    return this.http.post<PolicyVersion>(`${this.apiUrl}/${id}/versions`, {
      createdBy,
      description
    });
  }

  getVersions(id: string): Observable<PolicyVersion[]> {
    return this.http.get<PolicyVersion[]>(`${this.apiUrl}/${id}/versions`);
  }

  restoreVersion(id: string, versionId: string): Observable<BusinessPolicy> {
    return this.http.post<BusinessPolicy>(`${this.apiUrl}/${id}/versions/${versionId}/restore`, {});
  }

  createComment(id: string, userId: string, userName: string, message: string): Observable<PolicyComment> {
    return this.http.post<PolicyComment>(`${this.apiUrl}/${id}/comments`, {
      userId,
      userName,
      message,
      comentario: message
    });
  }

  getComments(id: string): Observable<PolicyComment[]> {
    return this.http.get<PolicyComment[]>(`${this.apiUrl}/${id}/comments`);
  }

  resolveComment(id: string, commentId: string): Observable<PolicyComment> {
    return this.http.patch<PolicyComment>(`${this.apiUrl}/${id}/comments/${commentId}/resolve`, {});
  }

  deleteComment(id: string, commentId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}/comments/${commentId}`);
  }
}