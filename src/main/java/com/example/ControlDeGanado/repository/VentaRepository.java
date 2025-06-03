package com.example.ControlDeGanado.repository;

import com.example.ControlDeGanado.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    @Query("SELECT v FROM Venta v WHERE v.animal.id = ?1")
    List<Venta> findByAnimalId(Long animalId);

    @Query("SELECT SUM(v.precioTotal) FROM Venta v WHERE v.fecha BETWEEN ?1 AND ?2")
    Double calcularTotalVentasPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin);

    @Query("SELECT SUM(v.pesoTotal) FROM Venta v WHERE v.fecha BETWEEN ?1 AND ?2")
    Double calcularPesoTotalVentasPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin);
}