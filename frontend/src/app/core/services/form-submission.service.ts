import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface FormSubmission {
  id?: string;
  activityId: string;
  formId: string;
  userId: string;
  estado?: string;
  respuestas: Record<string, any>;
  fechaEnvio?: string;
}

@Injectable({
  providedIn: 'root'
})
export class FormSubmissionService {

  private apiUrl = 'http://localhost:8080/api/form-submissions';

  constructor(private http: HttpClient) {}

  getByActivityId(activityId: string): Observable<FormSubmission> {
    return this.http.get<FormSubmission>(`${this.apiUrl}/activity/${activityId}`);
  }

  create(submission: FormSubmission): Observable<FormSubmission> {
    return this.http.post<FormSubmission>(this.apiUrl, submission);
  }
}