import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Pedido, PedidoService } from '../pedido/pedido.service'
import { ClienteService } from '../cliente/services/cliente.service'
import { ProdutoService } from '../produto/produto.service'
import { Cliente } from '../cliente/services/cliente.service';
import { Produto } from '../produto/produto.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatDividerModule } from '@angular/material/divider';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatOptionModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { Observable } from 'rxjs';
import { animate, style, transition, trigger } from '@angular/animations';

interface PedidoItem {
  produto: Produto;
  quantidade: number;
}

@Component({
  selector: 'app-pedido',
  templateUrl: './pedido-form.component.html',
  standalone: true, 
    imports: [
        CommonModule,  
        ReactiveFormsModule, 
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatCardModule,
        MatTableModule,
        MatDividerModule,
        MatIconModule,
        MatSnackBarModule,
        MatListModule,
        MatOptionModule,
        MatSelectModule
    ],
    styleUrls: ['./pedido-form.component.scss'],
    animations: [
        trigger('transformPanel', [
          transition('void => *', [
            style({ transform: 'scale(0)' }),
            animate('300ms', style({ transform: 'scale(1)' }))
          ]),
          transition('* => void', [
            animate('300ms', style({ transform: 'scale(0)' }))
          ])
        ])
      ]
    })

export class PedidoComponent implements OnInit {
    pedidoForm: FormGroup;
    clientes$: Observable<Cliente[]>; 
    produtos$: Observable<Produto[]>; 
    itensPedido: PedidoItem[] = [];
    total: number = 0;
  
    constructor(
      private fb: FormBuilder,
      private pedidoService: PedidoService,
      private clienteService: ClienteService,
      private produtoService: ProdutoService,
      private snackBar: MatSnackBar
    ) {
      this.pedidoForm = this.fb.group({
        clienteId: ['', Validators.required],
        produtoId: ['', Validators.required],
        quantidade: ['', [Validators.required, Validators.min(1)]]
      });
      this.clientes$ = this.clienteService.listarClientes();
      this.produtos$ = this.produtoService.listarProdutos();
    }
  
    ngOnInit(): void {
    }
  
    adicionarProduto(): void {
        this.produtos$.subscribe(produtos => {
          const produtoSelecionado = produtos.find(p => p.id === this.pedidoForm.value.produtoId);
          if (!produtoSelecionado) return;
      
          const itemExistente = this.itensPedido.find(item => item.produto.id === produtoSelecionado.id);
          
          if (itemExistente) {
            // Atualiza a quantidade criando um novo objeto para o item
            const index = this.itensPedido.indexOf(itemExistente);
            this.itensPedido[index] = {
              ...itemExistente, // Copia as propriedades do item existente
              quantidade: itemExistente.quantidade + this.pedidoForm.value.quantidade // Atualiza a quantidade
            };
          } else {
            this.itensPedido.push({
              produto: produtoSelecionado,
              quantidade: this.pedidoForm.value.quantidade
            });
          }
      
          // Força a detecção de mudanças
          this.itensPedido = [...this.itensPedido];
          this.calcularTotal();
          this.pedidoForm.patchValue({ produtoId: '', quantidade: '' });
        });
      }
      
      
    removerItem(index: number): void {
    this.itensPedido.splice(index, 1);  // Remove o item da lista
    this.itensPedido = [...this.itensPedido];  // Cria uma nova referência do array
    this.calcularTotal();
    }

  
    calcularTotal(): void {
        // Inscreve-se no Observable de produtos
        this.produtos$.subscribe(produtos => {
          this.total = this.itensPedido.reduce((sum, item) => {
            const produto = produtos.find(p => p.id === item.produto.id);
            return sum + ((produto?.precoUnitario || 0) * item.quantidade);
          }, 0);
        });
    }

  
    salvarPedido(): void {
        if (this.itensPedido.length === 0) {
            this.snackBar.open('Adicione pelo menos um item ao pedido!', 'Fechar', { duration: 3000 });
            return;
          }
        
          const pedidoDTO: Pedido = {
            clienteId: this.pedidoForm.value.clienteId,
            dataPedido: new Date().toISOString(), // Gera a data atual
            total: this.total,
            pedidoItens: this.itensPedido.map(item => ({
              produtoId: item.produto.id ?? 0,
              quantidade: item.quantidade
            })),
            pedidoStatus: 'REALIZADO' // Definir um status inicial adequado
          };

          console.log('Pedido', pedidoDTO);
          
          this.pedidoService.salvarPedido(pedidoDTO).subscribe(() => {
            this.snackBar.open('Pedido cadastrado com sucesso!', 'Fechar', { duration: 3000 });
            this.itensPedido = [];
            this.total = 0;
            this.pedidoForm.reset();
          });
    }
    
}
