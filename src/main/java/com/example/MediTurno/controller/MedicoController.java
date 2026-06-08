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

import com.example.MediTurno.dto.MedicoRequest;
import com.example.MediTurno.model.Medico;
import com.example.MediTurno.service.MedicoService;

@RestController
@RequestMapping("/api/medicos")
public class MedicoController {

	private final MedicoService medicoService;

	public MedicoController(MedicoService medicoService) {
		this.medicoService = medicoService;
	}

	@GetMapping
	public List<Medico> listar(@RequestParam(required = false) Long especialidadId) {
		return medicoService.listar(especialidadId);
	}

	@GetMapping("/{id}")
	public Medico obtener(@PathVariable Long id) {
		return medicoService.obtener(id);
	}

	@PostMapping
	public ResponseEntity<Medico> crear(@RequestBody MedicoRequest request) {
		Medico creado = medicoService.crear(request);
		return ResponseEntity.created(URI.create("/api/medicos/" + creado.getId())).body(creado);
	}

	@PutMapping("/{id}")
	public Medico actualizar(@PathVariable Long id, @RequestBody MedicoRequest request) {
		return medicoService.actualizar(id, request);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> desactivar(@PathVariable Long id) {
		medicoService.desactivar(id);
		return ResponseEntity.noContent().build();
	}
}
