import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AssetEvolution } from '../models/asset.model';

@Injectable({ providedIn: 'root' })
export class AssetService {
  private readonly http = inject(HttpClient);

  getEvolution(politicianId: number): Observable<AssetEvolution> {
    return this.http.get<AssetEvolution>(
      `/api/assets/politician/${politicianId}/evolution`
    );
  }
}
