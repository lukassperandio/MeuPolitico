export interface AttendanceSummary {
  politicianId: number;
  politicianName: string;
  totalSessions: number;
  present: number;
  attendancePercentage: number;
  mandateStart: string | null;
  dataComplete: boolean;
}
