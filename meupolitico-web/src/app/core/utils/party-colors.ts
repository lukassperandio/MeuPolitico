/**
 * Cores associadas a cada partido, usadas como destaque visual (ex.: faixa
 * no topo dos cards). Não são as marcas oficiais registradas de cada legenda
 * — são as cores mais comumente associadas a cada sigla na cobertura
 * política e na identidade visual histórica de cada partido. Ajuste aqui se
 * algum partido específico precisar de um tom mais preciso.
 */
export const PARTY_COLORS: Record<string, string> = {
  PT: '#db1c23',
  PSDB: '#1351b4',
  MDB: '#168821',
  PL: '#1351b4',
  PP: '#0047ab',
  PSD: '#f28c28',
  PSB: '#ffcd07',
  PDT: '#d81159',
  PSOL: '#f2c811',
  PCDOB: '#c0272d',
  PV: '#2e8b3d',
  REPUBLICANOS: '#1351b4',
  PODEMOS: '#f28c28',
  UNIAO: '#1351b4',
  CIDADANIA: '#f28c28',
  AVANTE: '#f28c28',
  SOLIDARIEDADE: '#e2231a',
  REDE: '#2e8b3d',
  NOVO: '#ff6600',
  PTB: '#c0272d',
  PMB: '#8a2be2'
};

const FALLBACK_COLOR = '#1351b4';

export function getPartyColor(sigla: string | null | undefined): string {
  if (!sigla) return FALLBACK_COLOR;
  const key = sigla.trim().toUpperCase().replace(/[^A-Z]/g, '');
  return PARTY_COLORS[key] ?? FALLBACK_COLOR;
}
