import { Routes } from '@angular/router';
import { MainLayoutComponent } from './layout/main-layout/main-layout.component';
import { DashboardComponent } from './modules/dashboard/dashboard.component';
import { UsersComponent } from './modules/users/users.component';
import { RolesComponent } from './modules/roles/roles.component';
import { DepartmentsComponent } from './modules/departments/departments.component';
import { LoginComponent } from './modules/login/login.component';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';

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
      { path: 'departments', component: DepartmentsComponent, canActivate: [adminGuard] }
    ]
  },

  { path: '**', redirectTo: 'login' }
];