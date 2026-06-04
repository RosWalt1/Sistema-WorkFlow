import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { Role } from '../models/role.model';

@Injectable({ providedIn: 'root' })
export class RoleService {
  private api = `${environment.apiUrl}/roles`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<Role[]> {
    return this.http
      .get<ApiResponse<Role[]>>(this.api)
      .pipe(map(res => res.data));
  }

  create(role: Role): Observable<Role> {
    return this.http
      .post<ApiResponse<Role>>(this.api, role)
      .pipe(map(res => res.data));
  }

  update(id: string, role: Role): Observable<Role> {
    return this.http
      .put<ApiResponse<Role>>(`${this.api}/${id}`, role)
      .pipe(map(res => res.data));
  }

  delete(id: string): Observable<void> {
    return this.http
      .delete<ApiResponse<void>>(`${this.api}/${id}`)
      .pipe(map(() => undefined));
  }
}