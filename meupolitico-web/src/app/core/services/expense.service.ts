// expense.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Expense } from '../models/expense.model';
import { Page } from '../models/politician.model';

export interface ExpenseSearchParams {
  politicianId: number;
  page?: number;
  size?: number;
  sort?: string;           // ex: 'date,desc' | 'amount,asc' | 'supplier,asc'
  supplier?: string;
  startDate?: string;      // yyyy-MM-dd
  endDate?: string;
  minAmount?: number;
  maxAmount?: number;      // se o backend não tiver max, ignoramos no front ou usas min só
}

@Injectable({ providedIn: 'root' })
export class ExpenseService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/expenses';

  search(params: ExpenseSearchParams): Observable<Page<Expense>> {
    let httpParams = new HttpParams()
      .set('politicianId', params.politicianId)
      .set('page', params.page ?? 0)
      .set('size', params.size ?? 20)
      .set('sort', params.sort ?? 'date,desc');

    if (params.supplier?.trim()) {
      httpParams = httpParams.set('supplier', params.supplier.trim());
    }
    if (params.startDate) {
      httpParams = httpParams.set('startDate', params.startDate);
    }
    if (params.endDate) {
      httpParams = httpParams.set('endDate', params.endDate);
    }
    if (params.minAmount != null && !Number.isNaN(params.minAmount)) {
      httpParams = httpParams.set('minAmount', params.minAmount);
    }

    return this.http.get<Page<Expense>>(`${this.baseUrl}/search`, { params: httpParams });
  }
}
