import { Component, inject } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import {
  catchError,
  map,
  of,
  switchMap,
  Observable,
  startWith,
  combineLatest
} from 'rxjs';
import { PoliticianService } from '../../../core/services/politician.service';
import { ExpenseService } from '../../../core/services/expense.service';
import { Politician } from '../../../core/models/politician.model';
import { Expense } from '../../../core/models/expense.model';
import { ExpenseCategoryLabelPipe } from '../../../shared/pipes/expense-category-label-pipe';

interface DetailState {
  loading: boolean;
  error: string | null;
  politician: Politician | null;
  expenses: Expense[];
  expensesLoading: boolean;
  totalExpenses: number;
}

@Component({
  selector: 'app-politician-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, CurrencyPipe, DatePipe, ExpenseCategoryLabelPipe],
  templateUrl: './politician-detail.html',
  styleUrl: './politician-detail.scss'
})
export class PoliticianDetailComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly politicianService = inject(PoliticianService);
  private readonly expenseService = inject(ExpenseService);

  readonly state$: Observable<DetailState> = this.route.paramMap.pipe(
    map((params) => Number(params.get('id'))),
    switchMap((id) => {
      if (!id || Number.isNaN(id)) {
        return of({
          loading: false,
          error: 'ID inválido.',
          politician: null,
          expenses: [],
          expensesLoading: false,
          totalExpenses: 0
        });
      }

      const politician$ = this.politicianService.findById(id);
      const expenses$ = this.expenseService.searchByPolitician(id, 0, 20);

      return combineLatest([politician$, expenses$]).pipe(
        map(([politician, expensePage]) => ({
          loading: false,
          error: null,
          politician,
          expenses: expensePage.content ?? [],
          expensesLoading: false,
          totalExpenses: expensePage.totalElements ?? 0
        })),
        startWith({
          loading: true,
          error: null,
          politician: null as Politician | null,
          expenses: [] as Expense[],
          expensesLoading: true,
          totalExpenses: 0
        }),
        catchError((err) => {
          console.error(err);
          return of({
            loading: false,
            error: 'Não foi possível carregar o perfil.',
            politician: null,
            expenses: [],
            expensesLoading: false,
            totalExpenses: 0
          });
        })
      );
    })
  );
}
