import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DynamicForm, DynamicFormService } from '../../core/services/dynamic-form.service';
import { FormSubmission, FormSubmissionService } from '../../core/services/form-submission.service';
import { AuthService, LoginResponse } from '../../core/services/auth.service';
import { TaskService, WorkflowTask } from '../../core/services/task.service';

@Component({
  selector: 'app-tasks',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './tasks.component.html'
})
export class TasksComponent implements OnInit {

  user: LoginResponse | null = null;

  tasks: WorkflowTask[] = [];
  selectedTask: WorkflowTask | null = null;

  dynamicForm: DynamicForm | null = null;
  existingSubmission: FormSubmission | null = null;
  formValues: Record<string, any> = {};

  loadingTasks = false;
  loadingForm = false;
  message = '';

  constructor(
    private dynamicFormService: DynamicFormService,
    private formSubmissionService: FormSubmissionService,
    private authService: AuthService,
    private taskService: TaskService
  ) {}

  ngOnInit(): void {
    this.user = this.authService.getUser();
    this.loadPendingTasks();
  }

  loadPendingTasks(): void {
    this.loadingTasks = true;

    this.taskService.listarPendientes().subscribe({
      next: data => {
        this.tasks = data;
        this.loadingTasks = false;
      },
      error: err => {
        console.error('Error al cargar tareas', err);
        this.message = 'No se pudieron cargar las tareas pendientes.';
        this.loadingTasks = false;
      }
    });
  }

  executeTask(task: WorkflowTask): void {
    this.selectedTask = task;
    this.dynamicForm = null;
    this.existingSubmission = null;
    this.formValues = {};
    this.message = '';
    this.loadingForm = true;

    this.formSubmissionService.getByActivityId(task.nodeId).subscribe({
      next: submission => {
        this.existingSubmission = submission;

        if (submission.estado === 'COMPLETADA') {
          this.formValues = submission.respuestas;
          this.message = 'Esta tarea ya tiene formulario completado anteriormente.';
        }

        this.loadDynamicForm(task.nodeId);
      },
      error: () => {
        this.loadDynamicForm(task.nodeId);
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
        this.dynamicForm = null;
        this.loadingForm = false;
        this.message = 'Esta tarea no tiene formulario dinámico asociado. Puedes completarla sin formulario.';
      }
    });
  }

  finishTask(): void {
    if (!this.selectedTask || !this.selectedTask.id || !this.user) return;

    const request = {
      userId: this.user.id,
      formData: this.formValues,
      respuestas: this.formValues
    };

    if (this.dynamicForm) {
      const submission: FormSubmission = {
        activityId: this.selectedTask.nodeId,
        formId: this.dynamicForm.id || '',
        userId: this.user.id,
        respuestas: this.formValues
      };

      this.formSubmissionService.create(submission).subscribe({
        next: saved => {
          this.existingSubmission = saved;
          this.completeBackendTask(request);
        },
        error: err => {
          console.error('Error al guardar formulario', err);
          this.message = 'Ocurrió un error al guardar el formulario.';
        }
      });

      return;
    }

    this.completeBackendTask(request);
  }

  completeBackendTask(request: { userId: string; formData: Record<string, any>; respuestas: Record<string, any> }): void {
    if (!this.selectedTask?.id) return;

    this.taskService.completar(this.selectedTask.id, request).subscribe({
      next: () => {
        this.message = 'Tarea completada correctamente. El proceso avanzó al siguiente nodo.';
        this.closePanel();
        this.loadPendingTasks();
      },
      error: err => {
        console.error('Error al completar tarea', err);
        this.message = 'Ocurrió un error al completar la tarea.';
      }
    });
  }

  closePanel(): void {
    this.selectedTask = null;
    this.dynamicForm = null;
    this.existingSubmission = null;
    this.formValues = {};
  }
}