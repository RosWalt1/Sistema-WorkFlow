import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { Department } from '../models/department.model';

@Injectable({ providedIn: 'root' })
export class DepartmentService {
  private api = `${environment.apiUrl}/departments`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<Department[]> {
    return this.http
      .get<ApiResponse<Department[]>>(this.api)
      .pipe(map(res => res.data));
  }

  create(department: Department): Observable<Department> {
    return this.http
      .post<ApiResponse<Department>>(this.api, department)
      .pipe(map(res => res.data));
  }

  update(id: string, department: Department): Observable<Department> {
    return this.http
      .put<ApiResponse<Department>>(`${this.api}/${id}`, department)
      .pipe(map(res => res.data));
  }

  delete(id: string): Observable<void> {
    return this.http
      .delete<ApiResponse<void>>(`${this.api}/${id}`)
      .pipe(map(() => undefined));
  }
}