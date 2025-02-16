import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatListModule } from '@angular/material/list';
import { MatTableModule } from '@angular/material/table';

@Component({
  selector: 'app-pedido-itens-modal',
  templateUrl: './pedido-itens-modal.component.html',
  standalone: true, 
  imports: [
    CommonModule,   
    MatDialogModule, 
    MatButtonModule, 
    MatListModule,
    MatTableModule
  ],
  styleUrls: ['./pedido-itens-modal.component.scss']
})
export class PedidoItensModalComponent {

  displayedColumns: string[] = ['produto', 'quantidade', 'preco', 'total'];

  constructor(
    public dialogRef: MatDialogRef<PedidoItensModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    console.log('Itens', data.pedidoItens);
  }

  onClose(): void {
    this.dialogRef.close(); // Fechar o modal quando o botão de fechar for clicado
  }
}
