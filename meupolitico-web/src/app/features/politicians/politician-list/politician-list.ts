import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import {
  BehaviorSubject,
  catchError,
  combineLatest,
  debounceTime,
  distinctUntilChanged,
  map,
  of,
  startWith,
  switchMap,
  Observable
} from 'rxjs';
import { PoliticianService } from '../../../core/services/politician.service';
import { Page, Politician } from '../../../core/models/politician.model';
import { PartyColorPipe } from '../../../shared/pipes/party-color-pipe';

interface ListState {
  loading: boolean;
  error: string | null;
  politicians: Politician[];
  page: number;
  totalPages: number;
  totalElements: number;
}

@Component({
  selector: 'app-politician-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, PartyColorPipe],
  templateUrl: './politician-list.html',
  styleUrl: './politician-list.scss'
})
export class PoliticianListComponent {
  private readonly politicianService = inject(PoliticianService);

  readonly pageSize = 20;
  readonly searchControl = new FormControl('', { nonNullable: true });
  private readonly page$ = new BehaviorSubject<number>(0);
  private lastTerm = '';

  constructor() {
    this.searchControl.valueChanges
      .pipe(debounceTime(300), distinctUntilChanged())
      .subscribe(() => this.page$.next(0));
  }

  readonly state$: Observable<ListState> = combineLatest([
    this.searchControl.valueChanges.pipe(
      startWith(''),
      debounceTime(300),
      distinctUntilChanged()
    ),
    this.page$
  ]).pipe(
    switchMap(([term, page]) => {
      const q = term.trim();
      let pageToUse = page;

      if (q !== this.lastTerm) {
        this.lastTerm = q;
        pageToUse = 0;
        if (page !== 0) {
          this.page$.next(0);
          return of({
            loading: true,
            error: null,
            politicians: [] as Politician[],
            page: 0,
            totalPages: 0,
            totalElements: 0
          });
        }
      }

      return this.politicianService.findAll(pageToUse, this.pageSize, q || undefined).pipe(
        map((res) => ({
          loading: false,
          error: null,
          politicians: res.content ?? [],
          page: res.number ?? pageToUse,
          totalPages: res.totalPages ?? 0,
          totalElements: res.totalElements ?? 0
        })),
        startWith({
          loading: true,
          error: null,
          politicians: [] as Politician[],
          page: pageToUse,
          totalPages: 0,
          totalElements: 0
        }),
        catchError((err) => {
          console.error(err);
          return of({
            loading: false,
            error: 'Erro ao carregar políticos.',
            politicians: [] as Politician[],
            page: pageToUse,
            totalPages: 0,
            totalElements: 0
          });
        })
      );
    })
  );

  prev(currentPage: number): void {
    if (currentPage > 0) {
      this.page$.next(currentPage - 1);
    }
  }

  next(currentPage: number, totalPages: number): void {
    if (currentPage + 1 < totalPages) {
      this.page$.next(currentPage + 1);
    }
  }

  pageNumbers(totalPages: number): number[] {
    return Array.from({ length: totalPages }, (_, i) => i);
  }

  goToPage(event: Event): void {
    const value = Number((event.target as HTMLSelectElement).value);
    if (!Number.isNaN(value)) {
      this.page$.next(value);
    }
  }
}
