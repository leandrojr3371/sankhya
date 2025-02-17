import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cliente } from '../cliente/services/cliente.service';

export interface PedidoItem {
  produtoId: number;
  quantidade: number;
  produto?: string;
  preco?: number;
  totalItem?: number;
}

export interface Pedido {
  id?: number;
  clienteId: number;
  dataPedido?: string;
  total?: number;
  pedidoItens: PedidoItem[];
  pedidoStatus?: string;
  nomeCliente?: string
}

@Injectable({
  providedIn: 'root'
})
export class PedidoService {
  private apiUrl = 'http://localhost:8080/pedido';

  constructor(private http: HttpClient) {}

  obterPedidoPorId(id: number): Observable<Pedido> {
    return this.http.get<Pedido>(`${this.apiUrl}/${id}`);
  }

  salvarPedido(pedido: Pedido): Observable<number> {
    return this.http.post<number>(`${this.apiUrl}/save`, pedido);
  }

  atualizarStatus(id: number, novoStatus: string): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/updateStatus/${id}`, { novoStatus });
  }

  relatorioGeral(nomeCliente: string | null): Observable<Pedido[]> {
    const params = new HttpParams()
      .set('nomeCliente', nomeCliente ? nomeCliente : '');
  
    return this.http.get<Pedido[]>(`${this.apiUrl}/relatorio`, { params });
  }  

  cancelarPedido(pedidoId: number): Observable<void> {
    const status = { novoStatus: 'CANCELADO' }; 
    return this.http.patch<void>(`${this.apiUrl}/cancelaPedido/${pedidoId}`, status);
  }

}
