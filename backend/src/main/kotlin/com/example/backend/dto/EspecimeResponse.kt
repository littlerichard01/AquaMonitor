package com.example.backend.dto

import com.example.backend.entity.Especime
import com.example.backend.entity.StatusAquario
import com.example.backend.entity.StatusEspecime
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Schema(description = "Resposta contendo os dados de um Especime com resumos dos relacionamentos.")
data class EspecimeResponse(
    @Schema(description = "Identificador unico do especime", example = "1")
    val id: Long,

    @Schema(description = "Codigo unico na loja", example = "PEIXE-034")
    val identificacao: String,

    @Schema(description = "Valor de venda", example = "120.00")
    val preco: BigDecimal,

    @Schema(description = "Status: DISPONIVEL / VENDIDO / INDISPONIVEL", example = "DISPONIVEL")
    val status: StatusEspecime,

    @Schema(description = "Observacoes individuais", nullable = true, example = "Machadinho, cor vibrante")
    val observacoes: String?,

    @Schema(description = "Data de entrada na loja", example = "2026-09-10")
    val dataEntrada: LocalDate,

    @Schema(description = "Resumo da Especie associada")
    val especie: EspecieResumo,

    @Schema(description = "Resumo do Aquario alocado. NULL se ainda nao alocado", nullable = true)
    val aquario: AquarioResumo?,

    @Schema(description = "ID da Loja dona", example = "1")
    val lojaId: Long,

    @Schema(description = "Data e hora do cadastro")
    val criadoEm: LocalDateTime,

    @Schema(description = "Data e hora da ultima atualizacao")
    val atualizadoEm: LocalDateTime
) {
    companion object {
        fun de(entidade: Especime): EspecimeResponse {
            return EspecimeResponse(
                id = entidade.id!!,
                identificacao = entidade.identificacao,
                preco = entidade.preco,
                status = entidade.status,
                observacoes = entidade.observacoes,
                dataEntrada = entidade.dataEntrada,
                especie = EspecieResumo(
                    id = entidade.especie.id!!,
                    nomePopular = entidade.especie.nomePopular,
                    tipo = entidade.especie.tipo
                ),
                aquario = entidade.aquario?.let {
                    AquarioResumo(
                        id = it.id!!,
                        nome = it.nome,
                        status = it.status
                    )
                },
                lojaId = entidade.loja.id!!,
                criadoEm = entidade.criadoEm,
                atualizadoEm = entidade.atualizadoEm
            )
        }
    }
}

@Schema(description = "Resumo simplificado de uma Especie (para embutir em outras responses).")
data class EspecieResumo(
    val id: Long,
    val nomePopular: String,
    val tipo: com.example.backend.entity.TipoEspecie
)

@Schema(description = "Resumo simplificado de um Aquario (para embutir em outras responses).")
data class AquarioResumo(
    val id: Long,
    val nome: String,
    val status: StatusAquario
)
