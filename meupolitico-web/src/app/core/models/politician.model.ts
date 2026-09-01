export interface Politician {
  id: number;
  externalId: string | null;
  name: string;
  ballotName: string | null;
  photoUrl: string | null;
  party: string | null;
  state: string | null;
  position: string | null;
  status: string | null;
  birthDate: string | null;
  gender: string | null;
  createdAt: string | null;
  updatedAt: string | null;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}
