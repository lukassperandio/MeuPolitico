import { ChangeDetectorRef, Component, DestroyRef, inject } from '@angular/core';
import { CommonModule, CurrencyPipe, DecimalPipe } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, debounceTime, distinctUntilChanged, map, of, switchMap, tap } from 'rxjs';
import { PoliticianService } from '../../../core/services/politician.service';
import { ComparisonService } from '../../../core/services/comparison.service';
import { ComparedPolitician } from '../../../core/models/comparison.model';
import { Politician, SelectedPolitician } from '../../../core/models/politician.model';

@Component({
  selector: 'app-comparison-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, CurrencyPipe, DecimalPipe],
  templateUrl: './comparison-page.html',
  styleUrl: './comparison-page.scss'
})
export class ComparisonPageComponent {
  private readonly politicianService = inject(PoliticianService);
  private readonly comparisonService = inject(ComparisonService);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly destroyRef = inject(DestroyRef);

  readonly maxSelected = 3;

  readonly searchControl = new FormControl('', { nonNullable: true });

  suggestions: Politician[] = [];
  suggestionsLoading = false;
  showSuggestions = false;

  private focused = false;

  selected: SelectedPolitician[] = [];

  politicians: ComparedPolitician[] = [];
  loading = false;
  error: string | null = null;

  constructor() {
    this.searchControl.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        tap(() => {
          this.suggestionsLoading = true;
          this.cdr.markForCheck();
        }),
        switchMap((term) => {
          const t = term.trim();
          if (t.length < 2) {
            this.suggestionsLoading = false;
            return of([] as Politician[]);
          }
          return this.politicianService.findAll(0, 6, t).pipe(
            map((res) => res.content ?? []),
            catchError(() => of([] as Politician[]))
          );
        }),
        takeUntilDestroyed(this.destroyRef)
      )
            .subscribe((results) => {
        this.suggestions = results;
        this.suggestionsLoading = false;

        if (this.focused && this.searchControl.value.trim().length >= 2) {
          this.showSuggestions = true;
        }

        this.cdr.markForCheck();
      });
  }

    onFocus(): void {
    this.focused = true;
    this.showSuggestions = this.suggestions.length > 0;
    this.cdr.markForCheck();
  }

  onBlur(): void {
    this.focused = false;
    this.showSuggestions = false;
    this.cdr.markForCheck();
  }

  add(p: Politician): void {
    if (this.selected.length >= this.maxSelected) return;
    if (this.selected.some((s) => s.id === p.id)) return;

    this.selected = [
      ...this.selected,
      { id: p.id, name: p.name, party: p.party, state: p.state }
    ];
    this.searchControl.setValue('');
    this.suggestions = [];
    this.showSuggestions = false;
    this.error = null;
    this.cdr.markForCheck();
  }

  remove(id: number): void {
    this.selected = this.selected.filter((s) => s.id !== id);
    this.cdr.markForCheck();
  }

  compare(): void {
    if (this.selected.length < 1) {
      this.error = 'Selecione pelo menos 1 político.';
      this.cdr.markForCheck();
      return;
    }

    const ids = this.selected.map((s) => s.id);
    this.loading = true;
    this.error = null;
    this.cdr.markForCheck();

    this.comparisonService.compare(ids).subscribe({
      next: (res) => {
        this.politicians = res.politicians ?? [];
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error(err);
        this.error = 'Erro ao carregar a comparação.';
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }

  get canAdd(): boolean {
    return this.selected.length < this.maxSelected;
  }

  get canCompare(): boolean {
    return this.selected.length > 0;
  }
}
