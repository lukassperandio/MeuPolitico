import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ComparisonResponse } from '../models/comparison.model';

@Injectable({ providedIn: 'root' })
export class ComparisonService {
  private readonly http = inject(HttpClient);

  compare(ids: number[]): Observable<ComparisonResponse> {
    const params = new HttpParams().set('ids', ids.join(','));
    return this.http.get<ComparisonResponse>('/api/comparisons', { params });
  }
}
