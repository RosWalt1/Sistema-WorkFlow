import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { DocumentService } from '../../core/services/document.service';
import { AuthService } from '../../core/services/auth.service';

declare const DocsAPI: any;

@Component({
  selector: 'app-onlyoffice-editor',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="h-[90vh] bg-slate-950 text-white">
      <div class="p-4 border-b border-slate-700">
        <h1 class="text-xl font-bold text-cyan-400">Editor OnlyOffice</h1>
        <p class="text-sm text-slate-400">Edición colaborativa de documentos</p>
      </div>

      <div id="onlyoffice-editor" class="w-full h-full"></div>

      <div *ngIf="error" class="p-4 text-red-400">
        {{ error }}
      </div>
    </div>
  `
})
export class OnlyofficeEditorComponent implements OnInit {
  error = '';

  constructor(
    private route: ActivatedRoute,
    private documentService: DocumentService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const documentId = this.route.snapshot.paramMap.get('id');
    const user = this.authService.getUser();

    if (!documentId || !user) {
      this.error = 'No se pudo abrir el documento.';
      return;
    }

    this.documentService.getOnlyOfficeConfig(documentId, user.id).subscribe({
      next: config => {
        this.loadOnlyOffice(config);
      },
      error: () => {
        this.error = 'No se pudo cargar la configuración de OnlyOffice.';
      }
    });
  }

  private loadOnlyOffice(config: any): void {
    const script = document.createElement('script');
    script.src = `${config.documentServerUrl}/web-apps/apps/api/documents/api.js`;

    script.onload = () => {
      new DocsAPI.DocEditor('onlyoffice-editor', config);
    };

    script.onerror = () => {
      this.error = 'No se pudo cargar el script de OnlyOffice.';
    };

    document.body.appendChild(script);
  }
}