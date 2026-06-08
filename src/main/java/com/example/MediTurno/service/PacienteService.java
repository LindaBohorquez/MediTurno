package com.example.MediTurno.service;

import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.MediTurno.exception.RecursoNoEncontradoException;
import com.example.MediTurno.exception.ReglaNegocioException;
import com.example.MediTurno.model.Paciente;
import com.example.MediTurno.repository.PacienteRepository;

@Service
@Transactional
public class PacienteService {

	private final PacienteRepository pacienteRepository;

	public PacienteService(PacienteRepository pacienteRepository) {
		this.pacienteRepository = pacienteRepository;
	}

	@Transactional(readOnly = true)
	public List<Paciente> listar() {
		return pacienteRepository.findAll(Sort.by("apellidos").ascending().and(Sort.by("nombres").ascending()));
	}

	@Transactional(readOnly = true)
	public Paciente obtener(Long id) {
		return pacienteRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Paciente no encontrado con id " + id));
	}

	public Paciente crear(Paciente paciente) {
		normalizar(paciente);
		validarUnicos(null, paciente.getDocumento(), paciente.getCorreo());
		paciente.setActivo(true);
		return pacienteRepository.save(paciente);
	}

	public Paciente actualizar(Long id, Paciente datos) {
		Paciente paciente = obtener(id);
		normalizar(datos);
		validarUnicos(id, datos.getDocumento(), datos.getCorreo());
		paciente.setNombres(datos.getNombres());
		paciente.setApellidos(datos.getApellidos());
		paciente.setDocumento(datos.getDocumento());
		paciente.setCorreo(datos.getCorreo());
		paciente.setTelefono(datos.getTelefono());
		paciente.setFechaNacimiento(datos.getFechaNacimiento());
		paciente.setActivo(datos.isActivo());
		return paciente;
	}

	public void desactivar(Long id) {
		Paciente paciente = obtener(id);
		paciente.setActivo(false);
	}

	private void normalizar(Paciente paciente) {
		paciente.setNombres(Validaciones.textoObligatorio(paciente.getNombres(), "nombres"));
		paciente.setApellidos(Validaciones.textoObligatorio(paciente.getApellidos(), "apellidos"));
		paciente.setDocumento(Validaciones.textoObligatorio(paciente.getDocumento(), "documento"));
		String correo = Validaciones.textoObligatorio(paciente.getCorreo(), "correo");
		paciente.setCorreo(correo.toLowerCase(Locale.ROOT));
	}

	private void validarUnicos(Long idActual, String documento, String correo) {
		pacienteRepository.findByDocumento(documento)
				.filter(paciente -> !paciente.getId().equals(idActual))
				.ifPresent(paciente -> {
					throw new ReglaNegocioException("Ya existe un paciente con ese documento.");
				});
		pacienteRepository.findByCorreoIgnoreCase(correo)
				.filter(paciente -> !paciente.getId().equals(idActual))
				.ifPresent(paciente -> {
					throw new ReglaNegocioException("Ya existe un paciente con ese correo.");
				});
	}
}
