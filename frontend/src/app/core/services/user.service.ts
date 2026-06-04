import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ApiResponse } from '../models/api-response.model';
import { User } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private api = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  findAll(): Observable<User[]> {
    return this.http
      .get<ApiResponse<User[]>>(this.api)
      .pipe(map(res => res.data));
  }

  create(user: User): Observable<User> {
    return this.http
      .post<ApiResponse<User>>(this.api, user)
      .pipe(map(res => res.data));
  }

  update(id: string, user: User): Observable<User> {
    return this.http
      .put<ApiResponse<User>>(`${this.api}/${id}`, user)
      .pipe(map(res => res.data));
  }

  delete(id: string): Observable<void> {
    return this.http
      .delete<ApiResponse<void>>(`${this.api}/${id}`)
      .pipe(map(() => undefined));
  }

  activate(id: string): Observable<User> {
    return this.http
      .patch<ApiResponse<User>>(`${this.api}/${id}/activate`, {})
      .pipe(map(res => res.data));
  }

  deactivate(id: string): Observable<User> {
    return this.http
      .patch<ApiResponse<User>>(`${this.api}/${id}/deactivate`, {})
      .pipe(map(res => res.data));
  }
}