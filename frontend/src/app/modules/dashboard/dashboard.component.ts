import { Component } from '@angular/core';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  template: `
    <h2 class="text-2xl font-bold mb-4">Dashboard</h2>
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
      <div class="bg-white rounded-xl shadow p-5"><p class="text-slate-500">Usuarios</p><h3 class="text-3xl font-bold">Core</h3></div>
      <div class="bg-white rounded-xl shadow p-5"><p class="text-slate-500">Roles</p><h3 class="text-3xl font-bold">Permisos</h3></div>
      <div class="bg-white rounded-xl shadow p-5"><p class="text-slate-500">Departamentos</p><h3 class="text-3xl font-bold">Áreas</h3></div>
    </div>
  `
})
export class DashboardComponent {}
