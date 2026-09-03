export interface AttendanceSummary {
  politicianId: number;
  politicianName: string;
  totalSessions: number;
  present: number;
  absent: number;
  justified: number;
  attendancePercentage: number;
}
