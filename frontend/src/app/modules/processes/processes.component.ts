import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BusinessPolicy, BusinessPolicyService } from '../business-policies/business-policy.service';
import { AuthService, LoginResponse } from '../../core/services/auth.service';
import {
  ProcessInstance,
  ProcessInstanceService,
  StartProcessFromAgentRequest
} from '../../core/services/process-instance.service';

@Component({
  selector: 'app-processes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './processes.component.html'
})
export class ProcessesComponent implements OnInit {

  user: LoginResponse | null = null;
  policies: BusinessPolicy[] = [];
  processes: ProcessInstance[] = [];

  selectedPolicyId = '';
  clienteId = '';
  motivo = '';
  observacion = '';

  selectedProcess: ProcessInstance | null = null;
  loading = false;
  message = '';

  constructor(
    private authService: AuthService,
    private businessPolicyService: BusinessPolicyService,
    private processInstanceService: ProcessInstanceService
  ) {}

  ngOnInit(): void {
    this.user = this.authService.getUser();
    this.clienteId = this.user?.id || 'CLIENTE_PRUEBA';
    this.loadPolicies();
    this.loadProcesses();
  }

  loadPolicies(): void {
    this.businessPolicyService.listar().subscribe({
      next: data => this.policies = data,
      error: () => this.message = 'No se pudieron cargar las políticas.'
    });
  }

  loadProcesses(): void {
    this.loading = true;

    this.processInstanceService.listar().subscribe({
      next: data => {
        this.processes = data;
        this.loading = false;
      },
      error: () => {
        this.message = 'No se pudieron cargar los trámites.';
        this.loading = false;
      }
    });
  }

  startProcess(): void {
    if (!this.selectedPolicyId || !this.user) {
      this.message = 'Selecciona una política para iniciar el trámite.';
      return;
    }

    const request: StartProcessFromAgentRequest = {
      policyId: this.selectedPolicyId,
      clienteId: this.clienteId || this.user.id,
      userId: this.user.id,
      initialData: {
        motivo: this.motivo,
        observacion: this.observacion
      }
    };

    this.processInstanceService.iniciarDesdeAgente(request).subscribe({
      next: saved => {
        this.message = `Trámite iniciado correctamente. Nodo actual: ${saved.currentNodeName || 'Sin nodo'}`;
        this.clearForm();
        this.loadProcesses();
      },
      error: err => {
        console.error('Error al iniciar trámite', err);
        this.message = 'No se pudo iniciar el trámite. Verifica que la política tenga diagrama INICIO → ACTIVIDAD → FIN.';
      }
    });
  }

  viewProcess(process: ProcessInstance): void {
    if (!process.id) return;

    this.processInstanceService.obtenerPorId(process.id).subscribe({
      next: data => this.selectedProcess = data,
      error: () => this.message = 'No se pudo cargar el detalle del trámite.'
    });
  }

  clearForm(): void {
    this.selectedPolicyId = '';
    this.motivo = '';
    this.observacion = '';
  }

  getPolicyName(policyId: string): string {
    return this.policies.find(policy => policy.id === policyId)?.nombre || policyId;
  }

  closeDetail(): void {
    this.selectedProcess = null;
  }
}