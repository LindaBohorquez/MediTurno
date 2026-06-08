package com.example.MediTurno.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.MediTurno.dto.ServicioMedicoRequest;
import com.example.MediTurno.model.ServicioMedico;
import com.example.MediTurno.service.ServicioMedicoService;

@RestController
@RequestMapping("/api/servicios-medicos")
public class ServicioMedicoController {

	private final ServicioMedicoService servicioMedicoService;

	public ServicioMedicoController(ServicioMedicoService servicioMedicoService) {
		this.servicioMedicoService = servicioMedicoService;
	}

	@GetMapping
	public List<ServicioMedico> listar(@RequestParam(required = false) Long especialidadId) {
		return servicioMedicoService.listar(especialidadId);
	}

	@GetMapping("/{id}")
	public ServicioMedico obtener(@PathVariable Long id) {
		return servicioMedicoService.obtener(id);
	}

	@PostMapping
	public ResponseEntity<ServicioMedico> crear(@RequestBody ServicioMedicoRequest request) {
		ServicioMedico creado = servicioMedicoService.crear(request);
		return ResponseEntity.created(URI.create("/api/servicios-medicos/" + creado.getId())).body(creado);
	}

	@PutMapping("/{id}")
	public ServicioMedico actualizar(@PathVariable Long id, @RequestBody ServicioMedicoRequest request) {
		return servicioMedicoService.actualizar(id, request);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> desactivar(@PathVariable Long id) {
		servicioMedicoService.desactivar(id);
		return ResponseEntity.noContent().build();
	}
}
