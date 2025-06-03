package com.example.ControlDeGanado.controller;

import com.example.ControlDeGanado.dto.ReporteDTO;
import com.example.ControlDeGanado.dto.TopAnimalDTO;
import com.example.ControlDeGanado.service.ReporteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @GetMapping("/resumen")
    public ResponseEntity<ReporteDTO> obtenerResumen(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        // Si no se proporcionan fechas, usar el mes actual
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().withDayOfMonth(1);
        }
        if (fechaFin == null) {
            fechaFin = LocalDate.now();
        }

        ReporteDTO reporte = reporteService.generarReportePorPeriodo(fechaInicio, fechaFin);
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/animales/top-ventas")
    public ResponseEntity<List<TopAnimalDTO>> obtenerTopAnimalesPorVentas(
            @RequestParam(required = false, defaultValue = "5") Integer limite,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        // Si no se proporcionan fechas, usar el último año
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().minusYears(1);
        }
        if (fechaFin == null) {
            fechaFin = LocalDate.now();
        }

        List<TopAnimalDTO> topAnimales = reporteService.obtenerTopAnimalesPorVentas(limite, fechaInicio, fechaFin);
        return ResponseEntity.ok(topAnimales);
    }

    @GetMapping("/animales/top-compras")
    public ResponseEntity<List<TopAnimalDTO>> obtenerTopAnimalesPorCompras(
            @RequestParam(required = false, defaultValue = "5") Integer limite,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        // Si no se proporcionan fechas, usar el último año
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().minusYears(1);
        }
        if (fechaFin == null) {
            fechaFin = LocalDate.now();
        }

        List<TopAnimalDTO> topAnimales = reporteService.obtenerTopAnimalesPorCompras(limite, fechaInicio, fechaFin);
        return ResponseEntity.ok(topAnimales);
    }

    @GetMapping("/rentabilidad")
    public ResponseEntity<Double> obtenerRentabilidad(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        // Si no se proporcionan fechas, usar el año actual
        if (fechaInicio == null) {
            fechaInicio = LocalDate.now().withDayOfYear(1);
        }
        if (fechaFin == null) {
            fechaFin = LocalDate.now();
        }

        Double rentabilidad = reporteService.calcularRentabilidad(fechaInicio, fechaFin);
        return ResponseEntity.ok(rentabilidad);
    }
}