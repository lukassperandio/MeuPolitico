import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AttendanceSummary } from '../models/attendance.model';

@Injectable({ providedIn: 'root' })
export class AttendanceService {
  private readonly http = inject(HttpClient);

  getSummary(politicianId: number): Observable<AttendanceSummary> {
    return this.http.get<AttendanceSummary>(
      `/api/attendances/politician/${politicianId}/summary`
    );
  }
}
