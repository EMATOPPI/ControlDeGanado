package com.example.ControlDeGanado.controller;

import com.example.ControlDeGanado.dto.CompraDTO;
import com.example.ControlDeGanado.model.Animal;
import com.example.ControlDeGanado.model.Compra;
import com.example.ControlDeGanado.service.CompraService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/compras")
@CrossOrigin(origins = "*")
public class CompraController {

    private final CompraService compraService;

    @Autowired
    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @GetMapping
    public ResponseEntity<List<Compra>> listar() {
        try {
            List<Compra> compras = compraService.listarCompras();
            return ResponseEntity.ok(compras);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Compra> obtenerPorId(@PathVariable Long id) {
        try {
            return compraService.obtenerPorId(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody CompraDTO compraDTO, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            Animal animal = compraService.obtenerAnimalPorId(compraDTO.getAnimalId())
                    .orElse(null);

            if (animal == null) {
                return ResponseEntity.badRequest().body("Animal no encontrado con ID: " + compraDTO.getAnimalId());
            }

            // Validaciones adicionales
            if (compraDTO.getFecha().isAfter(LocalDate.now())) {
                return ResponseEntity.badRequest().body("La fecha de compra no puede ser futura");
            }

            Compra compra = convertirDTOaEntidad(compraDTO, animal);
            Compra compraGuardada = compraService.guardarCompra(compra);
            return new ResponseEntity<>(compraGuardada, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear la compra: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody CompraDTO compraDTO,
                                        BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            return compraService.obtenerPorId(id)
                    .map(compraExistente -> {
                        Animal animal = compraService.obtenerAnimalPorId(compraDTO.getAnimalId())
                                .orElse(null);

                        if (animal == null) {
                            return ResponseEntity.badRequest().body("Animal no encontrado");
                        }

                        compraExistente.setFecha(compraDTO.getFecha());
                        compraExistente.setPrecioTotal(compraDTO.getPrecioTotal());
                        compraExistente.setAnimal(animal);
                        compraExistente.setDescripcion(compraDTO.getDescripcion());

                        Compra compraActualizada = compraService.guardarCompra(compraExistente);
                        return ResponseEntity.ok(compraActualizada);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar la compra: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            if (compraService.obtenerPorId(id).isPresent()) {
                compraService.eliminarCompra(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar la compra: " + e.getMessage());
        }
    }

    @GetMapping("/por-animal/{animalId}")
    public ResponseEntity<?> obtenerComprasPorAnimal(@PathVariable Long animalId) {
        try {
            List<Compra> compras = compraService.obtenerComprasPorAnimal(animalId);
            return ResponseEntity.ok(compras);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener compras del animal: " + e.getMessage());
        }
    }

    @GetMapping("/por-fechas")
    public ResponseEntity<?> obtenerComprasPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        try {
            if (fechaInicio.isAfter(fechaFin)) {
                return ResponseEntity.badRequest()
                        .body("La fecha de inicio no puede ser posterior a la fecha de fin");
            }

            List<Compra> compras = compraService.obtenerComprasPorRangoFechas(fechaInicio, fechaFin);
            return ResponseEntity.ok(compras);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener compras por fechas: " + e.getMessage());
        }
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticasCompras() {
        try {
            var estadisticas = compraService.obtenerEstadisticasCompras();
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener estadísticas de compras: " + e.getMessage());
        }
    }

    @GetMapping("/total-inversion")
    public ResponseEntity<?> obtenerTotalInversion() {
        try {
            double totalInversion = compraService.calcularTotalInversion();
            return ResponseEntity.ok(Map.of("totalInversion", totalInversion));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al calcular total de inversión: " + e.getMessage());
        }
    }

    @GetMapping("/recientes/{limite}")
    public ResponseEntity<?> obtenerComprasRecientes(@PathVariable int limite) {
        try {
            if (limite <= 0 || limite > 100) {
                return ResponseEntity.badRequest().body("El límite debe estar entre 1 y 100");
            }

            List<Compra> comprasRecientes = compraService.obtenerComprasRecientes(limite);
            return ResponseEntity.ok(comprasRecientes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener compras recientes: " + e.getMessage());
        }
    }

    // Método auxiliar para convertir DTO a entidad
    private Compra convertirDTOaEntidad(CompraDTO dto, Animal animal) {
        Compra compra = new Compra();
        compra.setFecha(dto.getFecha());
        compra.setPrecioTotal(dto.getPrecioTotal());
        compra.setAnimal(animal);
        compra.setDescripcion(dto.getDescripcion());
        return compra;
    }
}