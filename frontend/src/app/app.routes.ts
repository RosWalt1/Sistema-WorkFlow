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
      { path: 'business-policies/:id/editor', component: BpmnEditorComponent, canActivate: [authGuard] }
    ]
  },

  { path: '**', redirectTo: 'login' }
];