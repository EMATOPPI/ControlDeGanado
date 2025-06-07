package com.example.ControlDeGanado.service;

import com.example.ControlDeGanado.model.Animal;
import com.example.ControlDeGanado.model.Venta;
import com.example.ControlDeGanado.repository.AnimalRepository;
import com.example.ControlDeGanado.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class VentaService {

    private final VentaRepository ventaRepository;
    private final AnimalRepository animalRepository;

    @Autowired
    public VentaService(VentaRepository ventaRepository, AnimalRepository animalRepository) {
        this.ventaRepository = ventaRepository;
        this.animalRepository = animalRepository;
    }

    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    public Optional<Venta> obtenerPorId(Long id) {
        return ventaRepository.findById(id);
    }

    public Venta guardarVenta(Venta venta) {
        validateVenta(venta);
        return ventaRepository.save(venta);
    }

    public void eliminarVenta(Long id) {
        if (!ventaRepository.existsById(id)) {
            throw new RuntimeException("Venta no encontrada con ID: " + id);
        }
        ventaRepository.deleteById(id);
    }

    public Optional<Animal> obtenerAnimalPorId(Long id) {
        return animalRepository.findById(id);
    }

    public List<Venta> obtenerVentasPorAnimal(Long animalId) {
        return ventaRepository.findAll().stream()
                .filter(venta -> venta.getAnimal() != null &&
                        venta.getAnimal().getId().equals(animalId))
                .collect(Collectors.toList());
    }

    public List<Venta> obtenerVentasPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return ventaRepository.findAll().stream()
                .filter(venta -> venta.getFecha() != null &&
                        !venta.getFecha().isBefore(fechaInicio) &&
                        !venta.getFecha().isAfter(fechaFin))
                .collect(Collectors.toList());
    }

    public Map<String, Object> obtenerEstadisticasVentas() {
        List<Venta> ventas = ventaRepository.findAll();
        Map<String, Object> estadisticas = new HashMap<>();

        estadisticas.put("totalVentas", ventas.size());

        if (!ventas.isEmpty()) {
            // Ingresos totales
            double ingresoTotal = ventas.stream()
                    .mapToDouble(v -> v.getPrecioTotal() != null ? v.getPrecioTotal() : 0.0)
                    .sum();
            estadisticas.put("ingresoTotal", Math.round(ingresoTotal * 100.0) / 100.0);

            // Peso total vendido
            double pesoTotalVendido = ventas.stream()
                    .mapToDouble(v -> v.getPesoTotal() != null ? v.getPesoTotal() : 0.0)
                    .sum();
            estadisticas.put("pesoTotalVendido", Math.round(pesoTotalVendido * 100.0) / 100.0);

            // Precio promedio por kg
            double precioPorKg = pesoTotalVendido > 0 ? ingresoTotal / pesoTotalVendido : 0;
            estadisticas.put("precioPromedioKg", Math.round(precioPorKg * 100.0) / 100.0);

            // Venta promedio
            double ventaPromedio = ingresoTotal / ventas.size();
            estadisticas.put("ventaPromedio", Math.round(ventaPromedio * 100.0) / 100.0);

            // Descuento promedio
            double descuentoPromedio = ventas.stream()
                    .mapToDouble(v -> v.getPorcentajeDescuento() != null ? v.getPorcentajeDescuento() : 0.0)
                    .average()
                    .orElse(0.0);
            estadisticas.put("descuentoPromedio", Math.round(descuentoPromedio * 100.0) / 100.0);

            // Ventas por mes (últimos 12 meses)
            LocalDate fechaActual = LocalDate.now();
            Map<String, Integer> ventasPorMes = new TreeMap<>();

            for (int i = 0; i < 12; i++) {
                LocalDate mesInicio = fechaActual.minusMonths(11 - i).withDayOfMonth(1);
                LocalDate mesFin = mesInicio.plusMonths(1).minusDays(1);
                String claveMes = mesInicio.getYear() + "-" + String.format("%02d", mesInicio.getMonthValue());

                long ventasEnMes = ventas.stream()
                        .filter(v -> v.getFecha() != null &&
                                !v.getFecha().isBefore(mesInicio) &&
                                !v.getFecha().isAfter(mesFin))
                        .count();

                ventasPorMes.put(claveMes, (int) ventasEnMes);
            }
            estadisticas.put("ventasPorMes", ventasPorMes);

        } else {
            estadisticas.put("ingresoTotal", 0.0);
            estadisticas.put("pesoTotalVendido", 0.0);
            estadisticas.put("precioPromedioKg", 0.0);
            estadisticas.put("ventaPromedio", 0.0);
            estadisticas.put("descuentoPromedio", 0.0);
            estadisticas.put("ventasPorMes", new HashMap<>());
        }

        return estadisticas;
    }

    public double calcularPrecioSugerido(Long animalId) {
        Optional<Animal> animalOpt = animalRepository.findById(animalId);
        if (animalOpt.isEmpty()) {
            throw new RuntimeException("Animal no encontrado");
        }

        Animal animal = animalOpt.get();

        // Obtener ventas de animales del mismo tipo
        List<Venta> ventasSimilares = ventaRepository.findAll().stream()
                .filter(v -> v.getAnimal() != null &&
                        v.getAnimal().getTipo() != null &&
                        v.getAnimal().getTipo().equals(animal.getTipo()) &&
                        v.getPrecioTotal() != null &&
                        v.getPesoTotal() != null &&
                        v.getPesoTotal() > 0)
                .collect(Collectors.toList());

        if (ventasSimilares.isEmpty()) {
            // Si no hay ventas similares, usar un precio base estimado
            return animal.getPeso() != null ? animal.getPeso() * 50.0 : 1000.0; // $50 por kg como estimación
        }

        // Calcular precio promedio por kg de animales similares
        double precioPromedioKg = ventasSimilares.stream()
                .mapToDouble(v -> v.getPrecioTotal() / v.getPesoTotal())
                .average()
                .orElse(50.0);

        // Aplicar peso del animal actual
        double precioSugerido = animal.getPeso() != null ?
                animal.getPeso() * precioPromedioKg : precioPromedioKg * 100;

        return Math.round(precioSugerido * 100.0) / 100.0;
    }

    public List<Venta> obtenerVentasRecientes(int limite) {
        return ventaRepository.findAll().stream()
                .filter(v -> v.getFecha() != null)
                .sorted((v1, v2) -> v2.getFecha().compareTo(v1.getFecha()))
                .limit(limite)
                .collect(Collectors.toList());
    }

    public Map<String, Double> calcularIngresosUltimosMeses(int meses) {
        LocalDate fechaActual = LocalDate.now();
        Map<String, Double> ingresosPorMes = new TreeMap<>();

        for (int i = 0; i < meses; i++) {
            LocalDate mesInicio = fechaActual.minusMonths(meses - 1 - i).withDayOfMonth(1);
            LocalDate mesFin = mesInicio.plusMonths(1).minusDays(1);
            String claveMes = mesInicio.getYear() + "-" + String.format("%02d", mesInicio.getMonthValue());

            double ingresosMes = ventaRepository.findAll().stream()
                    .filter(v -> v.getFecha() != null &&
                            !v.getFecha().isBefore(mesInicio) &&
                            !v.getFecha().isAfter(mesFin) &&
                            v.getPrecioTotal() != null)
                    .mapToDouble(Venta::getPrecioTotal)
                    .sum();

            ingresosPorMes.put(claveMes, Math.round(ingresosMes * 100.0) / 100.0);
        }

        return ingresosPorMes;
    }

    private void validateVenta(Venta venta) {
        if (venta.getAnimal() == null) {
            throw new IllegalArgumentException("El animal es obligatorio para la venta");
        }

        if (venta.getFecha() == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }

        if (venta.getFecha().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de venta no puede ser futura");
        }

        if (venta.getPrecioTotal() == null || venta.getPrecioTotal() <= 0) {
            throw new IllegalArgumentException("El precio total debe ser mayor que cero");
        }

        if (venta.getPesoTotal() == null || venta.getPesoTotal() <= 0) {
            throw new IllegalArgumentException("El peso total debe ser mayor que cero");
        }

        if (venta.getPorcentajeDescuento() == null) {
            venta.setPorcentajeDescuento(0.0);
        }

        if (venta.getPorcentajeDescuento() < 0 || venta.getPorcentajeDescuento() > 100) {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar entre 0 y 100");
        }

        // Validar que el peso de venta no exceda significativamente el peso del animal
        if (venta.getAnimal().getPeso() != null &&
                venta.getPesoTotal() > venta.getAnimal().getPeso() * 1.2) {
            throw new IllegalArgumentException("El peso de venta no puede exceder significativamente el peso del animal");
        }

        // Validar precio razonable (entre $1 y $1,000,000)
        if (venta.getPrecioTotal() < 1 || venta.getPrecioTotal() > 1000000) {
            throw new IllegalArgumentException("El precio debe estar entre $1 y $1,000,000");
        }
    }
}