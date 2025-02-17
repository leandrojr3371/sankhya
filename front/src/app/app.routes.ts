import { Routes } from '@angular/router';
import { ClienteFormComponent } from './cliente/components/cliente-form.component';  // Importando o componente
import { ProdutoComponent } from './produto/components/produto-form.component';
import { PedidoComponent } from './pedido/pedido-form.component';
import { RelatorioPedidosComponent } from './pedido/relatorio-pedido/relatorio-pedidos.component';
import { LoginComponent } from './login/login.component';
import { AuthGuard } from './login/auth.guard';
import { AppComponent } from './app.component';

export const routes: Routes = [
  {
    path: 'home',
    component: AppComponent,  
    canActivate: [AuthGuard], 
    children: [
      { path: 'cliente', component: ClienteFormComponent },
      { path: 'produto', component: ProdutoComponent },
      { path: 'pedido', component: PedidoComponent },
      { path: 'relatorio-pedido', component: RelatorioPedidosComponent },
      { path: '', redirectTo: 'cliente', pathMatch: 'full' }
    ]
  },
  { path: 'login', component: LoginComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' }  
];
