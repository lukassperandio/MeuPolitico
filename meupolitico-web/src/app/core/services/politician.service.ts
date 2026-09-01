import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Page, Politician } from '../models/politician.model';

@Injectable({
  providedIn: 'root'
})
export class PoliticianService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/politicians';

  findAll(page = 0, size = 20): Observable<Page<Politician>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size);

    return this.http.get<Page<Politician>>(this.baseUrl, { params });
  }

  findById(id: number): Observable<Politician> {
    return this.http.get<Politician>(`${this.baseUrl}/${id}`);
  }

  searchByName(name: string): Observable<Politician[]> {
    const params = new HttpParams().set('name', name);
    return this.http.get<Politician[]>(`${this.baseUrl}/search/name`, { params });
  }
}
