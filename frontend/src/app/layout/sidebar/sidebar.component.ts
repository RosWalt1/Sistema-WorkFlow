import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { NgFor } from '@angular/common';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [NgFor, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html'
})
export class SidebarComponent {
  menuItems = [
    { label: 'Dashboard', route: '/dashboard' },
    { label: 'Usuarios', route: '/users' },
    { label: 'Roles', route: '/roles' },
    { label: 'Departamentos', route: '/departments' },
    { label: 'Políticas', route: '/policies' },
    { label: 'Editor Diagrama', route: '/diagram-editor' },
    { label: 'Trámites', route: '/processes' },
    { label: 'Mis Actividades', route: '/tasks' },
    { label: 'Monitoreo', route: '/monitoring' },
    { label: 'Asistente IA', route: '/ai' },
    { label: 'Repositorio Documental', route: '/documents' },
    { label: 'Reportes', route: '/reports' }
  ];
}
