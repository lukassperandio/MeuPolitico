import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe, DecimalPipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { PoliticianService } from '../../../core/services/politician.service';
import { ExpenseService } from '../../../core/services/expense.service';
import { AttendanceService } from '../../../core/services/attendance.service';
import { AssetService } from '../../../core/services/asset.service';
import { Politician } from '../../../core/models/politician.model';
import { Expense } from '../../../core/models/expense.model';
import { AttendanceSummary } from '../../../core/models/attendance.model';
import { AssetEvolution } from '../../../core/models/asset.model';
import { ExpenseCategoryLabelPipe } from '../../../shared/pipes/expense-category-label-pipe';

@Component({
  selector: 'app-politician-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    CurrencyPipe,
    DatePipe,
    DecimalPipe,
    ExpenseCategoryLabelPipe
  ],
  templateUrl: './politician-detail.html',
  styleUrl: './politician-detail.scss'
})
export class PoliticianDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly politicianService = inject(PoliticianService);
  private readonly expenseService = inject(ExpenseService);
  private readonly attendanceService = inject(AttendanceService);
  private readonly assetService = inject(AssetService);
  private readonly cdr = inject(ChangeDetectorRef);

  politician: Politician | null = null;
  expenses: Expense[] = [];
  attendanceSummary: AttendanceSummary | null = null;
  assetEvolution: AssetEvolution | null = null;

  loading = true;
  expensesLoading = false;
  error: string | null = null;

  page = 0;
  readonly pageSize = 20;
  totalElements = 0;

  get hasMore(): boolean {
    return this.expenses.length < this.totalElements;
  }

  get loadedTotalAmount(): number {
    return this.expenses.reduce((sum, e) => sum + (Number(e.amount) || 0), 0);
  }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    if (!id || Number.isNaN(id)) {
      this.error = 'ID inválido.';
      this.loading = false;
      this.cdr.markForCheck();
      return;
    }

    this.loading = true;
    this.error = null;
    this.cdr.markForCheck();

    forkJoin({
      politician: this.politicianService.findById(id),
      expenses: this.expenseService.searchByPolitician(id, 0, this.pageSize),
      attendance: this.attendanceService.getSummary(id).pipe(
        catchError(() => of(null))
      ),
      assets: this.assetService.getEvolution(id).pipe(
        catchError(() => of(null))
      )
    }).subscribe({
      next: ({ politician, expenses, attendance, assets }) => {
        this.politician = politician;
        this.expenses = expenses.content ?? [];
        this.totalElements = expenses.totalElements ?? 0;
        this.page = 0;
        this.attendanceSummary = attendance;
        this.assetEvolution = assets;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error(err);
        this.error = 'Não foi possível carregar o perfil.';
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  loadMore(): void {
    if (!this.politician || !this.hasMore || this.expensesLoading) {
      return;
    }

    const nextPage = this.page + 1;
    this.expensesLoading = true;
    this.cdr.markForCheck();

    this.expenseService.searchByPolitician(this.politician.id, nextPage, this.pageSize).subscribe({
      next: (pageData) => {
        this.expenses = [...this.expenses, ...(pageData.content ?? [])];
        this.totalElements = pageData.totalElements ?? this.totalElements;
        this.page = nextPage;
        this.expensesLoading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error(err);
        this.expensesLoading = false;
        this.cdr.markForCheck();
      }
    });
  }
}
