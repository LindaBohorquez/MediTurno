package com.example.MediTurno.service;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.MediTurno.dto.MedicoRequest;
import com.example.MediTurno.exception.RecursoNoEncontradoException;
import com.example.MediTurno.exception.ReglaNegocioException;
import com.example.MediTurno.model.Especialidad;
import com.example.MediTurno.model.Medico;
import com.example.MediTurno.repository.EspecialidadRepository;
import com.example.MediTurno.repository.MedicoRepository;

@Service
@Transactional
public class MedicoService {

	private final MedicoRepository medicoRepository;
	private final EspecialidadRepository especialidadRepository;

	public MedicoService(MedicoRepository medicoRepository, EspecialidadRepository especialidadRepository) {
		this.medicoRepository = medicoRepository;
		this.especialidadRepository = especialidadRepository;
	}

	@Transactional(readOnly = true)
	public List<Medico> listar(Long especialidadId) {
		if (especialidadId != null) {
			return medicoRepository.findByEspecialidadIdAndActivoTrueOrderByApellidosAscNombresAsc(especialidadId);
		}
		return medicoRepository.findByActivoTrueOrderByApellidosAscNombresAsc();
	}

	@Transactional(readOnly = true)
	public Medico obtener(Long id) {
		return medicoRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Medico no encontrado con id " + id));
	}

	public Medico crear(MedicoRequest request) {
		Medico medico = new Medico();
		aplicarDatos(medico, request);
		validarUnicos(null, medico.getDocumento(), medico.getCorreo(), medico.getRegistroMedico());
		medico.setActivo(request.activo() == null || request.activo());
		return medicoRepository.save(medico);
	}

	public Medico actualizar(Long id, MedicoRequest request) {
		Medico medico = obtener(id);
		aplicarDatos(medico, request);
		validarUnicos(id, medico.getDocumento(), medico.getCorreo(), medico.getRegistroMedico());
		if (request.activo() != null) {
			medico.setActivo(request.activo());
		}
		return medico;
	}

	public void desactivar(Long id) {
		Medico medico = obtener(id);
		medico.setActivo(false);
	}

	private void aplicarDatos(Medico medico, MedicoRequest request) {
		medico.setNombres(Validaciones.textoObligatorio(request.nombres(), "nombres"));
		medico.setApellidos(Validaciones.textoObligatorio(request.apellidos(), "apellidos"));
		medico.setDocumento(Validaciones.textoObligatorio(request.documento(), "documento"));
		String correo = Validaciones.textoObligatorio(request.correo(), "correo");
		medico.setCorreo(correo.toLowerCase(Locale.ROOT));
		medico.setTelefono(request.telefono());
		medico.setRegistroMedico(Validaciones.textoObligatorio(request.registroMedico(), "registroMedico"));
		medico.setEspecialidad(obtenerEspecialidad(request.especialidadId()));
	}

	private Especialidad obtenerEspecialidad(Long especialidadId) {
		Validaciones.idObligatorio(especialidadId, "especialidadId");
		Especialidad especialidad = especialidadRepository.findById(especialidadId)
				.orElseThrow(() -> new RecursoNoEncontradoException("Especialidad no encontrada con id " + especialidadId));
		if (!especialidad.isActiva()) {
			throw new ReglaNegocioException("La especialidad seleccionada no esta activa.");
		}
		return especialidad;
	}

	private void validarUnicos(Long idActual, String documento, String correo, String registroMedico) {
		medicoRepository.findByDocumento(documento)
				.filter(medico -> !medico.getId().equals(idActual))
				.ifPresent(medico -> {
					throw new ReglaNegocioException("Ya existe un medico con ese documento.");
				});
		medicoRepository.findByCorreoIgnoreCase(correo)
				.filter(medico -> !medico.getId().equals(idActual))
				.ifPresent(medico -> {
					throw new ReglaNegocioException("Ya existe un medico con ese correo.");
				});
		medicoRepository.findByRegistroMedicoIgnoreCase(registroMedico)
				.filter(medico -> !medico.getId().equals(idActual))
				.ifPresent(medico -> {
					throw new ReglaNegocioException("Ya existe un medico con ese registro medico.");
				});
	}
}
