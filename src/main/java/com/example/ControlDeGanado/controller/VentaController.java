package com.example.ControlDeGanado.controller;

import com.example.ControlDeGanado.dto.VentaDTO;
import com.example.ControlDeGanado.model.Animal;
import com.example.ControlDeGanado.model.Venta;
import com.example.ControlDeGanado.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping
    public List<Venta> listar() {
        return ventaService.listarVentas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venta> obtenerPorId(@PathVariable Long id) {
        return ventaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody VentaDTO ventaDTO) {
        Animal animal = ventaService.obtenerAnimalPorId(ventaDTO.getAnimalId())
                .orElse(null);

        if (animal == null) {
            return ResponseEntity.badRequest().body("Animal no encontrado");
        }

        Venta venta = new Venta();
        venta.setFecha(ventaDTO.getFecha() != null ? ventaDTO.getFecha() : java.time.LocalDate.now());
        venta.setPrecioTotal(ventaDTO.getPrecioTotal());
        venta.setPesoTotal(ventaDTO.getPesoTotal());
        venta.setPorcentajeDescuento(ventaDTO.getPorcentajeDescuento());
        venta.setCantidad(ventaDTO.getCantidad());
        venta.setAnimal(animal);
        venta.setDescripcion(ventaDTO.getDescripcion());

        Venta guardada = ventaService.guardarVenta(venta);
        return new ResponseEntity<>(guardada, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody VentaDTO ventaDTO) {
        return ventaService.obtenerPorId(id)
                .map(venta -> {
                    Animal animal = ventaService.obtenerAnimalPorId(ventaDTO.getAnimalId())
                            .orElse(null);

                    if (animal == null) {
                        return ResponseEntity.badRequest().body("Animal no encontrado");
                    }

                    venta.setFecha(ventaDTO.getFecha());
                    venta.setPrecioTotal(ventaDTO.getPrecioTotal());
                    venta.setPesoTotal(ventaDTO.getPesoTotal());
                    venta.setPorcentajeDescuento(ventaDTO.getPorcentajeDescuento());
                    venta.setCantidad(ventaDTO.getCantidad());
                    venta.setAnimal(animal);
                    venta.setDescripcion(ventaDTO.getDescripcion());

                    return new ResponseEntity<>(ventaService.guardarVenta(venta), HttpStatus.OK);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ventaService.eliminarVenta(id);
        return ResponseEntity.noContent().build();
    }
}