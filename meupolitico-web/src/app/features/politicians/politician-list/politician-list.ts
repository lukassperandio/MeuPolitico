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
import { Politician } from '../../../core/models/politician.model';
import { PartyColorPipe } from '../../../shared/pipes/party-color-pipe';
import { PagerComponent } from '../../../shared/components/pager/pager';

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
  imports: [CommonModule, ReactiveFormsModule, RouterLink, PartyColorPipe, PagerComponent],
  templateUrl: './politician-list.html',
  styleUrl: './politician-list.scss'
})
export class PoliticianListComponent {
  private readonly politicianService = inject(PoliticianService);

  readonly pageSize = 20;
  readonly searchControl = new FormControl('', { nonNullable: true });
  private readonly page$ = new BehaviorSubject<number>(0);

  /** termo já com debounce — ao mudar, página volta a 0 */
  private readonly term$ = this.searchControl.valueChanges.pipe(
    startWith(''),
    debounceTime(300),
    distinctUntilChanged(),
    map((t) => t.trim())
  );

  readonly state$: Observable<ListState> = combineLatest([this.term$, this.page$]).pipe(
    switchMap(([term, page]) =>
      this.politicianService.findAll(page, this.pageSize, term || undefined).pipe(
        map((res) => ({
          loading: false,
          error: null as string | null,
          politicians: res.content ?? [],
          page: res.number ?? page,
          totalPages: res.totalPages ?? 0,
          totalElements: res.totalElements ?? 0
        })),
        startWith({
          loading: true,
          error: null as string | null,
          politicians: [] as Politician[],
          page,
          totalPages: 0,
          totalElements: 0
        }),
        catchError((err) => {
          console.error(err);
          return of({
            loading: false,
            error: 'Erro ao carregar políticos.',
            politicians: [] as Politician[],
            page,
            totalPages: 0,
            totalElements: 0
          });
        })
      )
    )
  );

  constructor() {
    this.term$.subscribe(() => {
      if (this.page$.value !== 0) {
        this.page$.next(0);
      }
    });
  }

  onPageChange(page: number): void {
    this.page$.next(page);
  }
}
