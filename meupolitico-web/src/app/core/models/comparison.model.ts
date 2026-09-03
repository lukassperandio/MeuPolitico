export interface ComparedPolitician {
  id: number;
  name: string;
  party: string | null;
  state: string | null;
  position: string | null;
  totalExpenses: number;
  attendancePercentage: number;
  latestAssetValue: number | null;
  assetYear: number | null;
}

export interface ComparisonResponse {
  politicians: ComparedPolitician[];
}
