import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { NgFor, NgIf } from '@angular/common';
import { AuthService, LoginResponse } from '../../core/services/auth.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [NgFor, NgIf, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html'
})
export class SidebarComponent {
  user: LoginResponse | null = null;

  menuItems = [
    { label: 'Dashboard', route: '/dashboard' },
    { label: 'Usuarios', route: '/users' },
    { label: 'Roles', route: '/roles' },
    { label: 'Departamentos', route: '/departments' },
    { label: 'Políticas', route: '/business-policies' },
    { label: 'Editor Diagrama', route: '/business-policies' },
    { label: 'Formularios Dinámicos', route: '/dynamic-forms' },
    { label: 'Trámites', route: '/processes' },
    { label: 'Mis Actividades', route: '/tasks' },
    { label: 'Monitoreo', route: '/monitoring' },
    { label: 'Asistente IA', route: '/ai' },
    { label: 'Repositorio Documental', route: '/documents' },
    { label: 'Reportes', route: '/reports' }
  ];

  constructor(private authService: AuthService) {
    this.user = this.authService.getUser();
  }

  logout(): void {
    this.authService.logout();
  }
}