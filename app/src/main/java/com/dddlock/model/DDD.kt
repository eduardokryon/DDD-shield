package com.dddlock.model

/**
 * DDDLock
 * Criado por Eduardo Brito
 * GitHub: @eduardokryon
 */
data class DDD(
    val code: String,
    val city: String,
    val state: String
) {
    val displayName: String get() = "$code - $city ($state)"
    val codeWithState: String get() = "$code ($state)"
}

val allDDDs: List<DDD> = listOf(
    // São Paulo
    DDD("11", "São Paulo", "SP"),
    DDD("12", "São José dos Campos", "SP"),
    DDD("13", "Santos", "SP"),
    DDD("14", "Bauru", "SP"),
    DDD("15", "Sorocaba", "SP"),
    DDD("16", "Ribeirão Preto", "SP"),
    DDD("17", "São José do Rio Preto", "SP"),
    DDD("18", "Presidente Prudente", "SP"),
    DDD("19", "Campinas", "SP"),

    // Rio de Janeiro
    DDD("21", "Rio de Janeiro", "RJ"),
    DDD("22", "Campos dos Goytacazes", "RJ"),
    DDD("24", "Volta Redonda", "RJ"),

    // Espírito Santo
    DDD("27", "Vitória", "ES"),
    DDD("28", "Cachoeiro de Itapemirim", "ES"),

    // Minas Gerais
    DDD("31", "Belo Horizonte", "MG"),
    DDD("32", "Juiz de Fora", "MG"),
    DDD("33", "Governador Valadares", "MG"),
    DDD("34", "Uberlândia", "MG"),
    DDD("35", "Poços de Caldas", "MG"),
    DDD("37", "Divinópolis", "MG"),
    DDD("38", "Montes Claros", "MG"),

    // Paraná
    DDD("41", "Curitiba", "PR"),
    DDD("42", "Ponta Grossa", "PR"),
    DDD("43", "Londrina", "PR"),
    DDD("44", "Maringá", "PR"),
    DDD("45", "Foz do Iguaçu", "PR"),
    DDD("46", "Francisco Beltrão", "PR"),

    // Santa Catarina
    DDD("47", "Joinville", "SC"),
    DDD("48", "Florianópolis", "SC"),
    DDD("49", "Chapecó", "SC"),

    // Rio Grande do Sul
    DDD("51", "Porto Alegre", "RS"),
    DDD("53", "Pelotas", "RS"),
    DDD("54", "Caxias do Sul", "RS"),
    DDD("55", "Santa Maria", "RS"),

    // Distrito Federal
    DDD("61", "Brasília", "DF"),

    // Goiás
    DDD("62", "Goiânia", "GO"),
    DDD("64", "Rio Verde", "GO"),

    // Tocantins
    DDD("63", "Palmas", "TO"),

    // Mato Grosso
    DDD("65", "Cuiabá", "MT"),
    DDD("66", "Rondonópolis", "MT"),

    // Mato Grosso do Sul
    DDD("67", "Campo Grande", "MS"),

    // Acre
    DDD("68", "Rio Branco", "AC"),

    // Rondônia
    DDD("69", "Porto Velho", "RO"),

    // Bahia
    DDD("71", "Salvador", "BA"),
    DDD("73", "Ilhéus", "BA"),
    DDD("74", "Juazeiro", "BA"),
    DDD("75", "Feira de Santana", "BA"),
    DDD("77", "Barreiras", "BA"),

    // Sergipe
    DDD("79", "Aracaju", "SE"),

    // Pernambuco
    DDD("81", "Recife", "PE"),
    DDD("87", "Petrolina", "PE"),

    // Alagoas
    DDD("82", "Maceió", "AL"),

    // Paraíba
    DDD("83", "João Pessoa", "PB"),

    // Rio Grande do Norte
    DDD("84", "Natal", "RN"),

    // Ceará
    DDD("85", "Fortaleza", "CE"),
    DDD("88", "Juazeiro do Norte", "CE"),

    // Piauí
    DDD("86", "Teresina", "PI"),
    DDD("89", "Picos", "PI"),

    // Pará
    DDD("91", "Belém", "PA"),
    DDD("93", "Santarém", "PA"),
    DDD("94", "Marabá", "PA"),

    // Amazonas
    DDD("92", "Manaus", "AM"),
    DDD("97", "Coari", "AM"),

    // Roraima
    DDD("95", "Boa Vista", "RR"),

    // Amapá
    DDD("96", "Macapá", "AP"),

    // Maranhão
    DDD("98", "São Luís", "MA"),
    DDD("99", "Imperatriz", "MA"),
)

val allDDDsByCode: Map<String, DDD> = allDDDs.associateBy { it.code }
