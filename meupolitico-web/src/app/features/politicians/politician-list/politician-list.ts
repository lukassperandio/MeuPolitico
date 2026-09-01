import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import {
  catchError,
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

interface ListState {
  loading: boolean;
  error: string | null;
  politicians: Politician[];
}

@Component({
  selector: 'app-politician-list',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './politician-list.html',
  styleUrl: './politician-list.scss'
})
export class PoliticianListComponent {
  private readonly politicianService = inject(PoliticianService);

  readonly searchControl = new FormControl('', { nonNullable: true });

  readonly state$: Observable<ListState> = this.searchControl.valueChanges.pipe(
    startWith(''),
    debounceTime(300),
    distinctUntilChanged(),
    switchMap((term) => {
      const q = term.trim();

      const request$ = q.length === 0
        ? this.politicianService.findAll(0, 20).pipe(
            map((page: Page<Politician>) => page.content ?? [])
          )
        : this.politicianService.searchByName(q);

      return request$.pipe(
        map((politicians) => ({
          loading: false,
          error: null,
          politicians
        })),
        startWith({
          loading: true,
          error: null,
          politicians: [] as Politician[]
        }),
        catchError((err) => {
          console.error(err);
          return of({
            loading: false,
            error: 'Erro ao carregar políticos.',
            politicians: [] as Politician[]
          });
        })
      );
    })
  );
}
