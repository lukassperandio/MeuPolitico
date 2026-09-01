import { Pipe, PipeTransform } from '@angular/core';
import { expenseCategoryLabel } from '../../core/constants/expense-category-labels';

@Pipe({
  name: 'expenseCategoryLabel',
  standalone: true
})
export class ExpenseCategoryLabelPipe implements PipeTransform {
  transform(value: string | null | undefined): string {
    return expenseCategoryLabel(value);
  }
}
