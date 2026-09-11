import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { PoliticianService } from '../../core/services/politician.service';
import { RankingService } from '../../core/services/ranking.service';
import { RankingItem } from '../../core/models/ranking.model';

@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, CurrencyPipe],
  templateUrl: './home-page.html',
  styleUrl: './home-page.scss'
})
export class HomePageComponent implements OnInit {
  private readonly politicianService = inject(PoliticianService);
  private readonly rankingService = inject(RankingService);
  private readonly router = inject(Router);
  private readonly cdr = inject(ChangeDetectorRef);

  readonly q = new FormControl('', { nonNullable: true });

  totalPoliticians = 0;
  topExpenses: RankingItem[] = [];
  loading = true;

  ngOnInit(): void {
    forkJoin({
      page: this.politicianService.findAll(0, 1).pipe(catchError(() => of(null))),
      ranking: this.rankingService
        .rank('expenses', { order: 'desc' })
        .pipe(catchError(() => of([] as RankingItem[])))
    }).subscribe({
      next: ({ page, ranking }) => {
        this.totalPoliticians = page?.totalElements ?? 0;
        this.topExpenses = (ranking ?? []).slice(0, 5);
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  goSearch(): void {
    const term = this.q.value.trim();
    this.router.navigate(['/politicians'], {
      queryParams: term ? { q: term } : {}
    });
  }
}
