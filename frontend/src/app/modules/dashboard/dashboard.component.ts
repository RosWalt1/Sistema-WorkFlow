import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UserService } from '../../core/services/user.service';
import { RoleService } from '../../core/services/role.service';
import { DepartmentService } from '../../core/services/department.service';
import { PolicyRequirementService } from '../../core/services/policy-requirement.service';
import { ProcessInstanceService } from '../../core/services/process-instance.service';
import { TaskService } from '../../core/services/task.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="space-y-6 text-white">

      <div>
        <h1 class="text-3xl font-bold text-white">Dashboard General</h1>
        <p class="text-slate-400">Resumen rápido del sistema Workflow</p>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-4">

        <div
          *ngFor="let card of cards"
          class="rounded-2xl border border-cyan-500/20 bg-slate-900/80 p-5 shadow-lg shadow-cyan-950/30"
        >
          <div class="flex items-center justify-between">
            <p class="text-slate-300">{{ card.title }}</p>
            <span class="text-2xl">{{ card.icon }}</span>
          </div>

          <h2 class="mt-4 text-4xl font-bold text-cyan-300">
            {{ card.value }}
          </h2>
        </div>

      </div>

      <div class="rounded-2xl border border-violet-500/20 bg-slate-900/80 p-6 shadow-lg shadow-violet-950/30">
        <h2 class="text-xl font-semibold mb-5 text-white">
          Actividad del sistema
        </h2>

        <div class="space-y-5">

          <div>
            <div class="flex justify-between text-sm text-slate-300 mb-1">
              <span>Procesos</span>
              <span>{{ procesos }}</span>
            </div>
            <div class="w-full bg-slate-800 rounded-full h-3">
              <div
                class="bg-cyan-400 h-3 rounded-full"
                [style.width.%]="barWidth(procesos)">
              </div>
            </div>
          </div>

          <div>
            <div class="flex justify-between text-sm text-slate-300 mb-1">
              <span>Tareas</span>
              <span>{{ tareas }}</span>
            </div>
            <div class="w-full bg-slate-800 rounded-full h-3">
              <div
                class="bg-violet-500 h-3 rounded-full"
                [style.width.%]="barWidth(tareas)">
              </div>
            </div>
          </div>

          <div>
            <div class="flex justify-between text-sm text-slate-300 mb-1">
              <span>Usuarios</span>
              <span>{{ usuarios }}</span>
            </div>
            <div class="w-full bg-slate-800 rounded-full h-3">
              <div
                class="bg-blue-500 h-3 rounded-full"
                [style.width.%]="barWidth(usuarios)">
              </div>
            </div>
          </div>

        </div>
      </div>

    </div>
  `
})
export class DashboardComponent implements OnInit {

  usuarios = 0;
  roles = 0;
  departamentos = 0;
  politicas = 0;
  procesos = 0;
  tareas = 0;
  documentos = 0;
  reportes = 1;

  constructor(
    private userService: UserService,
    private roleService: RoleService,
    private departmentService: DepartmentService,
    private policyService: PolicyRequirementService,
    private processService: ProcessInstanceService,
    private taskService: TaskService
  ) {}

  ngOnInit(): void {
    this.userService.findAll()
      .subscribe(data => this.usuarios = data.length);

    this.roleService.findAll()
      .subscribe(data => this.roles = data.length);

    this.departmentService.findAll()
      .subscribe(data => this.departamentos = data.length);

    this.policyService.listar()
      .subscribe(data => this.politicas = data.length);

    this.processService.listar()
      .subscribe(data => this.procesos = data.length);

    this.taskService.listar()
      .subscribe(data => this.tareas = data.length);
  }

  get cards() {
    return [
      { title: 'Usuarios', value: this.usuarios, icon: '👥' },
      { title: 'Roles', value: this.roles, icon: '🔐' },
      { title: 'Departamentos', value: this.departamentos, icon: '🏢' },
      { title: 'Políticas', value: this.politicas, icon: '📋' },
      { title: 'Procesos', value: this.procesos, icon: '⚙️' },
      { title: 'Tareas', value: this.tareas, icon: '✅' },
      { title: 'Documentos', value: this.documentos, icon: '📄' },
      { title: 'Reportes', value: this.reportes, icon: '📊' }
    ];
  }

  barWidth(value: number): number {
    return Math.min(value * 10, 100);
  }
}