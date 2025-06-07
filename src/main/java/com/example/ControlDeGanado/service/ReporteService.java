package com.example.ControlDeGanado.service;

import com.example.ControlDeGanado.model.Animal;
import com.example.ControlDeGanado.model.Compra;
import com.example.ControlDeGanado.model.Venta;
import com.example.ControlDeGanado.repository.AnimalRepository;
import com.example.ControlDeGanado.repository.CompraRepository;
import com.example.ControlDeGanado.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    private final AnimalRepository animalRepository;
    private final CompraRepository compraRepository;
    private final VentaRepository ventaRepository;

    @Autowired
    public ReporteService(AnimalRepository animalRepository,
                          CompraRepository compraRepository,
                          VentaRepository ventaRepository) {
        this.animalRepository = animalRepository;
        this.compraRepository = compraRepository;
        this.ventaRepository = ventaRepository;
    }

    public Map<String, Object> generarReporteGeneral() {
        Map<String, Object> reporte = new HashMap<>();

        // Estadísticas básicas
        List<Animal> animales = animalRepository.findAll();
        List<Compra> compras = compraRepository.findAll();
        List<Venta> ventas = ventaRepository.findAll();

        reporte.put("totalAnimales", animales.size());
        reporte.put("totalCompras", compras.size());
        reporte.put("totalVentas", ventas.size());

        // Análisis financiero
        double totalInversion = compras.stream()
                .mapToDouble(c -> c.getPrecioTotal() != null ? c.getPrecioTotal() : 0.0)
                .sum();

        double totalIngresos = ventas.stream()
                .mapToDouble(v -> v.getPrecioTotal() != null ? v.getPrecioTotal() : 0.0)
                .sum();

        double gananciaTotal = totalIngresos - totalInversion;
        double margenGanancia = totalInversion > 0 ? (gananciaTotal / totalInversion) * 100 : 0;

        reporte.put("totalInversion", Math.round(totalInversion * 100.0) / 100.0);
        reporte.put("totalIngresos", Math.round(totalIngresos * 100.0) / 100.0);
        reporte.put("gananciaTotal", Math.round(gananciaTotal * 100.0) / 100.0);
        reporte.put("margenGanancia", Math.round(margenGanancia * 100.0) / 100.0);

        // Análisis de peso
        double pesoTotalAnimales = animales.stream()
                .mapToDouble(a -> a.getPeso() != null ? a.getPeso() : 0.0)
                .sum();

        double pesoTotalVendido = ventas.stream()
                .mapToDouble(v -> v.getPesoTotal() != null ? v.getPesoTotal() : 0.0)
                .sum();

        reporte.put("pesoTotalAnimales", Math.round(pesoTotalAnimales * 100.0) / 100.0);
        reporte.put("pesoTotalVendido", Math.round(pesoTotalVendido * 100.0) / 100.0);

        // Precio promedio por kg
        double precioPromedioKg = pesoTotalVendido > 0 ? totalIngresos / pesoTotalVendido : 0;
        reporte.put("precioPromedioKg", Math.round(precioPromedioKg * 100.0) / 100.0);

        return reporte;
    }

    public Map<String, Object> generarReportePorFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        Map<String, Object> reporte = new HashMap<>();

        // Filtrar compras y ventas por fechas
        List<Compra> compras = compraRepository.findAll().stream()
                .filter(c -> c.getFecha() != null &&
                        !c.getFecha().isBefore(fechaInicio) &&
                        !c.getFecha().isAfter(fechaFin))
                .collect(Collectors.toList());

        List<Venta> ventas = ventaRepository.findAll().stream()
                .filter(v -> v.getFecha() != null &&
                        !v.getFecha().isBefore(fechaInicio) &&
                        !v.getFecha().isAfter(fechaFin))
                .collect(Collectors.toList());

        reporte.put("fechaInicio", fechaInicio);
        reporte.put("fechaFin", fechaFin);
        reporte.put("comprasEnPeriodo", compras.size());
        reporte.put("ventasEnPeriodo", ventas.size());

        // Análisis financiero del período
        double inversionPeriodo = compras.stream()
                .mapToDouble(c -> c.getPrecioTotal() != null ? c.getPrecioTotal() : 0.0)
                .sum();

        double ingresosPeriodo = ventas.stream()
                .mapToDouble(v -> v.getPrecioTotal() != null ? v.getPrecioTotal() : 0.0)
                .sum();

        double gananciaPeriodo = ingresosPeriodo - inversionPeriodo;

        reporte.put("inversionPeriodo", Math.round(inversionPeriodo * 100.0) / 100.0);
        reporte.put("ingresosPeriodo", Math.round(ingresosPeriodo * 100.0) / 100.0);
        reporte.put("gananciaPeriodo", Math.round(gananciaPeriodo * 100.0) / 100.0);

        return reporte;
    }

    public Map<String, Object> generarReportePorTipoAnimal() {
        Map<String, Object> reporte = new HashMap<>();

        List<Animal> animales = animalRepository.findAll();

        // Agrupar por tipo
        Map<String, List<Animal>> animalesPorTipo = animales.stream()
                .filter(a -> a.getTipo() != null)
                .collect(Collectors.groupingBy(Animal::getTipo));

        Map<String, Map<String, Object>> reportePorTipo = new HashMap<>();

        for (Map.Entry<String, List<Animal>> entry : animalesPorTipo.entrySet()) {
            String tipo = entry.getKey();
            List<Animal> animalesTipo = entry.getValue();

            Map<String, Object> datoseTipo = new HashMap<>();
            datoseTipo.put("cantidad", animalesTipo.size());

            double pesoTotal = animalesTipo.stream()
                    .mapToDouble(a -> a.getPeso() != null ? a.getPeso() : 0.0)
                    .sum();
            double pesoPromedio = pesoTotal / animalesTipo.size();

            datoseTipo.put("pesoTotal", Math.round(pesoTotal * 100.0) / 100.0);
            datoseTipo.put("pesoPromedio", Math.round(pesoPromedio * 100.0) / 100.0);

            // Calcular ventas de este tipo
            List<Long> idsAnimalesTipo = animalesTipo.stream()
                    .map(Animal::getId)
                    .collect(Collectors.toList());

            List<Venta> ventasTipo = ventaRepository.findAll().stream()
                    .filter(v -> v.getAnimal() != null &&
                            idsAnimalesTipo.contains(v.getAnimal().getId()))
                    .collect(Collectors.toList());

            double ingresoseTipo = ventasTipo.stream()
                    .mapToDouble(v -> v.getPrecioTotal() != null ? v.getPrecioTotal() : 0.0)
                    .sum();

            datoseTipo.put("ventasRealizadas", ventasTipo.size());
            datoseTipo.put("ingresosTipo", Math.round(ingresoseTipo * 100.0) / 100.0);

            reportePorTipo.put(tipo, datoseTipo);
        }

        reporte.put("reportePorTipo", reportePorTipo);
        return reporte;
    }

    public Map<String, Object> generarAnalisisTendencias() {
        Map<String, Object> reporte = new HashMap<>();

        List<Compra> compras = compraRepository.findAll();
        List<Venta> ventas = ventaRepository.findAll();

        // Análisis mensual de los últimos 12 meses
        LocalDate fechaActual = LocalDate.now();
        LocalDate fechaInicioAnalisis = fechaActual.minusMonths(12);

        Map<String, Double> comprasMensuales = new TreeMap<>();
        Map<String, Double> ventasMensuales = new TreeMap<>();

        for (int i = 0; i < 12; i++) {
            LocalDate mesInicio = fechaInicioAnalisis.plusMonths(i);
            LocalDate mesFin = mesInicio.plusMonths(1).minusDays(1);
            String claveMes = mesInicio.getYear() + "-" + String.format("%02d", mesInicio.getMonthValue());

            double totalComprasMes = compras.stream()
                    .filter(c -> c.getFecha() != null &&
                            !c.getFecha().isBefore(mesInicio) &&
                            !c.getFecha().isAfter(mesFin))
                    .mapToDouble(c -> c.getPrecioTotal() != null ? c.getPrecioTotal() : 0.0)
                    .sum();

            double totalVentasMes = ventas.stream()
                    .filter(v -> v.getFecha() != null &&
                            !v.getFecha().isBefore(mesInicio) &&
                            !v.getFecha().isAfter(mesFin))
                    .mapToDouble(v -> v.getPrecioTotal() != null ? v.getPrecioTotal() : 0.0)
                    .sum();

            comprasMensuales.put(claveMes, Math.round(totalComprasMes * 100.0) / 100.0);
            ventasMensuales.put(claveMes, Math.round(totalVentasMes * 100.0) / 100.0);
        }

        reporte.put("comprasMensuales", comprasMensuales);
        reporte.put("ventasMensuales", ventasMensuales);

        // Proyección simple (promedio de últimos 3 meses)
        double promedioComprasUltimos3Meses = comprasMensuales.values().stream()
                .skip(Math.max(0, comprasMensuales.size() - 3))
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        double promedioVentasUltimos3Meses = ventasMensuales.values().stream()
                .skip(Math.max(0, ventasMensuales.size() - 3))
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        reporte.put("proyeccionComprasProximoMes", Math.round(promedioComprasUltimos3Meses * 100.0) / 100.0);
        reporte.put("proyeccionVentasProximoMes", Math.round(promedioVentasUltimos3Meses * 100.0) / 100.0);

        return reporte;
    }

    public List<Map<String, Object>> obtenerTop10AnimalesPorPeso() {
        return animalRepository.findAll().stream()
                .filter(a -> a.getPeso() != null)
                .sorted((a1, a2) -> Double.compare(a2.getPeso(), a1.getPeso()))
                .limit(10)
                .map(animal -> {
                    Map<String, Object> animalData = new HashMap<>();
                    animalData.put("id", animal.getId());
                    animalData.put("nombre", animal.getNombre());
                    animalData.put("tipo", animal.getTipo());
                    animalData.put("peso", animal.getPeso());
                    return animalData;
                })
                .collect(Collectors.toList());
    }

    public Map<String, Object> calcularPromediosPorMes() {
        List<Compra> compras = compraRepository.findAll();
        List<Venta> ventas = ventaRepository.findAll();

        // Calcular promedios por mes
        Map<Integer, Double> promedioComprasPorMes = compras.stream()
                .filter(c -> c.getFecha() != null)
                .collect(Collectors.groupingBy(
                        c -> c.getFecha().getMonthValue(),
                        Collectors.averagingDouble(c -> c.getPrecioTotal() != null ? c.getPrecioTotal() : 0.0)
                ));

        Map<Integer, Double> promedioVentasPorMes = ventas.stream()
                .filter(v -> v.getFecha() != null)
                .collect(Collectors.groupingBy(
                        v -> v.getFecha().getMonthValue(),
                        Collectors.averagingDouble(v -> v.getPrecioTotal() != null ? v.getPrecioTotal() : 0.0)
                ));

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("promedioComprasPorMes", promedioComprasPorMes);
        resultado.put("promedioVentasPorMes", promedioVentasPorMes);

        return resultado;
    }
}