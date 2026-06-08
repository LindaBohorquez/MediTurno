package com.example.MediTurno.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.MediTurno.dto.DisponibilidadMedicaRequest;
import com.example.MediTurno.exception.RecursoNoEncontradoException;
import com.example.MediTurno.exception.ReglaNegocioException;
import com.example.MediTurno.model.DisponibilidadMedica;
import com.example.MediTurno.model.EstadoDisponibilidad;
import com.example.MediTurno.model.Medico;
import com.example.MediTurno.repository.DisponibilidadMedicaRepository;
import com.example.MediTurno.repository.MedicoRepository;

@Service
@Transactional
public class DisponibilidadMedicaService {

	private final DisponibilidadMedicaRepository disponibilidadMedicaRepository;
	private final MedicoRepository medicoRepository;

	public DisponibilidadMedicaService(DisponibilidadMedicaRepository disponibilidadMedicaRepository,
			MedicoRepository medicoRepository) {
		this.disponibilidadMedicaRepository = disponibilidadMedicaRepository;
		this.medicoRepository = medicoRepository;
	}

	@Transactional(readOnly = true)
	public List<DisponibilidadMedica> listarDisponibles(Long medicoId, LocalDate fecha) {
		if (medicoId != null && fecha != null) {
			return disponibilidadMedicaRepository.findByMedicoIdAndFechaAndEstadoOrderByHoraInicioAsc(medicoId, fecha,
					EstadoDisponibilidad.DISPONIBLE);
		}
		if (medicoId != null) {
			return disponibilidadMedicaRepository.findByMedicoIdAndEstadoOrderByFechaAscHoraInicioAsc(medicoId,
					EstadoDisponibilidad.DISPONIBLE);
		}
		if (fecha != null) {
			return disponibilidadMedicaRepository.findByFechaAndEstadoOrderByHoraInicioAsc(fecha,
					EstadoDisponibilidad.DISPONIBLE);
		}
		return disponibilidadMedicaRepository.findByEstadoOrderByFechaAscHoraInicioAsc(EstadoDisponibilidad.DISPONIBLE);
	}

	@Transactional(readOnly = true)
	public DisponibilidadMedica obtener(Long id) {
		return disponibilidadMedicaRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Disponibilidad no encontrada con id " + id));
	}

	public DisponibilidadMedica registrar(DisponibilidadMedicaRequest request) {
		Medico medico = obtenerMedico(request.medicoId());
		LocalDate fecha = validarFecha(request.fecha());
		LocalTime horaInicio = validarHora(request.horaInicio(), "horaInicio");
		LocalTime horaFin = validarHora(request.horaFin(), "horaFin");
		if (!horaInicio.isBefore(horaFin)) {
			throw new ReglaNegocioException("La hora de inicio debe ser anterior a la hora de fin.");
		}
		boolean solapado = disponibilidadMedicaRepository.existeSolapamiento(medico.getId(), fecha, horaInicio, horaFin,
				EstadoDisponibilidad.CANCELADA);
		if (solapado) {
			throw new ReglaNegocioException("El medico ya tiene una disponibilidad que se cruza con ese horario.");
		}
		return disponibilidadMedicaRepository.save(new DisponibilidadMedica(medico, fecha, horaInicio, horaFin));
	}

	public DisponibilidadMedica reservar(Long id) {
		DisponibilidadMedica disponibilidad = obtener(id);
		disponibilidad.reservar();
		return disponibilidad;
	}

	public DisponibilidadMedica liberar(Long id) {
		DisponibilidadMedica disponibilidad = obtener(id);
		disponibilidad.liberar();
		return disponibilidad;
	}

	public DisponibilidadMedica cancelar(Long id) {
		DisponibilidadMedica disponibilidad = obtener(id);
		disponibilidad.cancelar();
		return disponibilidad;
	}

	private Medico obtenerMedico(Long medicoId) {
		Validaciones.idObligatorio(medicoId, "medicoId");
		Medico medico = medicoRepository.findById(medicoId)
				.orElseThrow(() -> new RecursoNoEncontradoException("Medico no encontrado con id " + medicoId));
		if (!medico.isActivo()) {
			throw new ReglaNegocioException("El medico seleccionado no esta activo.");
		}
		return medico;
	}

	private LocalDate validarFecha(LocalDate fecha) {
		if (fecha == null) {
			throw new ReglaNegocioException("El campo fecha es obligatorio.");
		}
		if (fecha.isBefore(LocalDate.now())) {
			throw new ReglaNegocioException("La disponibilidad no puede registrarse en una fecha pasada.");
		}
		return fecha;
	}

	private LocalTime validarHora(LocalTime hora, String campo) {
		if (hora == null) {
			throw new ReglaNegocioException("El campo " + campo + " es obligatorio.");
		}
		return hora;
	}
}
