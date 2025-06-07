package com.example.ControlDeGanado.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.time.LocalDate;

public class VentaDTO {
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "El precio total es obligatorio")
    @Positive(message = "El precio debe ser mayor que cero")
    private Double precioTotal;

    @NotNull(message = "El peso total es obligatorio")
    @Positive(message = "El peso debe ser mayor que cero")
    private Double pesoTotal;

    @NotNull(message = "El porcentaje de descuento es obligatorio")
    @DecimalMin(value = "0.0", message = "El porcentaje de descuento no puede ser negativo")
    @DecimalMax(value = "100.0", message = "El porcentaje de descuento no puede ser mayor a 100")
    private Double porcentajeDescuento;

    @NotNull(message = "El animal es obligatorio")
    private Long animalId;

    private String descripcion;

    // Constructor vacío
    public VentaDTO() {}

    // Constructor con parámetros
    public VentaDTO(LocalDate fecha, Double precioTotal, Double pesoTotal,
                    Double porcentajeDescuento, Long animalId, String descripcion) {
        this.fecha = fecha;
        this.precioTotal = precioTotal;
        this.pesoTotal = pesoTotal;
        this.porcentajeDescuento = porcentajeDescuento;
        this.animalId = animalId;
        this.descripcion = descripcion;
    }

    // Getters y setters
    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(Double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public Double getPesoTotal() {
        return pesoTotal;
    }

    public void setPesoTotal(Double pesoTotal) {
        this.pesoTotal = pesoTotal;
    }

    public Double getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public void setPorcentajeDescuento(Double porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
    }

    public Long getAnimalId() {
        return animalId;
    }

    public void setAnimalId(Long animalId) {
        this.animalId = animalId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "VentaDTO{" +
                "fecha=" + fecha +
                ", precioTotal=" + precioTotal +
                ", pesoTotal=" + pesoTotal +
                ", porcentajeDescuento=" + porcentajeDescuento +
                ", animalId=" + animalId +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}