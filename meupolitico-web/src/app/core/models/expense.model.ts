export interface Expense {
  id: number;
  politicianId: number;
  politicianName: string;
  amount: number;
  date: string;
  category: string;
  supplier: string | null;
  documentNumber: string | null;
  description: string | null;
  source: string | null;
  createdAt: string | null;
}
