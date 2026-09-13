import { ChangeDetectorRef, Component, inject, OnInit, ViewChild } from '@angular/core';
import { CommonModule, CurrencyPipe, DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { RankingService } from '../../../core/services/ranking.service';
import { RankingItem } from '../../../core/models/ranking.model';
import { PagerComponent } from '../../../shared/components/pager/pager';
import {
  RankFiltersComponent,
  RankFilterValue
} from '../../../shared/components/rank-filters/rank-filters';

type RankingType = 'expenses' | 'attendance' | 'assets';

@Component({
  selector: 'app-ranking-page',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    CurrencyPipe,
    DecimalPipe,
    PagerComponent,
    RankFiltersComponent
  ],
  templateUrl: './ranking-page.html',
  styleUrl: './ranking-page.scss'
})
export class RankingPageComponent implements OnInit {
  private readonly rankingService = inject(RankingService);
  private readonly cdr = inject(ChangeDetectorRef);

  @ViewChild(RankFiltersComponent) private rankFilters?: RankFiltersComponent;

  type: RankingType = 'expenses';
  items: RankingItem[] = [];
  loading = true;
  error: string | null = null;

  page = 0;
  readonly pageSize = 20;

  private filters: RankFilterValue = {
    name: '',
    party: '',
    startDate: '',
    endDate: '',
    order: 'desc'
  };

  get totalPages(): number {
    return Math.max(1, Math.ceil(this.items.length / this.pageSize));
  }

  get pageItems(): RankingItem[] {
    const start = this.page * this.pageSize;
    return this.items.slice(start, start + this.pageSize);
  }

  ngOnInit(): void {
    this.load();
  }

  setType(type: RankingType): void {
    this.type = type;
    this.page = 0;
    this.load();
  }

  onFiltersChange(value: RankFilterValue): void {
    this.filters = value;
    this.page = 0;
    this.load();
  }

  onPageChange(page: number): void {
    this.page = page;
  }

  clearFilters(): void {
    this.rankFilters?.reset();
  }

  load(): void {
    this.loading = true;
    this.error = null;
    this.cdr.markForCheck();

    this.rankingService
      .rank(this.type, {
        name: this.filters.name || undefined,
        party: this.filters.party || undefined,
        startDate: this.filters.startDate || undefined,
        endDate: this.filters.endDate || undefined,
        order: this.filters.order
      })
      .subscribe({
        next: (items) => {
          this.items = items ?? [];
          this.loading = false;
          this.cdr.markForCheck();
        },
        error: (err) => {
          console.error(err);
          this.error = 'Não foi possível carregar o ranking.';
          this.loading = false;
          this.cdr.markForCheck();
        }
      });
  }
}
