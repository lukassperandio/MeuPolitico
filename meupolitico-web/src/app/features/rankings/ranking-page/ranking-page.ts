import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe, DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { RankingService } from '../../../core/services/ranking.service';
import { RankingItem } from '../../../core/models/ranking.model';

type RankingType = 'expenses' | 'attendance' | 'assets';

@Component({
  selector: 'app-ranking-page',
  standalone: true,
  imports: [CommonModule, RouterLink, CurrencyPipe, DecimalPipe],
  templateUrl: './ranking-page.html',
  styleUrl: './ranking-page.scss'
})
export class RankingPageComponent implements OnInit {
  private readonly rankingService = inject(RankingService);
  private readonly cdr = inject(ChangeDetectorRef);

  type: RankingType = 'expenses';
  items: RankingItem[] = [];
  loading = true;
  error: string | null = null;

  ngOnInit(): void {
    this.load();
  }

  setType(type: RankingType): void {
    this.type = type;
    this.load();
  }

  load(): void {
    this.loading = true;
    this.error = null;
    this.cdr.markForCheck();

    const req$ =
      this.type === 'expenses'
        ? this.rankingService.byExpenses()
        : this.type === 'attendance'
          ? this.rankingService.byAttendance()
          : this.rankingService.byAssets();

    req$.subscribe({
      next: (items) => {
        this.items = items ?? [];
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error(err);
        this.error = 'Erro ao carregar ranking.';
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }
}
