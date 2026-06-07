import { Routes } from '@angular/router';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { DashboardComponent } from './modules/dashboard/dashboard.component';
import { UsersComponent } from './modules/users/users.component';
import { RolesComponent } from './modules/roles/roles.component';
import { DepartmentsComponent } from './modules/departments/departments.component';
import { LoginComponent } from './modules/login/login.component';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';
import { BusinessPoliciesComponent } from './modules/business-policies/business-policies.component';
import { BpmnEditorComponent } from './modules/bpmn-editor/bpmn-editor.component';
import { DynamicFormsComponent } from './modules/dynamic-forms/dynamic-forms.component';
import { TasksComponent } from './modules/tasks/tasks.component';
import { PolicyRequirementsComponent } from './modules/policy-requirements/policy-requirements.component';
import { ProcessesComponent } from './modules/processes/processes.component';
import { MonitoringComponent } from './modules/monitoring/monitoring.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },

  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'users', component: UsersComponent, canActivate: [adminGuard] },
      { path: 'roles', component: RolesComponent, canActivate: [adminGuard] },
      { path: 'departments', component: DepartmentsComponent, canActivate: [adminGuard] },
      { path: 'business-policies', component: BusinessPoliciesComponent, canActivate: [authGuard] },
      { path: 'business-policies/:id/editor', component: BpmnEditorComponent, canActivate: [authGuard] },
      { path: 'dynamic-forms', component: DynamicFormsComponent, canActivate: [authGuard] },
      { path: 'tasks', component: TasksComponent, canActivate: [authGuard] },
      { path: 'policy-requirements', component: PolicyRequirementsComponent, canActivate: [authGuard] },
      { path: 'processes', component: ProcessesComponent, canActivate: [authGuard] },
      { path: 'monitoring', component: MonitoringComponent, canActivate: [authGuard] }
    ]
  },

  { path: '**', redirectTo: 'login' }
];