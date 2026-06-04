import { Component, OnInit } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../core/services/user.service';
import { User } from '../../core/models/user.model';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [NgFor, NgIf, FormsModule],
  templateUrl: './users.component.html'
})
export class UsersComponent implements OnInit {
  users: User[] = [];
  loading = true;
  showForm = false;
  saving = false;
  editing = false;
  selectedId = '';

  formUser: User = this.emptyUser();

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  emptyUser(): User {
    return {
      nombre: '',
      email: '',
      password: '',
      rol: 'ADMIN',
      departamentoId: '',
      activo: true
    };
  }

  loadUsers(): void {
    this.loading = true;

    this.userService.findAll().subscribe({
      next: data => {
        this.users = data;
        this.loading = false;
      },
      error: err => {
        console.error('Error cargando usuarios', err);
        this.loading = false;
      }
    });
  }

  openCreate(): void {
    this.editing = false;
    this.selectedId = '';
    this.formUser = this.emptyUser();
    this.showForm = true;
  }

  openEdit(user: User): void {
    this.editing = true;
    this.selectedId = user.id || '';
    this.formUser = {
      ...user,
      password: user.password || ''
    };
    this.showForm = true;
  }

  closeForm(): void {
    this.showForm = false;
    this.editing = false;
    this.selectedId = '';
    this.formUser = this.emptyUser();
  }

  saveUser(): void {
    if (!this.formUser.nombre || !this.formUser.email) {
      alert('Completa nombre y email');
      return;
    }

    if (!this.editing && !this.formUser.password) {
      alert('Completa el password');
      return;
    }

    this.saving = true;

    const request = this.editing
      ? this.userService.update(this.selectedId, this.formUser)
      : this.userService.create(this.formUser);

    request.subscribe({
      next: () => {
        this.saving = false;
        this.closeForm();
        this.loadUsers();
      },
      error: err => {
        console.error('Error guardando usuario', err);
        this.saving = false;
        alert('No se pudo guardar el usuario');
      }
    });
  }

  deleteUser(user: User): void {
    if (!user.id) return;

    const confirmDelete = confirm(`¿Seguro que deseas eliminar el usuario ${user.nombre}?`);

    if (!confirmDelete) return;

    this.userService.delete(user.id).subscribe({
      next: () => this.loadUsers(),
      error: err => {
        console.error('Error eliminando usuario', err);
        alert('No se pudo eliminar el usuario');
      }
    });
  }
}