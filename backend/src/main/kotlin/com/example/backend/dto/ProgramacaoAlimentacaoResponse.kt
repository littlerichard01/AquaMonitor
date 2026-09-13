package com.example.backend.dto

import com.example.backend.entity.ProgramacaoAlimentacao
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "Resposta contendo uma ProgramacaoAlimentacao.")
data class ProgramacaoAlimentacaoResponse(
    @Schema(description = "Identificador unico", example = "1")
    val id: Long,

    @Schema(description = "Horario no formato HH:mm (24h)", example = "08:00")
    val horario: String,

    @Schema(description = "Descricao opcional", nullable = true, example = "Alimentacao da manha")
    val descricao: String?,

    @Schema(description = "Se esta ativa", example = "true")
    val ativo: Boolean,

    @Schema(description = "Resumo do Aquario associado")
    val aquario: AquarioResumo,

    @Schema(description = "Data e hora do cadastro")
    val criadoEm: LocalDateTime,

    @Schema(description = "Data e hora da ultima atualizacao")
    val atualizadoEm: LocalDateTime
) {
    companion object {
        fun de(entidade: ProgramacaoAlimentacao): ProgramacaoAlimentacaoResponse {
            return ProgramacaoAlimentacaoResponse(
                id = entidade.id!!,
                horario = entidade.horario,
                descricao = entidade.descricao,
                ativo = entidade.ativo,
                aquario = AquarioResumo(
                    id = entidade.aquario.id!!,
                    nome = entidade.aquario.nome,
                    status = entidade.aquario.status
                ),
                criadoEm = entidade.criadoEm,
                atualizadoEm = entidade.atualizadoEm
            )
        }
    }
}
