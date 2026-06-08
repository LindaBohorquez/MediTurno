package com.example.MediTurno.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.MediTurno.dto.ServicioMedicoRequest;
import com.example.MediTurno.exception.RecursoNoEncontradoException;
import com.example.MediTurno.exception.ReglaNegocioException;
import com.example.MediTurno.model.Especialidad;
import com.example.MediTurno.model.ServicioMedico;
import com.example.MediTurno.repository.EspecialidadRepository;
import com.example.MediTurno.repository.ServicioMedicoRepository;

@Service
@Transactional
public class ServicioMedicoService {

	private final ServicioMedicoRepository servicioMedicoRepository;
	private final EspecialidadRepository especialidadRepository;

	public ServicioMedicoService(ServicioMedicoRepository servicioMedicoRepository,
			EspecialidadRepository especialidadRepository) {
		this.servicioMedicoRepository = servicioMedicoRepository;
		this.especialidadRepository = especialidadRepository;
	}

	@Transactional(readOnly = true)
	public List<ServicioMedico> listar(Long especialidadId) {
		if (especialidadId != null) {
			return servicioMedicoRepository.findByEspecialidadIdAndActivoTrueOrderByNombreAsc(especialidadId);
		}
		return servicioMedicoRepository.findByActivoTrueOrderByNombreAsc();
	}

	@Transactional(readOnly = true)
	public ServicioMedico obtener(Long id) {
		return servicioMedicoRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Servicio medico no encontrado con id " + id));
	}

	public ServicioMedico crear(ServicioMedicoRequest request) {
		ServicioMedico servicioMedico = new ServicioMedico();
		aplicarDatos(servicioMedico, request);
		servicioMedico.setActivo(request.activo() == null || request.activo());
		return servicioMedicoRepository.save(servicioMedico);
	}

	public ServicioMedico actualizar(Long id, ServicioMedicoRequest request) {
		ServicioMedico servicioMedico = obtener(id);
		aplicarDatos(servicioMedico, request);
		if (request.activo() != null) {
			servicioMedico.setActivo(request.activo());
		}
		return servicioMedico;
	}

	public void desactivar(Long id) {
		ServicioMedico servicioMedico = obtener(id);
		servicioMedico.setActivo(false);
	}

	private void aplicarDatos(ServicioMedico servicioMedico, ServicioMedicoRequest request) {
		servicioMedico.setNombre(Validaciones.textoObligatorio(request.nombre(), "nombre"));
		servicioMedico.setDescripcion(request.descripcion());
		if (request.duracionMinutos() == null || request.duracionMinutos() <= 0) {
			throw new ReglaNegocioException("La duracion del servicio debe ser mayor que cero.");
		}
		servicioMedico.setDuracionMinutos(request.duracionMinutos());
		BigDecimal tarifa = request.tarifa() == null ? BigDecimal.ZERO : request.tarifa();
		if (tarifa.signum() < 0) {
			throw new ReglaNegocioException("La tarifa no puede ser negativa.");
		}
		servicioMedico.setTarifa(tarifa);
		servicioMedico.setEspecialidad(obtenerEspecialidad(request.especialidadId()));
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
}
