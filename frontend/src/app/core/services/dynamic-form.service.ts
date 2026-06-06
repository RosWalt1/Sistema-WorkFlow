import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DynamicField {
  nombre: string;
  tipoTexto: boolean;
  tipoFecha: boolean;
  tipoCheckbox: boolean;
  adjuntoRequerido: boolean;
}

export interface DynamicForm {
  id?: string;
  nombre: string;
  activityId: string;
  campos: DynamicField[];
}

@Injectable({
  providedIn: 'root'
})
export class DynamicFormService {

  private apiUrl = 'http://localhost:8080/api/dynamic-forms';

  constructor(private http: HttpClient) {}

  getAll(): Observable<DynamicForm[]> {
    return this.http.get<DynamicForm[]>(this.apiUrl);
  }

  getById(id: string): Observable<DynamicForm> {
    return this.http.get<DynamicForm>(`${this.apiUrl}/${id}`);
  }

  getByActivityId(activityId: string): Observable<DynamicForm> {
    return this.http.get<DynamicForm>(`${this.apiUrl}/activity/${activityId}`);
  }

  create(form: DynamicForm): Observable<DynamicForm> {
    return this.http.post<DynamicForm>(this.apiUrl, form);
  }

  update(id: string, form: DynamicForm): Observable<DynamicForm> {
    return this.http.put<DynamicForm>(`${this.apiUrl}/${id}`, form);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}