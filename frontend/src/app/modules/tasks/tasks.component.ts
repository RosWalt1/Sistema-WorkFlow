import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DynamicForm, DynamicFormService } from '../../core/services/dynamic-form.service';
import { FormSubmission, FormSubmissionService } from '../../core/services/form-submission.service';
import { AuthService, LoginResponse } from '../../core/services/auth.service';

@Component({
  selector: 'app-tasks',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './tasks.component.html'
})
export class TasksComponent implements OnInit {

  user: LoginResponse | null = null;

  activities = [
    {
      id: 'actividad-001',
      nombre: 'Revisar solicitud',
      descripcion: 'Actividad pendiente para revisar información del trámite.',
      estado: 'Pendiente'
    },
    {
      id: 'actividad-002',
      nombre: 'Aprobar documento',
      descripcion: 'Actividad para validar y aprobar documentación.',
      estado: 'Pendiente'
    },
    {
      id: 'actividad-003',
      nombre: 'Finalizar proceso',
      descripcion: 'Actividad final del flujo de trabajo.',
      estado: 'Pendiente'
    }
  ];

  selectedActivity: any = null;
  dynamicForm: DynamicForm | null = null;
  existingSubmission: FormSubmission | null = null;
  formValues: Record<string, any> = {};
  loadingForm = false;
  message = '';

  constructor(
    private dynamicFormService: DynamicFormService,
    private formSubmissionService: FormSubmissionService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.user = this.authService.getUser();
    this.loadCompletedActivities();
  }

  loadCompletedActivities(): void {
    this.activities.forEach(activity => {
      this.formSubmissionService.getByActivityId(activity.id).subscribe({
        next: submission => {
          if (submission.estado === 'COMPLETADA') {
            activity.estado = 'Completada';
          }
        },
        error: () => {}
      });
    });
  }

  executeActivity(activity: any): void {
    this.selectedActivity = activity;
    this.dynamicForm = null;
    this.existingSubmission = null;
    this.formValues = {};
    this.message = '';
    this.loadingForm = true;

    this.formSubmissionService.getByActivityId(activity.id).subscribe({
      next: submission => {
        this.existingSubmission = submission;

        if (submission.estado === 'COMPLETADA') {
          activity.estado = 'Completada';
          this.formValues = submission.respuestas;
          this.message = 'Esta actividad ya fue completada anteriormente.';
        }

        this.loadDynamicForm(activity.id);
      },
      error: () => {
        this.loadDynamicForm(activity.id);
      }
    });
  }

  loadDynamicForm(activityId: string): void {
    this.dynamicFormService.getByActivityId(activityId).subscribe({
      next: form => {
        this.dynamicForm = form;
        this.loadingForm = false;

        if (!this.existingSubmission) {
          form.campos.forEach(field => {
            this.formValues[field.nombre] = field.tipoCheckbox ? false : '';
          });
        }
      },
      error: () => {
        this.loadingForm = false;
        this.message = 'Esta actividad no tiene formulario dinámico asociado.';
      }
    });
  }

  finishActivity(): void {
    if (!this.selectedActivity || !this.dynamicForm || !this.user) return;

    const submission: FormSubmission = {
      activityId: this.selectedActivity.id,
      formId: this.dynamicForm.id || '',
      userId: this.user.id,
      respuestas: this.formValues
    };

    this.formSubmissionService.create(submission).subscribe({
      next: saved => {
        this.existingSubmission = saved;
        this.selectedActivity.estado = 'Completada';
        this.message = 'Actividad completada y guardada correctamente.';
      },
      error: err => {
        console.error('Error al guardar ejecución', err);
        this.message = 'Ocurrió un error al guardar la actividad.';
      }
    });
  }

  closePanel(): void {
    this.selectedActivity = null;
    this.dynamicForm = null;
    this.existingSubmission = null;
    this.formValues = {};
    this.message = '';
  }
}