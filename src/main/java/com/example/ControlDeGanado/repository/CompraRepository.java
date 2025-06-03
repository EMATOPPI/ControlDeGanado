package com.example.ControlDeGanado.repository;

import com.example.ControlDeGanado.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {

    List<Compra> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    @Query("SELECT c FROM Compra c WHERE c.animal.id = ?1")
    List<Compra> findByAnimalId(Long animalId);

    @Query("SELECT SUM(c.precioTotal) FROM Compra c WHERE c.fecha BETWEEN ?1 AND ?2")
    Double calcularTotalComprasPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin);
}