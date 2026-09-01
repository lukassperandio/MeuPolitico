import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { catchError, map, of, switchMap, Observable, startWith } from 'rxjs';
import { PoliticianService } from '../../../core/services/politician.service';
import { Politician } from '../../../core/models/politician.model';

interface DetailState {
  loading: boolean;
  error: string | null;
  politician: Politician | null;
}

@Component({
  selector: 'app-politician-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './politician-detail.html',
  styleUrl: './politician-detail.scss'
})
export class PoliticianDetailComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly politicianService = inject(PoliticianService);

  readonly state$: Observable<DetailState> = this.route.paramMap.pipe(
    map((params) => Number(params.get('id'))),
    switchMap((id) => {
      if (!id || Number.isNaN(id)) {
        return of({
          loading: false,
          error: 'ID inválido.',
          politician: null
        });
      }

      return this.politicianService.findById(id).pipe(
        map((politician) => ({
          loading: false,
          error: null,
          politician
        })),
        startWith({
          loading: true,
          error: null,
          politician: null as Politician | null
        }),
        catchError((err) => {
          console.error(err);
          return of({
            loading: false,
            error: 'Político não encontrado.',
            politician: null
          });
        })
      );
    })
  );
}
