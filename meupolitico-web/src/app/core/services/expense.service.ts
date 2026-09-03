import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Expense } from '../models/expense.model';
import { Page } from '../models/politician.model';

@Injectable({
  providedIn: 'root'
})
export class ExpenseService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/expenses';

  findByPoliticianId(politicianId: number): Observable<Expense[]> {
    return this.http.get<Expense[]>(`${this.baseUrl}/politician/${politicianId}`);
  }

  searchByPolitician(
    politicianId: number,
    page = 0,
    size = 20,
    sort = 'date,desc'
  ): Observable<Page<Expense>> {
    const params = new HttpParams()
      .set('politicianId', politicianId)
      .set('page', page)
      .set('size', size)
      .set('sort', sort);

    return this.http.get<Page<Expense>>(`${this.baseUrl}/search`, { params });
  }
}
