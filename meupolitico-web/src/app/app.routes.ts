import { Routes } from '@angular/router';
import { PoliticianListComponent } from './features/politicians/politician-list/politician-list';
import { PoliticianDetailComponent } from './features/politicians/politician-detail/politician-detail';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'politicians' },
  { path: 'politicians', component: PoliticianListComponent },
  { path: 'politicians/:id', component: PoliticianDetailComponent }
];
