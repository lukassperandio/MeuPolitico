export const EXPENSE_CATEGORY_LABELS: Record<string, string> = {
  HOUSING_ALLOWANCE: 'Auxílio-moradia',
  AIRFARE: 'Passagem aérea',
  FUEL: 'Combustível',
  VEHICLE_RENTAL: 'Locação de veículos',
  GROUND_TRANSPORT: 'Táxi, pedágio e transporte',
  OFFICE_MAINTENANCE: 'Manutenção de escritório',
  OFFICE_SUPPLIES: 'Material de escritório',
  CONSULTING: 'Consultoria e assessoria',
  ADVERTISING: 'Divulgação / publicidade',
  MAIL: 'Correios',
  PHONE_INTERNET: 'Telefone e internet',
  RENT: 'Locação de imóveis',
  SECURITY: 'Segurança',
  STAFF_SALARIES: 'Salários de gabinete',
  MEALS: 'Alimentação',
  LODGING: 'Hospedagem',
  TRAINING: 'Cursos e capacitação',
  EVENTS: 'Eventos',
  LEGAL_FEES: 'Honorários advocatícios',
  HEALTH: 'Saúde / plano de saúde',
  OTHER: 'Outros'
};

export function expenseCategoryLabel(category: string | null | undefined): string {
  if (!category) {
    return '—';
  }
  return EXPENSE_CATEGORY_LABELS[category] ?? category;
}
