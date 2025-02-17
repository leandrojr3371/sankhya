import { ChangeDetectorRef, Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';


@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule, 
    MatFormFieldModule, 
    MatInputModule, 
    MatButtonModule, 
    MatSnackBarModule
  ], 
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  username: string = '';
  password: string = '';
  errorMessage: string = '';

  constructor(
    private authService: AuthService, 
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  login() {
    if (!this.username || !this.password) {
      this.errorMessage = 'Por favor, preencha todos os campos';
      return;
    }

    this.authService.login(this.username, this.password).subscribe(
      (response: any) => {
        if (response && response.token) {
          this.authService.setToken(response.token); 
          console.log('Token armazenado com sucesso!');
          
          this.router.navigate(['/home/cliente']);
        } else {
          this.showError('Usuário ou senha inválidos');
        }
      },
      (error) => {
        this.showError('Erro no login: ' + error);
      }
    );
  }

  showError(message: string) {
    this.errorMessage = message;
    this.snackBar.open(message, 'Fechar', {
      duration: 3000,
    });
  }
}
