import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from './login/auth.service';
import { Router } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { HeaderComponent } from './header/header.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, RouterLink, LoginComponent, HeaderComponent],  
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'front-sankhya';
  isAuthenticated: boolean = false;  // Variável de controle para autenticação

  constructor(public authService: AuthService, private router: Router) {}

  ngOnInit() {
    // Verifica o estado de autenticação
    this.isAuthenticated = this.authService.isLoggedIn();
    console.log('autenticado?', this.isAuthenticated);
  }

  logout() {
    this.authService.logout();  
    this.router.navigate(['/login']);  
    this.isAuthenticated = false;
  }
}
