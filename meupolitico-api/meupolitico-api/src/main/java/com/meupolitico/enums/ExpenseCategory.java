package com.meupolitico.enums;

public enum ExpenseCategory {

    HOUSING_ALLOWANCE("Auxílio-moradia / residência"),
    AIRFARE("Passagem aérea"),
    FUEL("Combustível"),
    VEHICLE_RENTAL("Locação de veículos"),
    OFFICE_MAINTENANCE("Manutenção de escritório"),
    OFFICE_SUPPLIES("Material de escritório"),
    CONSULTING("Consultoria e assessoria"),
    ADVERTISING("Divulgação / publicidade"),
    MAIL("Correios"),
    PHONE_INTERNET("Telefone e internet"),
    RENT("Locação de imóveis (escritório/sede)"),
    SECURITY("Segurança"),
    STAFF_SALARIES("Salários de assessores/gabinete"),
    MEALS("Alimentação"),
    LODGING("Hospedagem (viagens)"),
    TRAINING("Cursos e capacitação"),
    EVENTS("Eventos e representação"),
    LEGAL_FEES("Honorários advocatícios"),
    HEALTH("Plano de saúde / assistência médica"),
    OTHER("Outros");

    private final String description;

    ExpenseCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}