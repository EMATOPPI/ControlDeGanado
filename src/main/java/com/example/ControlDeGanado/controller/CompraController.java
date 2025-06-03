package com.example.ControlDeGanado.controller;


import com.example.ControlDeGanado.dto.CompraDTO;
import com.example.ControlDeGanado.model.Animal;
import com.example.ControlDeGanado.model.Compra;
import com.example.ControlDeGanado.service.CompraService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compras")
public class CompraController {

    private final CompraService compraService;

    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @GetMapping
    public List<Compra> listar() {
        return compraService.listarCompras();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Compra> obtenerPorId(@PathVariable Long id) {
        return compraService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody CompraDTO compraDTO) {
        Animal animal = compraService.obtenerAnimalPorId(compraDTO.getAnimalId())
                .orElse(null);

        if (animal == null) {
            return ResponseEntity.badRequest().body("Animal no encontrado");
        }

        Compra compra = new Compra();
        compra.setFecha(compraDTO.getFecha());
        compra.setPrecioTotal(compraDTO.getPrecioTotal());
        compra.setAnimal(animal);
        compra.setDescripcion(compraDTO.getDescripcion());

        Compra guardada = compraService.guardarCompra(compra);
        return new ResponseEntity<>(guardada, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        compraService.eliminarCompra(id);
        return ResponseEntity.noContent().build();
    }
}