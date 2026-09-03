import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RankingItem } from '../models/ranking.model';

@Injectable({ providedIn: 'root' })
export class RankingService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/rankings';



  byExpenses(state?: string, party?: string): Observable<RankingItem[]> {
    let params = new HttpParams();
    if (state) params = params.set('state', state);
    if (party) params = params.set('party', party);
    return this.http.get<RankingItem[]>(`${this.baseUrl}/expenses`, { params });
  }

  byAttendance(state?: string, party?: string): Observable<RankingItem[]> {
    let params = new HttpParams();
    if (state) params = params.set('state', state);
    if (party) params = params.set('party', party);
    return this.http.get<RankingItem[]>(`${this.baseUrl}/attendance`, { params });
  }

  byAssets(state?: string, party?: string, year?: number): Observable<RankingItem[]> {  
    let params = new HttpParams();
    if (state) params = params.set('state', state);
    if (party) params = params.set('party', party);
    if (year != null) params = params.set('year', year);
    return this.http.get<RankingItem[]>(`${this.baseUrl}/assets`, { params });
  }
}
