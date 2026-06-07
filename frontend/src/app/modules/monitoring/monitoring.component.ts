import { NgClass, NgFor, NgIf } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  MonitoringService,
  MonitoringSummaryResponse,
  ProcessMonitoringDetailResponse,
  TaskMonitoringResponse
} from '../../core/services/monitoring.service';

@Component({
  selector: 'app-monitoring',
  standalone: true,
  imports: [NgIf, NgFor, NgClass, FormsModule],
  templateUrl: './monitoring.component.html'
})
export class MonitoringComponent implements OnInit {

  summary: MonitoringSummaryResponse | null = null;
  tasks: TaskMonitoringResponse[] = [];
  filteredTasks: TaskMonitoringResponse[] = [];
  selectedProcess: ProcessMonitoringDetailResponse | null = null;

  loading = false;
  errorMessage = '';

  statusFilter = 'TODOS';
  policyFilter = '';
  clienteFilter = '';
  selectedProcessId = '';

  constructor(private monitoringService: MonitoringService) {}

  ngOnInit(): void {
    this.loadMonitoringData();
  }

  loadMonitoringData(): void {
    this.loading = true;
    this.errorMessage = '';

    this.monitoringService.getSummary().subscribe({
      next: (summary) => {
        this.summary = summary;
      },
      error: () => {
        this.errorMessage = 'No se pudo cargar el resumen de monitoreo.';
      }
    });

    this.monitoringService.getAllTasks().subscribe({
      next: (tasks) => {
        this.tasks = tasks;
        this.filteredTasks = tasks;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'No se pudieron cargar las tareas monitoreadas.';
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    this.filteredTasks = this.tasks.filter(task => {
      const matchesStatus =
        this.statusFilter === 'TODOS' || task.estado === this.statusFilter;

      const matchesPolicy =
        !this.policyFilter ||
        task.policyId.toLowerCase().includes(this.policyFilter.toLowerCase());

      const matchesClient =
        !this.clienteFilter ||
        task.processInstanceId.toLowerCase().includes(this.clienteFilter.toLowerCase());

      return matchesStatus && matchesPolicy && matchesClient;
    });
  }

  clearFilters(): void {
    this.statusFilter = 'TODOS';
    this.policyFilter = '';
    this.clienteFilter = '';
    this.filteredTasks = this.tasks;
  }

  loadProcessDetail(processInstanceId: string): void {
    if (!processInstanceId) {
      return;
    }

    this.selectedProcessId = processInstanceId;
    this.selectedProcess = null;
    this.errorMessage = '';

    this.monitoringService.getProcessDetail(processInstanceId).subscribe({
      next: (detail) => {
        this.selectedProcess = detail;
      },
      error: () => {
        this.errorMessage = 'No se pudo cargar la trazabilidad del proceso seleccionado.';
      }
    });
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'EN_PROCESO':
      case 'PENDIENTE':
        return 'bg-yellow-500/10 text-yellow-300 border-yellow-500/30';
      case 'FINALIZADO':
      case 'COMPLETADA':
        return 'bg-emerald-500/10 text-emerald-300 border-emerald-500/30';
      case 'CANCELADO':
      case 'CANCELADA':
        return 'bg-red-500/10 text-red-300 border-red-500/30';
      default:
        return 'bg-slate-500/10 text-slate-300 border-slate-500/30';
    }
  }

  formatDate(value?: string | null): string {
    if (!value) {
      return 'Sin fecha';
    }

    return new Date(value).toLocaleString();
  }

  formatDuration(minutes?: number | null): string {
    if (minutes === null || minutes === undefined) {
      return 'En curso';
    }

    if (minutes < 1) {
      return 'Menos de 1 min';
    }

    if (minutes < 60) {
      return `${minutes} min`;
    }

    const hours = Math.floor(minutes / 60);
    const remainingMinutes = minutes % 60;

    return `${hours} h ${remainingMinutes} min`;
  }
}