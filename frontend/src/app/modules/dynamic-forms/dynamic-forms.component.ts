import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DynamicForm, DynamicField, DynamicFormService } from '../../core/services/dynamic-form.service';

@Component({
  selector: 'app-dynamic-forms',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dynamic-forms.component.html'
})
export class DynamicFormsComponent implements OnInit {

  forms: DynamicForm[] = [];
  editingId: string | null = null;

  form: DynamicForm = {
    nombre: '',
    activityId: '',
    campos: []
  };

  constructor(private dynamicFormService: DynamicFormService) {}

  ngOnInit(): void {
    this.loadForms();
  }

  loadForms(): void {
    this.dynamicFormService.getAll().subscribe({
      next: data => this.forms = data,
      error: err => console.error('Error al cargar formularios', err)
    });
  }

  addField(): void {
    const field: DynamicField = {
      nombre: '',
      tipoTexto: true,
      tipoFecha: false,
      tipoCheckbox: false,
      adjuntoRequerido: false
    };

    this.form.campos.push(field);
  }

  removeField(index: number): void {
    this.form.campos.splice(index, 1);
  }

  setFieldType(field: DynamicField, type: 'texto' | 'fecha' | 'checkbox'): void {
    field.tipoTexto = type === 'texto';
    field.tipoFecha = type === 'fecha';
    field.tipoCheckbox = type === 'checkbox';
  }

  saveForm(): void {
    if (!this.form.nombre || !this.form.activityId) {
      alert('Completa el nombre del formulario y el ID de actividad');
      return;
    }

    if (this.form.campos.length === 0) {
      alert('Agrega al menos un campo');
      return;
    }

    if (this.editingId) {
      this.dynamicFormService.update(this.editingId, this.form).subscribe({
        next: () => {
          this.resetForm();
          this.loadForms();
        },
        error: err => console.error('Error al actualizar formulario', err)
      });
    } else {
      this.dynamicFormService.create(this.form).subscribe({
        next: () => {
          this.resetForm();
          this.loadForms();
        },
        error: err => console.error('Error al crear formulario', err)
      });
    }
  }

  editForm(selectedForm: DynamicForm): void {
    this.editingId = selectedForm.id || null;

    this.form = {
      nombre: selectedForm.nombre,
      activityId: selectedForm.activityId,
      campos: selectedForm.campos.map(campo => ({ ...campo }))
    };
  }

  deleteForm(id?: string): void {
    if (!id) return;

    if (!confirm('¿Seguro que deseas eliminar este formulario?')) {
      return;
    }

    this.dynamicFormService.delete(id).subscribe({
      next: () => this.loadForms(),
      error: err => console.error('Error al eliminar formulario', err)
    });
  }

  resetForm(): void {
    this.editingId = null;
    this.form = {
      nombre: '',
      activityId: '',
      campos: []
    };
  }
}