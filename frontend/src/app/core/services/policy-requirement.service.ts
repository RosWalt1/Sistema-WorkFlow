import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PolicyRequirement {
  id?: string;
  policyId: string;
  initialRequiredDocs: string[];
  initialQuestions: string[];
}

@Injectable({
  providedIn: 'root'
})
export class PolicyRequirementService {

  private apiUrl = 'http://localhost:8080/api/policy-requirements';

  constructor(private http: HttpClient) {}

  listar(): Observable<PolicyRequirement[]> {
    return this.http.get<PolicyRequirement[]>(this.apiUrl);
  }

  obtenerPorId(id: string): Observable<PolicyRequirement> {
    return this.http.get<PolicyRequirement>(`${this.apiUrl}/${id}`);
  }

  obtenerPorPolicyId(policyId: string): Observable<PolicyRequirement> {
    return this.http.get<PolicyRequirement>(`${this.apiUrl}/policy/${policyId}`);
  }

  crear(policyRequirement: PolicyRequirement): Observable<PolicyRequirement> {
    return this.http.post<PolicyRequirement>(this.apiUrl, policyRequirement);
  }

  actualizar(id: string, policyRequirement: PolicyRequirement): Observable<PolicyRequirement> {
    return this.http.put<PolicyRequirement>(`${this.apiUrl}/${id}`, policyRequirement);
  }

  eliminar(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}