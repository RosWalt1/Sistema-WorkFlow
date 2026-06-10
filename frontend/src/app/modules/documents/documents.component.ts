import { NgFor, NgIf, DatePipe, DecimalPipe } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DocumentFile, DocumentLog, DocumentService } from '../../core/services/document.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-documents',
  standalone: true,
  imports: [NgIf, NgFor, FormsModule, DatePipe, DecimalPipe],
  templateUrl: './documents.component.html'
})
export class DocumentsComponent {
  processInstanceId = '';
  taskId = '';
  policyId = '';

  selectedFile: File | null = null;

  documents: DocumentFile[] = [];
  history: DocumentLog[] = [];

  loading = false;
  message = '';
  error = '';

  constructor(
    private documentService: DocumentService,
    private authService: AuthService
  ) {}

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.selectedFile = input.files && input.files.length > 0 ? input.files[0] : null;
  }

  upload(): void {
    this.clearMessages();

    const user = this.authService.getUser();

    if (!user) {
      this.error = 'No se encontró el usuario autenticado.';
      return;
    }

    if (!this.processInstanceId.trim()) {
      this.error = 'El ID del trámite es obligatorio.';
      return;
    }

    if (!this.selectedFile) {
      this.error = 'Seleccione un archivo.';
      return;
    }

    this.loading = true;

    this.documentService.uploadDocument(
      this.selectedFile,
      this.processInstanceId.trim(),
      user.id,
      this.taskId.trim() || undefined,
      this.policyId.trim() || undefined
    ).subscribe({
      next: () => {
        this.message = 'Documento subido correctamente.';
        this.selectedFile = null;
        this.loadByProcess();
      },
      error: () => {
        this.error = 'No se pudo subir el documento. Verifique MinIO y el backend.';
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  loadByProcess(): void {
    this.clearMessages();

    if (!this.processInstanceId.trim()) {
      this.error = 'Ingrese el ID del trámite para buscar documentos.';
      return;
    }

    this.loading = true;

    this.documentService.getDocumentsByProcess(this.processInstanceId.trim()).subscribe({
      next: data => {
        this.documents = data;
        this.history = [];
      },
      error: () => {
        this.error = 'No se pudieron cargar los documentos.';
        this.loading = false;
      },
      complete: () => {
        this.loading = false;
      }
    });
  }

  download(document: DocumentFile): void {
    const user = this.authService.getUser();

    if (!user) {
      this.error = 'No se encontró el usuario autenticado.';
      return;
    }

    this.documentService.downloadDocument(document.id, user.id).subscribe({
      next: blob => {
        const url = window.URL.createObjectURL(blob);
        const link = window.document.createElement('a');
        link.href = url;
        link.download = document.originalFileName || 'documento';
        link.click();
        window.URL.revokeObjectURL(url);
      },
      error: () => {
        this.error = 'No se pudo descargar el documento.';
      }
    });
  }

  viewHistory(document: DocumentFile): void {
    this.clearMessages();

    this.documentService.getDocumentHistory(document.id).subscribe({
      next: data => {
        this.history = data;
      },
      error: () => {
        this.error = 'No se pudo cargar el historial del documento.';
      }
    });
  }

  private clearMessages(): void {
    this.message = '';
    this.error = '';
  }
}