package com.example.MediTurno.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.MediTurno.dto.DisponibilidadMedicaRequest;
import com.example.MediTurno.model.DisponibilidadMedica;
import com.example.MediTurno.service.DisponibilidadMedicaService;

@RestController
@RequestMapping("/api/disponibilidades")
public class DisponibilidadMedicaController {

	private final DisponibilidadMedicaService disponibilidadMedicaService;

	public DisponibilidadMedicaController(DisponibilidadMedicaService disponibilidadMedicaService) {
		this.disponibilidadMedicaService = disponibilidadMedicaService;
	}

	@GetMapping
	public List<DisponibilidadMedica> listarDisponibles(
			@RequestParam(required = false) Long medicoId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
		return disponibilidadMedicaService.listarDisponibles(medicoId, fecha);
	}

	@GetMapping("/{id}")
	public DisponibilidadMedica obtener(@PathVariable Long id) {
		return disponibilidadMedicaService.obtener(id);
	}

	@PostMapping
	public ResponseEntity<DisponibilidadMedica> registrar(@RequestBody DisponibilidadMedicaRequest request) {
		DisponibilidadMedica creada = disponibilidadMedicaService.registrar(request);
		return ResponseEntity.created(URI.create("/api/disponibilidades/" + creada.getId())).body(creada);
	}

	@PatchMapping("/{id}/reservar")
	public DisponibilidadMedica reservar(@PathVariable Long id) {
		return disponibilidadMedicaService.reservar(id);
	}

	@PatchMapping("/{id}/liberar")
	public DisponibilidadMedica liberar(@PathVariable Long id) {
		return disponibilidadMedicaService.liberar(id);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> cancelar(@PathVariable Long id) {
		disponibilidadMedicaService.cancelar(id);
		return ResponseEntity.noContent().build();
	}
}
