import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Cliente {
  id: number;
  nome: string;
  cpf: string;
}

@Injectable({
  providedIn: 'root' 
})
export class ClienteService {
  private apiUrl = 'http://localhost:8080/cliente';
  constructor(private http: HttpClient) {}

  criarCliente(client: Cliente): Observable<Cliente> {
    return this.http.post<Cliente>(`${this.apiUrl}/salvar`, client);
  }

  getClientePorId(id: number): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.apiUrl}/${id}`);
  }
  
  atualizaCliente(id: number, client: Cliente): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/atualiza/${id}`, client);
  }

  removeCliente(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/remove/${id}`);
  }

  listarClientes(): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(`${this.apiUrl}/listar`); // Atualiza com todos os clientes
  }
}
