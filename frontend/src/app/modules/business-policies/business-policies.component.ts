import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BusinessPolicy, BusinessPolicyService } from './business-policy.service';

@Component({
  selector: 'app-business-policies',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './business-policies.component.html'
})
export class BusinessPoliciesComponent implements OnInit {

  policies: BusinessPolicy[] = [];

  form: BusinessPolicy = {
    nombre: '',
    descripcion: '',
    diagramaJson: {
      nodes: [],
      connections: []
    }
  };

  editando = false;
  selectedId: string | null = null;

  constructor(private policyService: BusinessPolicyService) {}

  ngOnInit(): void {
    this.cargarPolicies();
  }

  cargarPolicies(): void {
    this.policyService.listar().subscribe({
      next: data => this.policies = data,
      error: err => console.error('Error al listar políticas', err)
    });
  }

  guardar(): void {
    if (!this.form.nombre.trim()) return;

    if (this.editando && this.selectedId) {
      this.policyService.actualizar(this.selectedId, this.form).subscribe(() => {
        this.limpiarFormulario();
        this.cargarPolicies();
      });
    } else {
      this.policyService.crear(this.form).subscribe(() => {
        this.limpiarFormulario();
        this.cargarPolicies();
      });
    }
  }

  editar(policy: BusinessPolicy): void {
    this.editando = true;
    this.selectedId = policy.id || null;
    this.form = JSON.parse(JSON.stringify(policy));
  }

  eliminar(policy: BusinessPolicy): void {
    if (!policy.id) return;

    if (confirm('¿Seguro que deseas eliminar esta política?')) {
      this.policyService.eliminar(policy.id).subscribe(() => {
        this.cargarPolicies();
      });
    }
  }

  activar(policy: BusinessPolicy): void {
    if (!policy.id) return;

    this.policyService.activar(policy.id).subscribe(() => {
      this.cargarPolicies();
    });
  }

  limpiarFormulario(): void {
    this.editando = false;
    this.selectedId = null;
    this.form = {
      nombre: '',
      descripcion: '',
      diagramaJson: {
        nodes: [],
        connections: []
      }
    };
  }
}