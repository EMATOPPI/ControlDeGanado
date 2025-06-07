package com.example.ControlDeGanado.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;

public class CompraDTO {

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @NotNull(message = "El precio total es obligatorio")
    @Positive(message = "El precio total debe ser positivo")
    private Double precioTotal;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    @NotNull(message = "El ID del animal es obligatorio")
    private Long animalId;

    private String descripcion;

    // Constructor vacío
    public CompraDTO() {}

    // Constructor con parámetros
    public CompraDTO(LocalDate fecha, Double precioTotal, Integer cantidad, Long animalId, String descripcion) {
        this.fecha = fecha;
        this.precioTotal = precioTotal;
        this.cantidad = cantidad;
        this.animalId = animalId;
        this.descripcion = descripcion;
    }

    // Getters y setters
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Double getPrecioTotal() { return precioTotal; }
    public void setPrecioTotal(Double precioTotal) { this.precioTotal = precioTotal; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Long getAnimalId() { return animalId; }
    public void setAnimalId(Long animalId) { this.animalId = animalId; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() {
        return "CompraDTO{" +
                "fecha=" + fecha +
                ", precioTotal=" + precioTotal +
                ", cantidad=" + cantidad +
                ", animalId=" + animalId +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}