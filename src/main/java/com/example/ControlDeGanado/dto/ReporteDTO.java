package com.example.ControlDeGanado.dto;

import java.time.LocalDate;

public class ReporteDTO {
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer totalAnimales;
    private Integer totalCompras;
    private Integer totalVentas;
    private Double montoTotalCompras;
    private Double montoTotalVentas;
    private Double pesoTotalVentas;
    private Double rentabilidad;

    // Constructor vacío
    public ReporteDTO() {}

    // Constructor con parámetros
    public ReporteDTO(LocalDate fechaInicio, LocalDate fechaFin) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    // Getters y Setters
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public Integer getTotalAnimales() { return totalAnimales; }
    public void setTotalAnimales(Integer totalAnimales) { this.totalAnimales = totalAnimales; }

    public Integer getTotalCompras() { return totalCompras; }
    public void setTotalCompras(Integer totalCompras) { this.totalCompras = totalCompras; }

    public Integer getTotalVentas() { return totalVentas; }
    public void setTotalVentas(Integer totalVentas) { this.totalVentas = totalVentas; }

    public Double getMontoTotalCompras() { return montoTotalCompras; }
    public void setMontoTotalCompras(Double montoTotalCompras) { this.montoTotalCompras = montoTotalCompras; }

    public Double getMontoTotalVentas() { return montoTotalVentas; }
    public void setMontoTotalVentas(Double montoTotalVentas) { this.montoTotalVentas = montoTotalVentas; }

    public Double getPesoTotalVentas() { return pesoTotalVentas; }
    public void setPesoTotalVentas(Double pesoTotalVentas) { this.pesoTotalVentas = pesoTotalVentas; }

    public Double getRentabilidad() { return rentabilidad; }
    public void setRentabilidad(Double rentabilidad) { this.rentabilidad = rentabilidad; }
}