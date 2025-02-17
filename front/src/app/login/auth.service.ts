import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  
  private apiUrl = 'http://localhost:8080/auth/login'; // Atualize com a URL correta do seu backend

  constructor(private http: HttpClient, private router: Router) {}

  login(username: string, password: string): Observable<any> {
    const params = new HttpParams()
      .set('username', username)
      .set('password', password);

    return this.http.post<any>(this.apiUrl, null, { params });
  }

  setToken(token: string) {
    localStorage.setItem('token', token);  // Armazena o token
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isLoggedIn(): boolean {
    if (typeof window !== 'undefined' && window.localStorage) {
      const token = localStorage.getItem('token');
      console.log('Token recuperado:', token);
      return !!token; 
    }
    return false; 
  }

  
  logout() {
    localStorage.removeItem('token');  
  }
}
