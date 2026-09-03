export interface RankingItem {
  position: number;
  politicianId: number;
  politicianName: string;
  party: string | null;
  state: string | null;
  positionTitle: string | null;
  value: number;
  secondaryValue: number | null;
}
