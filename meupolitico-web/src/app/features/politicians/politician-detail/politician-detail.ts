import { ChangeDetectorRef, Component, inject, OnInit, DestroyRef } from '@angular/core';
import { CommonModule, CurrencyPipe, DatePipe, DecimalPipe } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
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
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-politician-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    ReactiveFormsModule,
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
  private readonly destroyRef = inject(DestroyRef);

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

  readonly filterForm = new FormGroup({
    supplier: new FormControl('', { nonNullable: true }),
    startDate: new FormControl('', { nonNullable: true }),
    endDate: new FormControl('', { nonNullable: true }),
    minAmount: new FormControl<number | null>(null),
    sort: new FormControl('date,desc', { nonNullable: true })
  });

  get hasMore(): boolean {
    return this.expenses.length < this.totalElements;
  }

  get loadedTotalAmount(): number {
    return this.expenses.reduce((sum, e) => sum + (Number(e.amount) || 0), 0);
  }

  /** Presenças registadas (fonte Câmara: só quem marcou presença) */
  get presenceCount(): number {
    if (this.attendanceSummary?.present != null) {
      return this.attendanceSummary.present;
    }
    if (this.attendanceSummary?.totalSessions != null) {
      return this.attendanceSummary.totalSessions;
    }
    return 0;
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
      expenses: this.expenseService.search({
        politicianId: id,
        page: 0,
        size: this.pageSize,
        sort: this.filterForm.controls.sort.value
      }),
      attendance: this.attendanceService.getSummary(id).pipe(catchError(() => of(null))),
      assets: this.assetService.getEvolution(id).pipe(catchError(() => of(null)))
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
        this.setupLiveFilters();
      },
      error: (err) => {
        console.error(err);
        this.error = 'Não foi possível carregar o perfil.';
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  applyFilters(): void {
    this.page = 0;
    this.loadExpenses(false);
  }

  clearFilters(): void {
    this.filterForm.reset({
      supplier: '',
      startDate: '',
      endDate: '',
      minAmount: null,
      sort: 'date,desc'
    });
    this.applyFilters();
  }

  loadMore(): void {
    if (!this.hasMore || this.expensesLoading) {
      return;
    }
    this.page += 1;
    this.loadExpenses(true);
  }

  private loadExpenses(append: boolean): void {
    if (!this.politician) {
      return;
    }

    const f = this.filterForm.getRawValue();
    this.expensesLoading = true;
    this.cdr.markForCheck();

    this.expenseService
      .search({
        politicianId: this.politician.id,
        page: this.page,
        size: this.pageSize,
        sort: f.sort,
        supplier: f.supplier || undefined,
        startDate: f.startDate || undefined,
        endDate: f.endDate || undefined,
        minAmount: f.minAmount != null && f.minAmount !== ('' as never)
          ? Number(f.minAmount)
          : undefined
      })
      .subscribe({
        next: (pageData) => {
          const content = pageData.content ?? [];
          this.expenses = append ? [...this.expenses, ...content] : content;
          this.totalElements = pageData.totalElements ?? 0;
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

  private setupLiveFilters(): void {
    this.filterForm.controls.supplier.valueChanges
      .pipe(
        debounceTime(350),
        distinctUntilChanged(),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(() => this.applyFilters());

    this.filterForm.controls.sort.valueChanges
      .pipe(
        distinctUntilChanged(),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(() => this.applyFilters());

    this.filterForm.controls.minAmount.valueChanges
      .pipe(
        debounceTime(500),
        distinctUntilChanged(),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(() => this.applyFilters());

    this.filterForm.controls.startDate.valueChanges
      .pipe(
        distinctUntilChanged(),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(() => this.applyFilters());

    this.filterForm.controls.endDate.valueChanges
      .pipe(
        distinctUntilChanged(),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(() => this.applyFilters());
  }
}
