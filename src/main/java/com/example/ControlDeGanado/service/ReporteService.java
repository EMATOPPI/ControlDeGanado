package com.example.ControlDeGanado.service;

import com.example.ControlDeGanado.dto.ReporteDTO;
import com.example.ControlDeGanado.dto.TopAnimalDTO;
import com.example.ControlDeGanado.model.Animal;
import com.example.ControlDeGanado.model.Compra;
import com.example.ControlDeGanado.model.Venta;
import com.example.ControlDeGanado.repository.AnimalRepository;
import com.example.ControlDeGanado.repository.CompraRepository;
import com.example.ControlDeGanado.repository.VentaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    private final AnimalRepository animalRepository;
    private final CompraRepository compraRepository;
    private final VentaRepository ventaRepository;

    public ReporteService(AnimalRepository animalRepository,
                          CompraRepository compraRepository,
                          VentaRepository ventaRepository) {
        this.animalRepository = animalRepository;
        this.compraRepository = compraRepository;
        this.ventaRepository = ventaRepository;
    }

    public ReporteDTO generarReportePorPeriodo(LocalDate fechaInicio, LocalDate fechaFin) {
        ReporteDTO reporte = new ReporteDTO(fechaInicio, fechaFin);

        // Obtener total de animales
        reporte.setTotalAnimales((int) animalRepository.count());

        // Obtener compras en el período
        List<Compra> compras = compraRepository.findByFechaBetween(fechaInicio, fechaFin);
        reporte.setTotalCompras(compras.size());

        // Calcular monto total de compras
        Double montoCompras = compras.stream()
                .mapToDouble(Compra::getPrecioTotal)
                .sum();
        reporte.setMontoTotalCompras(montoCompras);

        // Obtener ventas en el período
        List<Venta> ventas = ventaRepository.findByFechaBetween(fechaInicio, fechaFin);
        reporte.setTotalVentas(ventas.size());

        // Calcular monto total de ventas
        Double montoVentas = ventas.stream()
                .mapToDouble(Venta::getPrecioTotal)
                .sum();
        reporte.setMontoTotalVentas(montoVentas);

        // Calcular peso total vendido
        Double pesoVendido = ventas.stream()
                .mapToDouble(Venta::getPesoTotal)
                .sum();
        reporte.setPesoTotalVentas(pesoVendido);

        // Calcular rentabilidad
        reporte.setRentabilidad(montoVentas - montoCompras);

        return reporte;
    }

    public List<TopAnimalDTO> obtenerTopAnimalesPorVentas(Integer limite, LocalDate fechaInicio, LocalDate fechaFin) {
        List<Venta> ventas = ventaRepository.findByFechaBetween(fechaInicio, fechaFin);

        // Agrupar ventas por animal y calcular montos totales
        Map<Animal, List<Venta>> ventasPorAnimal = ventas.stream()
                .collect(Collectors.groupingBy(Venta::getAnimal));

        List<TopAnimalDTO> resultado = new ArrayList<>();

        ventasPorAnimal.forEach((animal, listaVentas) -> {
            Double montoTotal = listaVentas.stream()
                    .mapToDouble(Venta::getPrecioTotal)
                    .sum();

            resultado.add(new TopAnimalDTO(
                    animal.getId(),
                    animal.getNombre(),
                    animal.getTipo(),
                    animal.getRaza(),
                    montoTotal,
                    listaVentas.size()
            ));
        });

        // Ordenar por monto total (mayor a menor) y limitar resultados
        return resultado.stream()
                .sorted(Comparator.comparing(TopAnimalDTO::getMontoTotal).reversed())
                .limit(limite)
                .collect(Collectors.toList());
    }

    public List<TopAnimalDTO> obtenerTopAnimalesPorCompras(Integer limite, LocalDate fechaInicio, LocalDate fechaFin) {
        List<Compra> compras = compraRepository.findByFechaBetween(fechaInicio, fechaFin);

        // Agrupar compras por animal y calcular montos totales
        Map<Animal, List<Compra>> comprasPorAnimal = compras.stream()
                .collect(Collectors.groupingBy(Compra::getAnimal));

        List<TopAnimalDTO> resultado = new ArrayList<>();

        comprasPorAnimal.forEach((animal, listaCompras) -> {
            Double montoTotal = listaCompras.stream()
                    .mapToDouble(Compra::getPrecioTotal)
                    .sum();

            resultado.add(new TopAnimalDTO(
                    animal.getId(),
                    animal.getNombre(),
                    animal.getTipo(),
                    animal.getRaza(),
                    montoTotal,
                    listaCompras.size()
            ));
        });

        // Ordenar por monto total (mayor a menor) y limitar resultados
        return resultado.stream()
                .sorted(Comparator.comparing(TopAnimalDTO::getMontoTotal).reversed())
                .limit(limite)
                .collect(Collectors.toList());
    }

    public Double calcularRentabilidad(LocalDate fechaInicio, LocalDate fechaFin) {
        Double totalVentas = ventaRepository.calcularTotalVentasPorPeriodo(fechaInicio, fechaFin);
        Double totalCompras = compraRepository.calcularTotalComprasPorPeriodo(fechaInicio, fechaFin);

        totalVentas = totalVentas != null ? totalVentas : 0.0;
        totalCompras = totalCompras != null ? totalCompras : 0.0;

        return totalVentas - totalCompras;
    }
}