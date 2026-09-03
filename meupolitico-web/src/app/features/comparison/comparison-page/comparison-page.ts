import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { CommonModule, CurrencyPipe, DecimalPipe } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ComparisonService } from '../../../core/services/comparison.service';
import { ComparedPolitician } from '../../../core/models/comparison.model';

@Component({
  selector: 'app-comparison-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, CurrencyPipe, DecimalPipe],
  templateUrl: './comparison-page.html',
  styleUrl: './comparison-page.scss'
})
export class ComparisonPageComponent {
  private readonly comparisonService = inject(ComparisonService);
  private readonly cdr = inject(ChangeDetectorRef);

  readonly idsControl = new FormControl('1,2', { nonNullable: true });
  politicians: ComparedPolitician[] = [];
  loading = false;
  error: string | null = null;

  compare(): void {
    const ids = this.idsControl.value
      .split(',')
      .map((s) => Number(s.trim()))
      .filter((n) => !Number.isNaN(n));

    if (ids.length < 1 || ids.length > 3) {
      this.error = 'Informe de 1 a 3 IDs (ex.: 1,2,3).';
      this.cdr.markForCheck();
      return;
    }

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
        this.error = 'Erro na comparação.';
        this.loading = false;
        this.cdr.markForCheck();
      }
    });
  }
}
