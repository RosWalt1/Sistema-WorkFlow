import { Component, OnInit } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DepartmentService } from '../../core/services/department.service';
import { Department } from '../../core/models/department.model';

@Component({
  selector: 'app-departments',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule],
  templateUrl: './departments.component.html'
})
export class DepartmentsComponent implements OnInit {
  departments: Department[] = [];
  loading = true;
  showForm = false;
  saving = false;
  editing = false;
  selectedId = '';

  formDepartment: Department = this.emptyDepartment();

  constructor(private departmentService: DepartmentService) {}

  ngOnInit(): void {
    this.loadDepartments();
  }

  emptyDepartment(): Department {
    return {
      nombre: '',
      descripcion: ''
    };
  }

  loadDepartments(): void {
    this.loading = true;

    this.departmentService.findAll().subscribe({
      next: data => {
        this.departments = data;
        this.loading = false;
      },
      error: err => {
        console.error('Error cargando departamentos', err);
        this.loading = false;
      }
    });
  }

  openCreate(): void {
    this.editing = false;
    this.selectedId = '';
    this.formDepartment = this.emptyDepartment();
    this.showForm = true;
  }

  openEdit(department: Department): void {
    this.editing = true;
    this.selectedId = department.id || '';
    this.formDepartment = { ...department };
    this.showForm = true;
  }

  closeForm(): void {
    this.showForm = false;
    this.editing = false;
    this.selectedId = '';
    this.formDepartment = this.emptyDepartment();
  }

  saveDepartment(): void {
    if (!this.formDepartment.nombre) {
      alert('Completa el nombre del departamento');
      return;
    }

    this.saving = true;

    const request = this.editing
      ? this.departmentService.update(this.selectedId, this.formDepartment)
      : this.departmentService.create(this.formDepartment);

    request.subscribe({
      next: () => {
        this.saving = false;
        this.closeForm();
        this.loadDepartments();
      },
      error: err => {
        console.error('Error guardando departamento', err);
        this.saving = false;
        alert('No se pudo guardar el departamento');
      }
    });
  }

  deleteDepartment(department: Department): void {
    if (!department.id) return;

    const confirmDelete = confirm(`¿Seguro que deseas eliminar el departamento ${department.nombre}?`);

    if (!confirmDelete) return;

    this.departmentService.delete(department.id).subscribe({
      next: () => this.loadDepartments(),
      error: err => {
        console.error('Error eliminando departamento', err);
        alert('No se pudo eliminar el departamento');
      }
    });
  }
}