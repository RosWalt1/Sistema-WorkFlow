import { Component, OnInit } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RoleService } from '../../core/services/role.service';
import { Role } from '../../core/models/role.model';

@Component({
  selector: 'app-roles',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule],
  templateUrl: './roles.component.html'
})
export class RolesComponent implements OnInit {
  roles: Role[] = [];
  loading = true;
  showForm = false;
  saving = false;
  editing = false;
  selectedId = '';

  permisosTexto = '';
  formRole: Role = this.emptyRole();

  constructor(private roleService: RoleService) {}

  ngOnInit(): void {
    this.loadRoles();
  }

  emptyRole(): Role {
    return {
      nombre: '',
      permisos: []
    };
  }

  loadRoles(): void {
    this.loading = true;

    this.roleService.findAll().subscribe({
      next: data => {
        this.roles = data;
        this.loading = false;
      },
      error: err => {
        console.error('Error cargando roles', err);
        this.loading = false;
      }
    });
  }

  openCreate(): void {
    this.editing = false;
    this.selectedId = '';
    this.permisosTexto = '';
    this.formRole = this.emptyRole();
    this.showForm = true;
  }

  openEdit(role: Role): void {
    this.editing = true;
    this.selectedId = role.id || '';
    this.formRole = { ...role };
    this.permisosTexto = role.permisos?.join(', ') || '';
    this.showForm = true;
  }

  closeForm(): void {
    this.showForm = false;
    this.editing = false;
    this.selectedId = '';
    this.permisosTexto = '';
    this.formRole = this.emptyRole();
  }

  saveRole(): void {
    if (!this.formRole.nombre) {
      alert('Completa el nombre del rol');
      return;
    }

    this.formRole.permisos = this.permisosTexto
      .split(',')
      .map(item => item.trim())
      .filter(item => item.length > 0);

    this.saving = true;

    const request = this.editing
      ? this.roleService.update(this.selectedId, this.formRole)
      : this.roleService.create(this.formRole);

    request.subscribe({
      next: () => {
        this.saving = false;
        this.closeForm();
        this.loadRoles();
      },
      error: err => {
        console.error('Error guardando rol', err);
        this.saving = false;
        alert('No se pudo guardar el rol');
      }
    });
  }

  deleteRole(role: Role): void {
    if (!role.id) return;

    const confirmDelete = confirm(`¿Seguro que deseas eliminar el rol ${role.nombre}?`);

    if (!confirmDelete) return;

    this.roleService.delete(role.id).subscribe({
      next: () => this.loadRoles(),
      error: err => {
        console.error('Error eliminando rol', err);
        alert('No se pudo eliminar el rol');
      }
    });
  }
}