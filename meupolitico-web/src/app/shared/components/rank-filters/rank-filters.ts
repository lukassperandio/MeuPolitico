import {
  Component,
  DestroyRef,
  EventEmitter,
  OnInit,
  Output,
  inject
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

export interface RankFilterValue {
  name: string;
  party: string;
  startDate: string;
  endDate: string;
  order: 'asc' | 'desc';
}

@Component({
  selector: 'app-rank-filters',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './rank-filters.html',
  styleUrl: './rank-filters.scss'
})
export class RankFiltersComponent implements OnInit {
  private readonly destroyRef = inject(DestroyRef);

  @Output() filtersChange = new EventEmitter<RankFilterValue>();

  readonly form = new FormGroup({
    name: new FormControl('', { nonNullable: true }),
    party: new FormControl('', { nonNullable: true }),
    startDate: new FormControl('', { nonNullable: true }),
    endDate: new FormControl('', { nonNullable: true }),
    order: new FormControl<'asc' | 'desc'>('desc', { nonNullable: true })
  });

  ngOnInit(): void {
    this.form.valueChanges
      .pipe(
        debounceTime(350),
        distinctUntilChanged(),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(() => this.emit());
  }

  apply(): void {
    this.emit();
  }

  clear(): void {
    this.form.reset({
      name: '',
      party: '',
      startDate: '',
      endDate: '',
      order: 'desc'
    });
    this.emit();
  }

  private emit(): void {
    const v = this.form.getRawValue();
    this.filtersChange.emit({
      name: v.name.trim(),
      party: v.party.trim(),
      startDate: v.startDate,
      endDate: v.endDate,
      order: v.order
    });
  }
}
