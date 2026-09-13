package com.example.backend.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "compradores")
class Comprador(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "nome", nullable = false)
    var nome: String,

    @Column(name = "cpf")
    var cpf: String? = null,

    @Column(name = "email")
    var email: String? = null,

    @Column(name = "telefone")
    var telefone: String? = null,

    @Column(name = "observacoes", columnDefinition = "TEXT")
    var observacoes: String? = null,

    @Column(name = "criado_em", nullable = false, updatable = false)
    var criadoEm: LocalDateTime = LocalDateTime.now(),

    @Column(name = "atualizado_em", nullable = false)
    var atualizadoEm: LocalDateTime = LocalDateTime.now(),

    @ManyToMany(mappedBy = "compradores")
    var lojas: MutableList<Loja> = mutableListOf(),

    @OneToMany(mappedBy = "comprador", cascade = [CascadeType.ALL], orphanRemoval = false)
    var vendas: MutableList<Venda> = mutableListOf()
) {

    @PreUpdate
    fun preAtualizar() {
        atualizadoEm = LocalDateTime.now()
    }
}
