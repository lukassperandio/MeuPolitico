import { Routes } from '@angular/router';
import { HomePageComponent } from './features/home/home-page';
import { PoliticianListComponent } from './features/politicians/politician-list/politician-list';
import { PoliticianDetailComponent } from './features/politicians/politician-detail/politician-detail';
import { RankingPageComponent } from './features/rankings/ranking-page/ranking-page';
import { ComparisonPageComponent } from './features/comparison/comparison-page/comparison-page';

export const routes: Routes = [
  { path: '', component: HomePageComponent },
  { path: 'politicians', component: PoliticianListComponent },
  { path: 'politicians/:id', component: PoliticianDetailComponent },
  { path: 'rankings', component: RankingPageComponent },
  { path: 'compare', component: ComparisonPageComponent },
  { path: '**', redirectTo: '' }
];
