package com.example.MediTurno.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.MediTurno.dto.CancelarCitaRequest;
import com.example.MediTurno.dto.CitaRequest;
import com.example.MediTurno.dto.ReprogramarCitaRequest;
import com.example.MediTurno.exception.RecursoNoEncontradoException;
import com.example.MediTurno.exception.ReglaNegocioException;
import com.example.MediTurno.model.Cita;
import com.example.MediTurno.model.DisponibilidadMedica;
import com.example.MediTurno.model.EstadoCita;
import com.example.MediTurno.model.EstadoDisponibilidad;
import com.example.MediTurno.model.Medico;
import com.example.MediTurno.model.Paciente;
import com.example.MediTurno.model.ServicioMedico;
import com.example.MediTurno.repository.CitaRepository;
import com.example.MediTurno.repository.DisponibilidadMedicaRepository;
import com.example.MediTurno.repository.MedicoRepository;
import com.example.MediTurno.repository.PacienteRepository;
import com.example.MediTurno.repository.ServicioMedicoRepository;
import com.example.MediTurno.strategy.PoliticaCancelacionSelector;
import com.example.MediTurno.strategy.TipoPoliticaCancelacion;

@Service
@Transactional
public class CitaService {

	private static final List<EstadoCita> ESTADOS_ACTIVOS = List.of(EstadoCita.AGENDADA, EstadoCita.CONFIRMADA,
			EstadoCita.REPROGRAMADA);

	private final CitaRepository citaRepository;
	private final PacienteRepository pacienteRepository;
	private final MedicoRepository medicoRepository;
	private final ServicioMedicoRepository servicioMedicoRepository;
	private final DisponibilidadMedicaRepository disponibilidadMedicaRepository;
	private final PoliticaCancelacionSelector politicaCancelacionSelector;

	public CitaService(CitaRepository citaRepository, PacienteRepository pacienteRepository,
			MedicoRepository medicoRepository, ServicioMedicoRepository servicioMedicoRepository,
			DisponibilidadMedicaRepository disponibilidadMedicaRepository,
			PoliticaCancelacionSelector politicaCancelacionSelector) {
		this.citaRepository = citaRepository;
		this.pacienteRepository = pacienteRepository;
		this.medicoRepository = medicoRepository;
		this.servicioMedicoRepository = servicioMedicoRepository;
		this.disponibilidadMedicaRepository = disponibilidadMedicaRepository;
		this.politicaCancelacionSelector = politicaCancelacionSelector;
	}

	@Transactional(readOnly = true)
	public List<Cita> listar(Long pacienteId, Long medicoId) {
		if (pacienteId != null) {
			return citaRepository.findByPacienteIdOrderByFechaDescHoraInicioDesc(pacienteId);
		}
		if (medicoId != null) {
			return citaRepository.findByMedicoIdOrderByFechaDescHoraInicioDesc(medicoId);
		}
		return citaRepository.findAll(Sort.by("fecha").descending().and(Sort.by("horaInicio").descending()));
	}

	@Transactional(readOnly = true)
	public Cita obtener(Long id) {
		return citaRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Cita no encontrada con id " + id));
	}

	public Cita agendar(CitaRequest request) {
		Paciente paciente = obtenerPaciente(request.pacienteId());
		Medico medico = obtenerMedico(request.medicoId());
		ServicioMedico servicioMedico = obtenerServicio(request.servicioMedicoId());
		DisponibilidadMedica disponibilidad = obtenerDisponibilidad(request.disponibilidadMedicaId());

		validarMedicoActivo(medico);
		validarServicioActivo(servicioMedico);
		validarServicioCompatible(medico, servicioMedico);
		validarDisponibilidadParaMedico(disponibilidad, medico);
		validarDisponibilidadLibre(disponibilidad);

		if (citaRepository.existeCitaActivaParaDisponibilidad(disponibilidad.getId(), ESTADOS_ACTIVOS)) {
			throw new ReglaNegocioException("Ya existe una cita activa para esa disponibilidad.");
		}

		disponibilidad.reservar();
		Cita cita = new Cita(paciente, medico, servicioMedico, disponibilidad, request.motivo(), request.observaciones());
		return citaRepository.save(cita);
	}

	public Cita confirmar(Long id) {
		Cita cita = obtener(id);
		cita.confirmar();
		return cita;
	}

	public Cita cancelar(Long id, CancelarCitaRequest request) {
		Cita cita = obtener(id);
		TipoPoliticaCancelacion tipoPolitica = request == null ? null : request.politica();
		politicaCancelacionSelector.seleccionar(tipoPolitica).validar(cita);
		cita.cancelar(request == null ? null : request.motivo());
		cita.getDisponibilidadMedica().liberar();
		return cita;
	}

	public Cita reprogramar(Long id, ReprogramarCitaRequest request) {
		Cita cita = obtener(id);
		Long nuevaDisponibilidadId = Validaciones.idObligatorio(request.nuevaDisponibilidadId(),
				"nuevaDisponibilidadId");
		DisponibilidadMedica nuevaDisponibilidad = obtenerDisponibilidad(nuevaDisponibilidadId);
		if (cita.getDisponibilidadMedica().getId().equals(nuevaDisponibilidad.getId())) {
			throw new ReglaNegocioException("La nueva disponibilidad debe ser diferente a la actual.");
		}
		validarDisponibilidadLibre(nuevaDisponibilidad);
		validarServicioCompatible(nuevaDisponibilidad.getMedico(), cita.getServicioMedico());
		if (citaRepository.existeCitaActivaParaDisponibilidad(nuevaDisponibilidad.getId(), ESTADOS_ACTIVOS)) {
			throw new ReglaNegocioException("Ya existe una cita activa para la nueva disponibilidad.");
		}
		cita.getDisponibilidadMedica().liberar();
		nuevaDisponibilidad.reservar();
		cita.reprogramar(nuevaDisponibilidad, request.observaciones());
		return cita;
	}

	private Paciente obtenerPaciente(Long pacienteId) {
		Validaciones.idObligatorio(pacienteId, "pacienteId");
		Paciente paciente = pacienteRepository.findById(pacienteId)
				.orElseThrow(() -> new RecursoNoEncontradoException("Paciente no encontrado con id " + pacienteId));
		if (!paciente.isActivo()) {
			throw new ReglaNegocioException("El paciente seleccionado no esta activo.");
		}
		return paciente;
	}

	private Medico obtenerMedico(Long medicoId) {
		Validaciones.idObligatorio(medicoId, "medicoId");
		return medicoRepository.findById(medicoId)
				.orElseThrow(() -> new RecursoNoEncontradoException("Medico no encontrado con id " + medicoId));
	}

	private ServicioMedico obtenerServicio(Long servicioMedicoId) {
		Validaciones.idObligatorio(servicioMedicoId, "servicioMedicoId");
		return servicioMedicoRepository.findById(servicioMedicoId)
				.orElseThrow(() -> new RecursoNoEncontradoException("Servicio medico no encontrado con id "
						+ servicioMedicoId));
	}

	private DisponibilidadMedica obtenerDisponibilidad(Long disponibilidadMedicaId) {
		Validaciones.idObligatorio(disponibilidadMedicaId, "disponibilidadMedicaId");
		return disponibilidadMedicaRepository.findById(disponibilidadMedicaId)
				.orElseThrow(() -> new RecursoNoEncontradoException("Disponibilidad no encontrada con id "
						+ disponibilidadMedicaId));
	}

	private void validarMedicoActivo(Medico medico) {
		if (!medico.isActivo()) {
			throw new ReglaNegocioException("El medico seleccionado no esta activo.");
		}
	}

	private void validarServicioActivo(ServicioMedico servicioMedico) {
		if (!servicioMedico.isActivo()) {
			throw new ReglaNegocioException("El servicio medico seleccionado no esta activo.");
		}
	}

	private void validarServicioCompatible(Medico medico, ServicioMedico servicioMedico) {
		Long especialidadMedico = medico.getEspecialidad().getId();
		Long especialidadServicio = servicioMedico.getEspecialidad().getId();
		if (!especialidadMedico.equals(especialidadServicio)) {
			throw new ReglaNegocioException("El servicio medico no corresponde a la especialidad del medico.");
		}
	}

	private void validarDisponibilidadParaMedico(DisponibilidadMedica disponibilidad, Medico medico) {
		if (!disponibilidad.getMedico().getId().equals(medico.getId())) {
			throw new ReglaNegocioException("La disponibilidad no pertenece al medico seleccionado.");
		}
	}

	private void validarDisponibilidadLibre(DisponibilidadMedica disponibilidad) {
		if (disponibilidad.getEstado() != EstadoDisponibilidad.DISPONIBLE) {
			throw new ReglaNegocioException("La disponibilidad seleccionada no esta libre.");
		}
	}
}
