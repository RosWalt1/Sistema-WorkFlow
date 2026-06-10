import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  AgentAnalyzeResponse,
  AgentInteraction,
  AgentService,
  ProcessInstance
} from '../../core/services/agent.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-ai',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ai.component.html'
})
export class AiComponent implements OnInit {

  mensaje = '';
  clienteId = '';
  loading = false;
  starting = false;
  errorMessage = '';
  successMessage = '';

  analysis: AgentAnalyzeResponse | null = null;
  createdProcess: ProcessInstance | null = null;
  history: AgentInteraction[] = [];

  constructor(
    private agentService: AgentService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const user = this.authService.getUser();
    this.clienteId = user?.id || '';
    this.loadHistory();
  }

  analyze(): void {
    this.errorMessage = '';
    this.successMessage = '';
    this.createdProcess = null;
    this.analysis = null;

    if (!this.mensaje.trim()) {
      this.errorMessage = 'Escribe una solicitud para que el agente pueda analizarla.';
      return;
    }

    if (!this.clienteId.trim()) {
      this.errorMessage = 'No se pudo obtener el usuario actual.';
      return;
    }

    this.loading = true;

    this.agentService.analyze({
      mensaje: this.mensaje,
      clienteId: this.clienteId
    }).subscribe({
      next: response => {
        this.analysis = response;
        this.loading = false;
        this.loadHistory();
      },
      error: err => {
        console.error('Error al analizar solicitud', err);
        this.errorMessage = 'No se pudo analizar la solicitud.';
        this.loading = false;
      }
    });
  }

  startProcess(): void {
    if (!this.analysis?.recommendedPolicyId) {
      this.errorMessage = 'No existe una política recomendada para iniciar.';
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';
    this.starting = true;

    this.agentService.start({
      policyId: this.analysis.recommendedPolicyId,
      clienteId: this.clienteId,
      mensaje: this.mensaje,
      initialData: {
        mensajeOriginal: this.mensaje,
        origen: 'AGENTE_INTELIGENTE'
      }
    }).subscribe({
      next: response => {
        this.createdProcess = response;
        this.successMessage = 'Trámite iniciado correctamente desde el agente.';
        this.starting = false;
        this.loadHistory();
      },
      error: err => {
        console.error('Error al iniciar trámite', err);
        this.errorMessage = 'No se pudo iniciar el trámite recomendado.';
        this.starting = false;
      }
    });
  }

  loadHistory(): void {
    this.agentService.history().subscribe({
      next: data => this.history = data,
      error: err => console.error('Error al cargar historial del agente', err)
    });
  }

  clear(): void {
    this.mensaje = '';
    this.analysis = null;
    this.createdProcess = null;
    this.errorMessage = '';
    this.successMessage = '';
  }
}