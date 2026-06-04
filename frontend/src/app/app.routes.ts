import { Routes } from '@angular/router';
import { DashboardComponent } from './modules/dashboard/dashboard.component';
import { UsersComponent } from './modules/users/users.component';
import { RolesComponent } from './modules/roles/roles.component';
import { DepartmentsComponent } from './modules/departments/departments.component';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'users', component: UsersComponent },
  { path: 'roles', component: RolesComponent },
  { path: 'departments', component: DepartmentsComponent },
  { path: '**', redirectTo: 'dashboard' }
];
