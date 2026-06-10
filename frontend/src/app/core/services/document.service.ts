import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DocumentFile {
  id: string;
  processInstanceId: string;
  taskId?: string;
  policyId?: string;
  uploadedBy: string;
  originalFileName: string;
  storedFileName: string;
  contentType: string;
  size: number;
  bucketName: string;
  objectKey: string;
  estado: string;
  fechaSubida: string;
}

export interface DocumentLog {
  id: string;
  documentFileId: string;
  processInstanceId: string;
  taskId?: string;
  userId: string;
  accion: string;
  descripcion: string;
  fecha: string;
}

@Injectable({
  providedIn: 'root'
})
export class DocumentService {
  private apiUrl = 'http://localhost:8080/api/documents';

  constructor(private http: HttpClient) {}

  uploadDocument(
    file: File,
    processInstanceId: string,
    uploadedBy: string,
    taskId?: string,
    policyId?: string
  ): Observable<DocumentFile> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('processInstanceId', processInstanceId);
    formData.append('uploadedBy', uploadedBy);

    if (taskId) {
      formData.append('taskId', taskId);
    }

    if (policyId) {
      formData.append('policyId', policyId);
    }

    return this.http.post<DocumentFile>(`${this.apiUrl}/upload`, formData);
  }

  getDocumentsByProcess(processInstanceId: string): Observable<DocumentFile[]> {
    return this.http.get<DocumentFile[]>(`${this.apiUrl}/process/${processInstanceId}`);
  }

  getDocumentsByTask(taskId: string): Observable<DocumentFile[]> {
    return this.http.get<DocumentFile[]>(`${this.apiUrl}/task/${taskId}`);
  }

  getDocumentHistory(documentId: string): Observable<DocumentLog[]> {
    return this.http.get<DocumentLog[]>(`${this.apiUrl}/${documentId}/history`);
  }

  downloadDocument(documentId: string, userId: string): Observable<Blob> {
    const params = new HttpParams().set('userId', userId);

    return this.http.get(`${this.apiUrl}/${documentId}/download`, {
      params,
      responseType: 'blob'
    });
  }
}