package com.example.backend.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

@Schema(description = "Requisicao para criar ou atualizar ConfiguracaoTemperatura (1:1 com Aquario).")
data class ConfiguracaoTemperaturaRequest(
    @field:NotNull(message = "Temperatura minima e obrigatoria")
    @Schema(description = "Limite inferior aceitavel em graus Celsius", example = "24.00")
    val temperaturaMinima: BigDecimal,

    @field:NotNull(message = "Temperatura maxima e obrigatoria")
    @Schema(description = "Limite superior aceitavel em graus Celsius", example = "28.00")
    val temperaturaMaxima: BigDecimal,

    @Schema(description = "Se a configuracao esta ativa. Default = true", example = "true", nullable = true)
    val ativo: Boolean? = true,

    @field:NotNull(message = "ID do aquario e obrigatorio")
    @Schema(description = "ID do Aquario (relacionamento 1:1 — cada aquario tem exatamente UMA configuracao)", example = "3")
    val aquarioId: Long
)
