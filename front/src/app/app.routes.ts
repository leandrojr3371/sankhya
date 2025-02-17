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
    component: AppComponent,  // AppComponent será o layout com o menu
    children: [
      { path: 'cliente', component: ClienteFormComponent, canActivate: [AuthGuard] },
      { path: 'produto', component: ProdutoComponent, canActivate: [AuthGuard] },
      { path: 'pedido', component: PedidoComponent, canActivate: [AuthGuard] },
      { path: 'relatorio-pedido', component: RelatorioPedidosComponent, canActivate: [AuthGuard] },
      { path: '', redirectTo: 'cliente', pathMatch: 'full' }  // Redireciona para "cliente" por padrão
    ]
  },
  { path: 'login', component: LoginComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' }  // Redireciona para login por padrão
];
