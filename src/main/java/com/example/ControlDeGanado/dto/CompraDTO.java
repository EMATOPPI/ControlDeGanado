package com.example.ControlDeGanado.dto;

import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDate;

public class CompraDTO {
    @NotNull
    private LocalDate fecha;

    @NotNull
    private Double precioTotal;

    @NotNull
    private Long animalId;

    private String descripcion;

    // Getters y setters
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Double getPrecioTotal() { return precioTotal; }
    public void setPrecioTotal(Double precioTotal) { this.precioTotal = precioTotal; }

    public Long getAnimalId() { return animalId; }
    public void setAnimalId(Long animalId) { this.animalId = animalId; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
