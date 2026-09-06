package com.example.backend.repository

import com.example.backend.entity.Especie
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EspecieRepository : JpaRepository<Especie, Long> {
}
