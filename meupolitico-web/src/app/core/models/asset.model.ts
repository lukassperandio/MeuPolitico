export interface AssetEvolutionPoint {
  year: number;
  declaredValue: number;
  variationAmount: number | null;
  variationPercentage: number | null;
}

export interface AssetEvolution {
  politicianId: number;
  politicianName: string;
  points: AssetEvolutionPoint[];
}
