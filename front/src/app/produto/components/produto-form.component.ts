import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Produto, ProdutoService } from '../produto.service';
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
  selector: 'app-produto',
  templateUrl: './produto-form.component.html',
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
  styleUrls: ['./produto-form.component.scss']
})
export class ProdutoComponent implements OnInit {
  produtoForm: FormGroup;
  filtroForm: FormGroup;
  produtosSubject = new BehaviorSubject<Produto[]>([]); // Sempre começa como um array vazio
  produtos$ = this.produtosSubject.asObservable();
  selectedProduto: Produto | null = null;
  displayedColumns: string[] = ['descricao', 'precoUnitario', 'actions'];

  constructor(
    private fb: FormBuilder,
    private produtoService: ProdutoService,
    private snackBar: MatSnackBar
  ) {
    this.produtoForm = this.fb.group({
      descricao: [''],
      precoUnitario: ['']
    });

    this.filtroForm = this.fb.group({
      descricao: [''],
      precoUnitario: ['']
    });
  }

  ngOnInit() {
    this.filtrar();
  }

  listarProdutos() {
    this.produtoService.listarProdutos().subscribe(produtos => {
      this.produtosSubject.next(produtos);
    });
  }

  cadastrar() {
    if (this.produtoForm.valid) {
      if (this.selectedProduto) {
        this.filtrar();
      } else {
        this.produtoService.criarProduto(this.produtoForm.value).subscribe(() => {
          this.listarProdutos();
          this.snackBar.open('Produto cadastrado com sucesso!', 'Fechar', { duration: 3000 });
          this.produtoForm.reset();
        });
      }
    }
  }

  atualizar() {
    if (this.selectedProduto) {
      this.produtoService.atualizarProduto(this.selectedProduto.id!, this.produtoForm.value).subscribe(() => {
        this.filtrar();
        this.snackBar.open('Produto atualizado com sucesso!', 'Fechar', { duration: 3000 });
        this.selectedProduto = null;
        this.produtoForm.reset();
      });
    }
  }

  remover(produto: Produto) {
    this.produtoService.removerProduto(produto.id!).subscribe(() => {
      this.filtrar();
      this.snackBar.open('Produto removido com sucesso!', 'Fechar', { duration: 3000 });
    });
  }

  editar(produto: Produto) {
    this.selectedProduto = produto;
    this.produtoForm.setValue({
      descricao: produto.descricao,
      precoUnitario: produto.precoUnitario
    });
  }

  resetForm() {
    this.produtoForm.reset();
    this.selectedProduto = null;
  }

  filtrar() {
    const filtro = this.filtroForm.value;
    this.produtoService.filtrarProdutos(filtro).subscribe(produtos => {
      this.produtosSubject.next(produtos);
    });
  }

}
