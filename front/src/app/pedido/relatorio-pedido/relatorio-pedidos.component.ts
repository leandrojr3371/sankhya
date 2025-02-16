import { Component, OnInit } from '@angular/core';
import { PedidoService } from '../pedido.service';
import { Pedido } from '../pedido.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatDividerModule } from '@angular/material/divider';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatOptionModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { PedidoItensModalComponent } from '../pedido-itens/pedido-itens-modal.component';

@Component({
  selector: 'app-relatorio-pedidos',
  templateUrl: './relatorio-pedidos.component.html',
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
        MatSelectModule,
        FormsModule,
        MatTooltipModule,
        MatDialogModule
    ],
  styleUrls: ['./relatorio-pedidos.component.scss']
})
export class RelatorioPedidosComponent implements OnInit {
    pedidos: any[] = [];
    clienteId: number | null = null;
    displayedColumns: string[] = ['id', 'cliente', 'status', 'total', 'acoes'];  // Colunas da tabela
    dataSource = new MatTableDataSource<any>(this.pedidos);
  
    constructor(private pedidoService: PedidoService, public dialog: MatDialog, private snackBar: MatSnackBar) {}
  
    ngOnInit(): void {
      this.getPedidos(); 
    }
  
    getPedidos(): void {
        const carregarItens = true; // Ou false, dependendo de quando você quiser carregar os itens
      
        this.pedidoService.relatorioGeral(this.clienteId, carregarItens).subscribe((pedidos: any[]) => {
          this.pedidos = pedidos;
          this.dataSource.data = pedidos;
          console.log('Pedido', this.pedidos)
        });
      }
      
  
    verItensPedido(pedidoId: number): void {
        // Aqui você busca os itens do pedido, você pode modificar para pegar da API
        const pedidoSelecionado = this.pedidos.find(pedido => pedido.id === pedidoId);
        
        if (pedidoSelecionado) {
          const dialogRef = this.dialog.open(PedidoItensModalComponent, {
            width: '500px',  // Defina o tamanho do modal
            data: {
              pedidoId: pedidoSelecionado.id,
              pedidoItens: pedidoSelecionado.pedidoItens
            }
          });
    
          dialogRef.afterClosed().subscribe(result => {
            console.log('Modal fechado');
          });
        }
    }

    cancelarPedido(pedidoId: number): void {
        this.pedidoService.cancelarPedido(pedidoId).subscribe({
          next: () => {
            // Exibe uma mensagem de sucesso
            this.snackBar.open('Pedido cancelado com sucesso', 'Fechar', {
              duration: 3000,
              panelClass: ['success-snackbar'],
            });
            // Atualiza os pedidos ou executa qualquer ação após o cancelamento
            this.getPedidos();
          },
          error: (err) => {
            // Exibe uma mensagem de erro
            this.snackBar.open('Erro ao cancelar pedido', 'Fechar', {
              duration: 3000,
              panelClass: ['error-snackbar'],
            });
          }
        });
    }
}
