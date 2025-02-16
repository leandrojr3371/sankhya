import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Cliente, ClienteService } from '../services/cliente.service';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatDividerModule } from '@angular/material/divider';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { CommonModule } from '@angular/common';
import { BehaviorSubject, Observable } from 'rxjs';
import { MatTooltipModule } from '@angular/material/tooltip';


@Component({
  selector: 'app-client-form',
  templateUrl: './cliente-form.component.html',
  standalone: true, 
  imports: [
    ReactiveFormsModule, 
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCardModule,
    MatTableModule,
    MatDividerModule,
    MatIconModule,
    MatSnackBarModule,
    CommonModule,
    MatListModule,
    MatTooltipModule
  ],
  styleUrls: ['./cliente-form.component.scss']
})
export class ClienteFormComponent implements OnInit {
  
  clienteForm: FormGroup;
  displayedColumns: string[] = ['nome', 'cpf', 'actions'];
  selectedClient: Cliente | null = null; 
  message: string | null = null;
  success: boolean = false;

  clientesSubject = new BehaviorSubject<any[]>([]);
  clientes$ = this.clientesSubject.asObservable();

  constructor(
    private fb: FormBuilder,
    private clienteService: ClienteService,
    private snackBar: MatSnackBar
  ) {
    this.clienteForm = this.fb.group({ 
      nome: ['', Validators.required],
      cpf: ['', [Validators.required, Validators.pattern('[0-9]{11}')]]
    });
  }

  ngOnInit() {
    this.listarClientes();
  }

  cadastrar() {
    if (this.clienteForm.valid) {
      if (this.selectedClient) { 
        this.atualizar();
      } else { 
        this.clienteService.criarCliente(this.clienteForm.value).subscribe(response => {
          this.listarClientes();
          this.snackBar.open('Cliente cadastrado com sucesso!', 'Fechar', { duration: 3000 });
          this.clienteForm.reset();
        });
      }
    }
  }
  

  atualizar() {
    if (this.selectedClient) {
      this.clienteService.atualizaCliente(this.selectedClient.id, this.clienteForm.value).subscribe(() => {
        this.listarClientes();
        this.selectedClient = null; 
        this.snackBar.open('Cliente atualizado com sucesso!', 'Fechar', { duration: 3000 });
        // Atualiza a lista localmente para refletir a mudança
        const updatedClientes = this.clientesSubject.value.map(cliente =>
          cliente.id === this.selectedClient?.id ? { ...cliente, ...this.clienteForm.value } : cliente
        );
        this.clientesSubject.next(updatedClientes);       
        this.clienteForm.reset(); 
      });
    }
  }
  

  remover(client: any = this.selectedClient) {
    if (client) {
      this.clienteService.removeCliente(client.id).subscribe(() => {
        this.listarClientes();
        this.snackBar.open('Cliente removido com sucesso!', 'Fechar', { duration: 3000 });
        const updatedClientes = this.clientesSubject.value.filter(cliente => cliente.id !== client.id);
        this.listarClientes();
        this.clientesSubject.next(updatedClientes);
      });
    }
  }

  listarClientes() {
    this.clienteService.listarClientes().subscribe(clientes => {
      console.log('Clientes recebidos:', clientes); 
      this.clientesSubject.next(clientes);  
    });
  }
  

  editar(cliente: Cliente) {
    this.selectedClient = cliente; 
    this.clienteForm.setValue({
      nome: cliente.nome,
      cpf: cliente.cpf
    });
  }

  resetForm() {
    this.clienteForm.reset();
    this.message = null;
    this.selectedClient = null; 
  }

  

}
