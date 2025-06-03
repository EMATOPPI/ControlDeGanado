package com.example.ControlDeGanado.dto;

import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDate;

public class VentaDTO {
    @NotNull
    private LocalDate fecha;

    @NotNull
    private Double precioTotal;

    @NotNull
    private Double pesoTotal;

    @NotNull
    private Double porcentajeDescuento;

    @NotNull
    private Long animalId;

    private String descripcion;

    // Getters y setters
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Double getPrecioTotal() { return precioTotal; }
    public void setPrecioTotal(Double precioTotal) { this.precioTotal = precioTotal; }

    public Double getPesoTotal() { return pesoTotal; }
    public void setPesoTotal(Double pesoTotal) { this.pesoTotal = pesoTotal; }

    public Double getPorcentajeDescuento() { return porcentajeDescuento; }
    public void setPorcentajeDescuento(Double porcentajeDescuento) { this.porcentajeDescuento = porcentajeDescuento; }

    public Long getAnimalId() { return animalId; }
    public void setAnimalId(Long animalId) { this.animalId = animalId; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
