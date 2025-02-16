import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Produto {
  id?: number;
  descricao: string;
  precoUnitario: number;
}

@Injectable({
  providedIn: 'root'
})
export class ProdutoService {
  private apiUrl = 'http://localhost:8080/produto';

  constructor(private http: HttpClient) {}

  criarProduto(produto: Produto): Observable<Produto> {
    return this.http.post<Produto>(`${this.apiUrl}/save`, produto);
  }

  atualizarProduto(id: number, produto: Produto): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/atualiza/${id}`, produto);
  }

  removerProduto(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`);
  }

  listarProdutos(): Observable<Produto[]> {
    return this.http.get<Produto[]>(`${this.apiUrl}/listar`);
  }

  filtrarProdutos(filtro: Partial<Produto>): Observable<Produto[]> {
    return this.http.get<Produto[]>(`${this.apiUrl}/filter`, { params: filtro as any });
  }
}
