package com.example.ControlDeGanado.service;

import com.example.ControlDeGanado.model.Animal;
import com.example.ControlDeGanado.model.Compra;
import com.example.ControlDeGanado.repository.AnimalRepository;
import com.example.ControlDeGanado.repository.CompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompraService {

    private final CompraRepository compraRepository;
    private final AnimalRepository animalRepository;

    @Autowired
    public CompraService(CompraRepository compraRepository, AnimalRepository animalRepository) {
        this.compraRepository = compraRepository;
        this.animalRepository = animalRepository;
    }

    public List<Compra> listarCompras() {
        return compraRepository.findAll();
    }

    public Optional<Compra> obtenerPorId(Long id) {
        return compraRepository.findById(id);
    }

    public Compra guardarCompra(Compra compra) {
        validateCompra(compra);
        return compraRepository.save(compra);
    }

    public void eliminarCompra(Long id) {
        if (!compraRepository.existsById(id)) {
            throw new RuntimeException("Compra no encontrada con ID: " + id);
        }
        compraRepository.deleteById(id);
    }

    public Optional<Animal> obtenerAnimalPorId(Long id) {
        return animalRepository.findById(id);
    }

    public List<Compra> obtenerComprasPorAnimal(Long animalId) {
        return compraRepository.findAll().stream()
                .filter(compra -> compra.getAnimal() != null &&
                        compra.getAnimal().getId().equals(animalId))
                .collect(Collectors.toList());
    }

    public List<Compra> obtenerComprasPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        return compraRepository.findAll().stream()
                .filter(compra -> compra.getFecha() != null &&
                        !compra.getFecha().isBefore(fechaInicio) &&
                        !compra.getFecha().isAfter(fechaFin))
                .collect(Collectors.toList());
    }

    public Map<String, Object> obtenerEstadisticasCompras() {
        List<Compra> compras = compraRepository.findAll();
        Map<String, Object> estadisticas = new HashMap<>();

        estadisticas.put("totalCompras", compras.size());

        if (!compras.isEmpty()) {
            // Inversión total
            double inversionTotal = compras.stream()
                    .mapToDouble(c -> c.getPrecioTotal() != null ? c.getPrecioTotal() : 0.0)
                    .sum();
            estadisticas.put("inversionTotal", Math.round(inversionTotal * 100.0) / 100.0);

            // Compra promedio
            double compraPromedio = inversionTotal / compras.size();
            estadisticas.put("compraPromedio", Math.round(compraPromedio * 100.0) / 100.0);

            // Compra más alta y más baja
            OptionalDouble compraMaxima = compras.stream()
                    .mapToDouble(c -> c.getPrecioTotal() != null ? c.getPrecioTotal() : 0.0)
                    .max();
            OptionalDouble compraMinima = compras.stream()
                    .mapToDouble(c -> c.getPrecioTotal() != null ? c.getPrecioTotal() : Double.MAX_VALUE)
                    .min();

            if (compraMaxima.isPresent()) {
                estadisticas.put("compraMaxima", Math.round(compraMaxima.getAsDouble() * 100.0) / 100.0);
            }
            if (compraMinima.isPresent() && compraMinima.getAsDouble() != Double.MAX_VALUE) {
                estadisticas.put("compraMinima", Math.round(compraMinima.getAsDouble() * 100.0) / 100.0);
            }

            // Distribución por tipo de animal
            Map<String, Long> comprasPorTipo = compras.stream()
                    .filter(c -> c.getAnimal() != null && c.getAnimal().getTipo() != null)
                    .collect(Collectors.groupingBy(
                            c -> c.getAnimal().getTipo(),
                            Collectors.counting()
                    ));
            estadisticas.put("comprasPorTipo", comprasPorTipo);

            // Inversión por tipo de animal
            Map<String, Double> inversionPorTipo = compras.stream()
                    .filter(c -> c.getAnimal() != null && c.getAnimal().getTipo() != null)
                    .collect(Collectors.groupingBy(
                            c -> c.getAnimal().getTipo(),
                            Collectors.summingDouble(c -> c.getPrecioTotal() != null ? c.getPrecioTotal() : 0.0)
                    ));

            // Redondear valores de inversión por tipo
            inversionPorTipo.replaceAll((k, v) -> Math.round(v * 100.0) / 100.0);
            estadisticas.put("inversionPorTipo", inversionPorTipo);

            // Compras por mes (últimos 12 meses)
            LocalDate fechaActual = LocalDate.now();
            Map<String, Integer> comprasPorMes = new TreeMap<>();

            for (int i = 0; i < 12; i++) {
                LocalDate mesInicio = fechaActual.minusMonths(11 - i).withDayOfMonth(1);
                LocalDate mesFin = mesInicio.plusMonths(1).minusDays(1);
                String claveMes = mesInicio.getYear() + "-" + String.format("%02d", mesInicio.getMonthValue());

                long comprasEnMes = compras.stream()
                        .filter(c -> c.getFecha() != null &&
                                !c.getFecha().isBefore(mesInicio) &&
                                !c.getFecha().isAfter(mesFin))
                        .count();

                comprasPorMes.put(claveMes, (int) comprasEnMes);
            }
            estadisticas.put("comprasPorMes", comprasPorMes);

        } else {
            estadisticas.put("inversionTotal", 0.0);
            estadisticas.put("compraPromedio", 0.0);
            estadisticas.put("comprasPorTipo", new HashMap<>());
            estadisticas.put("inversionPorTipo", new HashMap<>());
            estadisticas.put("comprasPorMes", new HashMap<>());
        }

        return estadisticas;
    }

    public double calcularTotalInversion() {
        return compraRepository.findAll().stream()
                .mapToDouble(c -> c.getPrecioTotal() != null ? c.getPrecioTotal() : 0.0)
                .sum();
    }

    public List<Compra> obtenerComprasRecientes(int limite) {
        return compraRepository.findAll().stream()
                .filter(c -> c.getFecha() != null)
                .sorted((c1, c2) -> c2.getFecha().compareTo(c1.getFecha()))
                .limit(limite)
                .collect(Collectors.toList());
    }

    public Map<String, Double> calcularInversionUltimosMeses(int meses) {
        LocalDate fechaActual = LocalDate.now();
        Map<String, Double> inversionPorMes = new TreeMap<>();

        for (int i = 0; i < meses; i++) {
            LocalDate mesInicio = fechaActual.minusMonths(meses - 1 - i).withDayOfMonth(1);
            LocalDate mesFin = mesInicio.plusMonths(1).minusDays(1);
            String claveMes = mesInicio.getYear() + "-" + String.format("%02d", mesInicio.getMonthValue());

            double inversionMes = compraRepository.findAll().stream()
                    .filter(c -> c.getFecha() != null &&
                            !c.getFecha().isBefore(mesInicio) &&
                            !c.getFecha().isAfter(mesFin) &&
                            c.getPrecioTotal() != null)
                    .mapToDouble(Compra::getPrecioTotal)
                    .sum();

            inversionPorMes.put(claveMes, Math.round(inversionMes * 100.0) / 100.0);
        }

        return inversionPorMes;
    }

    public double calcularPromedioInversionPorAnimal() {
        List<Compra> compras = compraRepository.findAll();
        if (compras.isEmpty()) {
            return 0.0;
        }

        Set<Long> animalesUnicos = compras.stream()
                .filter(c -> c.getAnimal() != null)
                .map(c -> c.getAnimal().getId())
                .collect(Collectors.toSet());

        if (animalesUnicos.isEmpty()) {
            return 0.0;
        }

        double inversionTotal = compras.stream()
                .mapToDouble(c -> c.getPrecioTotal() != null ? c.getPrecioTotal() : 0.0)
                .sum();

        return Math.round((inversionTotal / animalesUnicos.size()) * 100.0) / 100.0;
    }

    public List<Map<String, Object>> obtenerComprasMasCaras(int limite) {
        return compraRepository.findAll().stream()
                .filter(c -> c.getPrecioTotal() != null)
                .sorted((c1, c2) -> Double.compare(c2.getPrecioTotal(), c1.getPrecioTotal()))
                .limit(limite)
                .map(compra -> {
                    Map<String, Object> compraData = new HashMap<>();
                    compraData.put("id", compra.getId());
                    compraData.put("fecha", compra.getFecha());
                    compraData.put("precio", compra.getPrecioTotal());
                    compraData.put("animal", compra.getAnimal() != null ?
                            compra.getAnimal().getNombre() : "N/A");
                    compraData.put("descripcion", compra.getDescripcion());
                    return compraData;
                })
                .collect(Collectors.toList());
    }

    public Map<String, Object> analizarTendenciasCompras() {
        List<Compra> compras = compraRepository.findAll().stream()
                .filter(c -> c.getFecha() != null && c.getPrecioTotal() != null)
                .sorted((c1, c2) -> c1.getFecha().compareTo(c2.getFecha()))
                .collect(Collectors.toList());

        Map<String, Object> analisis = new HashMap<>();

        if (compras.size() < 2) {
            analisis.put("tendencia", "insuficientes_datos");
            return analisis;
        }

        // Dividir en dos mitades para comparar
        int mitad = compras.size() / 2;
        List<Compra> primeraeMitad = compras.subList(0, mitad);
        List<Compra> segundaMitad = compras.subList(mitad, compras.size());

        double promedioPrimeraeMitad = primeraeMitad.stream()
                .mapToDouble(Compra::getPrecioTotal)
                .average()
                .orElse(0.0);

        double promedioSegundaMitad = segundaMitad.stream()
                .mapToDouble(Compra::getPrecioTotal)
                .average()
                .orElse(0.0);

        double diferencia = promedioSegundaMitad - promedioPrimeraeMitad;
        double porcentajeCambio = promedioPrimeraeMitad > 0 ?
                (diferencia / promedioPrimeraeMitad) * 100 : 0;

        analisis.put("promedioPrimeraeMitad", Math.round(promedioPrimeraeMitad * 100.0) / 100.0);
        analisis.put("promedioSegundaMitad", Math.round(promedioSegundaMitad * 100.0) / 100.0);
        analisis.put("diferencia", Math.round(diferencia * 100.0) / 100.0);
        analisis.put("porcentajeCambio", Math.round(porcentajeCambio * 100.0) / 100.0);

        if (porcentajeCambio > 10) {
            analisis.put("tendencia", "incremento_significativo");
        } else if (porcentajeCambio < -10) {
            analisis.put("tendencia", "decremento_significativo");
        } else {
            analisis.put("tendencia", "estable");
        }

        return analisis;
    }

    private void validateCompra(Compra compra) {
        if (compra.getAnimal() == null) {
            throw new IllegalArgumentException("El animal es obligatorio para la compra");
        }

        if (compra.getFecha() == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }

        if (compra.getFecha().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de compra no puede ser futura");
        }

        if (compra.getPrecioTotal() == null || compra.getPrecioTotal() <= 0) {
            throw new IllegalArgumentException("El precio total debe ser mayor que cero");
        }

        // Validar precio razonable (entre $1 y $1,000,000)
        if (compra.getPrecioTotal() < 1 || compra.getPrecioTotal() > 1000000) {
            throw new IllegalArgumentException("El precio debe estar entre $1 y $1,000,000");
        }

        // Validar que no haya más de 10 compras del mismo animal en el mismo día
        long comprasDelMismoDia = compraRepository.findAll().stream()
                .filter(c -> c.getAnimal() != null &&
                        c.getAnimal().getId().equals(compra.getAnimal().getId()) &&
                        c.getFecha() != null &&
                        c.getFecha().equals(compra.getFecha()) &&
                        !c.getId().equals(compra.getId())) // Excluir la compra actual en caso de actualización
                .count();

        if (comprasDelMismoDia >= 10) {
            throw new IllegalArgumentException("No se pueden registrar más de 10 compras del mismo animal en un día");
        }

        // Validar longitud de descripción
        if (compra.getDescripcion() != null && compra.getDescripcion().length() > 1000) {
            throw new IllegalArgumentException("La descripción no puede exceder 1000 caracteres");
        }
    }
}