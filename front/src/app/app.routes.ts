import { Routes } from '@angular/router';
import { ClienteFormComponent } from './cliente/components/cliente-form.component';  // Importando o componente
import { ProdutoComponent } from './produto/components/produto-form.component';
import { PedidoComponent } from './pedido/pedido-form.component';
import { RelatorioPedidosComponent } from './pedido/relatorio-pedido/relatorio-pedidos.component';

export const routes: Routes = [
  { 
    path: 'cliente', 
    component: ClienteFormComponent
  },
  { 
    path: 'produto', 
    component: ProdutoComponent
  },
  { 
    path: 'pedido', 
    component: PedidoComponent
  },
  { 
    path: 'relatorio-pedido', 
    component: RelatorioPedidosComponent
  }
]