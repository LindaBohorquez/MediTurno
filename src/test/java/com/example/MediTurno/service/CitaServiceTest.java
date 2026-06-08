package com.example.MediTurno.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.MediTurno.dto.CancelarCitaRequest;
import com.example.MediTurno.dto.CitaRequest;
import com.example.MediTurno.dto.ReprogramarCitaRequest;
import com.example.MediTurno.exception.ReglaNegocioException;
import com.example.MediTurno.model.Cita;
import com.example.MediTurno.model.DisponibilidadMedica;
import com.example.MediTurno.model.Especialidad;
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
import com.example.MediTurno.strategy.PoliticaCancelacion;
import com.example.MediTurno.strategy.PoliticaCancelacionSelector;
import com.example.MediTurno.strategy.TipoPoliticaCancelacion;

@ExtendWith(MockitoExtension.class)
class CitaServiceTest {

	@Mock
	private CitaRepository citaRepository;

	@Mock
	private PacienteRepository pacienteRepository;

	@Mock
	private MedicoRepository medicoRepository;

	@Mock
	private ServicioMedicoRepository servicioMedicoRepository;

	@Mock
	private DisponibilidadMedicaRepository disponibilidadMedicaRepository;

	@Mock
	private PoliticaCancelacionSelector politicaCancelacionSelector;

	@Mock
	private PoliticaCancelacion politicaCancelacion;

	private CitaService citaService;
	private Paciente paciente;
	private Medico medico;
	private ServicioMedico servicioMedico;
	private DisponibilidadMedica disponibilidad;

	@BeforeEach
	void setUp() {
		citaService = new CitaService(citaRepository, pacienteRepository, medicoRepository, servicioMedicoRepository,
				disponibilidadMedicaRepository, politicaCancelacionSelector);
		Especialidad especialidad = especialidad(1L);
		paciente = paciente(1L);
		medico = medico(1L, especialidad);
		servicioMedico = servicioMedico(1L, especialidad);
		disponibilidad = disponibilidad(1L, medico, EstadoDisponibilidad.DISPONIBLE, LocalDate.now().plusDays(3),
				LocalTime.of(9, 0), LocalTime.of(9, 30));
	}

	@Test
	void agendaCitaConDisponibilidadValida() {
		when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
		when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));
		when(servicioMedicoRepository.findById(1L)).thenReturn(Optional.of(servicioMedico));
		when(disponibilidadMedicaRepository.findById(1L)).thenReturn(Optional.of(disponibilidad));
		when(citaRepository.existeCitaActivaParaDisponibilidad(any(), any())).thenReturn(false);
		when(citaRepository.save(any(Cita.class))).thenAnswer(invocation -> {
			Cita cita = invocation.getArgument(0);
			cita.setId(1L);
			return cita;
		});

		Cita cita = citaService.agendar(new CitaRequest(1L, 1L, 1L, 1L, "Control general", null));

		assertEquals(EstadoCita.AGENDADA, cita.getEstado());
		assertEquals(EstadoDisponibilidad.OCUPADA, disponibilidad.getEstado());
		assertSame(paciente, cita.getPaciente());
		ArgumentCaptor<Cita> captor = ArgumentCaptor.forClass(Cita.class);
		verify(citaRepository).save(captor.capture());
		assertEquals(LocalDate.now().plusDays(3), captor.getValue().getFecha());
	}

	@Test
	void rechazaCitaConDisponibilidadOcupada() {
		disponibilidad.setEstado(EstadoDisponibilidad.OCUPADA);
		when(pacienteRepository.findById(1L)).thenReturn(Optional.of(paciente));
		when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));
		when(servicioMedicoRepository.findById(1L)).thenReturn(Optional.of(servicioMedico));
		when(disponibilidadMedicaRepository.findById(1L)).thenReturn(Optional.of(disponibilidad));

		assertThrows(ReglaNegocioException.class,
				() -> citaService.agendar(new CitaRequest(1L, 1L, 1L, 1L, "Control general", null)));

		verify(citaRepository, never()).save(any());
	}

	@Test
	void cancelaCitaValidaYLiberarDisponibilidad() {
		disponibilidad.setEstado(EstadoDisponibilidad.OCUPADA);
		Cita cita = cita(paciente, medico, servicioMedico, disponibilidad);
		when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
		when(politicaCancelacionSelector.seleccionar(TipoPoliticaCancelacion.FLEXIBLE)).thenReturn(politicaCancelacion);

		Cita cancelada = citaService.cancelar(1L, new CancelarCitaRequest("Paciente no puede asistir",
				TipoPoliticaCancelacion.FLEXIBLE));

		assertEquals(EstadoCita.CANCELADA, cancelada.getEstado());
		assertEquals(EstadoDisponibilidad.DISPONIBLE, disponibilidad.getEstado());
		verify(politicaCancelacion).validar(cita);
	}

	@Test
	void reprogramaCitaConNuevaDisponibilidad() {
		disponibilidad.setEstado(EstadoDisponibilidad.OCUPADA);
		Cita cita = cita(paciente, medico, servicioMedico, disponibilidad);
		DisponibilidadMedica nuevaDisponibilidad = disponibilidad(2L, medico, EstadoDisponibilidad.DISPONIBLE,
				LocalDate.now().plusDays(5), LocalTime.of(10, 0), LocalTime.of(10, 30));
		when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
		when(disponibilidadMedicaRepository.findById(2L)).thenReturn(Optional.of(nuevaDisponibilidad));
		when(citaRepository.existeCitaActivaParaDisponibilidad(any(), any())).thenReturn(false);

		Cita reprogramada = citaService.reprogramar(1L, new ReprogramarCitaRequest(2L, "Cambio solicitado"));

		assertEquals(EstadoCita.REPROGRAMADA, reprogramada.getEstado());
		assertSame(nuevaDisponibilidad, reprogramada.getDisponibilidadMedica());
		assertEquals(EstadoDisponibilidad.DISPONIBLE, disponibilidad.getEstado());
		assertEquals(EstadoDisponibilidad.OCUPADA, nuevaDisponibilidad.getEstado());
		assertEquals(LocalDate.now().plusDays(5), reprogramada.getFecha());
	}

	@Test
	void reprogramaCitaCanceladaDespuesDeLiberarDisponibilidad() {
		disponibilidad.setEstado(EstadoDisponibilidad.DISPONIBLE);
		Cita cita = cita(paciente, medico, servicioMedico, disponibilidad);
		cita.setEstado(EstadoCita.CANCELADA);
		DisponibilidadMedica nuevaDisponibilidad = disponibilidad(2L, medico, EstadoDisponibilidad.DISPONIBLE,
				LocalDate.now().plusDays(6), LocalTime.of(11, 0), LocalTime.of(11, 30));
		when(citaRepository.findById(1L)).thenReturn(Optional.of(cita));
		when(disponibilidadMedicaRepository.findById(2L)).thenReturn(Optional.of(nuevaDisponibilidad));
		when(citaRepository.existeCitaActivaParaDisponibilidad(any(), any())).thenReturn(false);

		Cita reprogramada = citaService.reprogramar(1L, new ReprogramarCitaRequest(2L, "Nueva fecha asignada"));

		assertEquals(EstadoCita.REPROGRAMADA, reprogramada.getEstado());
		assertSame(nuevaDisponibilidad, reprogramada.getDisponibilidadMedica());
		assertEquals(EstadoDisponibilidad.OCUPADA, nuevaDisponibilidad.getEstado());
	}

	private Paciente paciente(Long id) {
		Paciente paciente = new Paciente("Ana", "Gomez", "123", "ana@example.com", "3001234567",
				LocalDate.of(1995, 1, 10));
		paciente.setId(id);
		return paciente;
	}

	private Especialidad especialidad(Long id) {
		Especialidad especialidad = new Especialidad("Medicina general", "Atencion primaria");
		especialidad.setId(id);
		return especialidad;
	}

	private Medico medico(Long id, Especialidad especialidad) {
		Medico medico = new Medico("Carlos", "Perez", "456", "carlos@example.com", "3109876543", "RM-001",
				especialidad);
		medico.setId(id);
		return medico;
	}

	private ServicioMedico servicioMedico(Long id, Especialidad especialidad) {
		ServicioMedico servicio = new ServicioMedico("Consulta general", "Consulta medica", 30,
				BigDecimal.valueOf(50000), especialidad);
		servicio.setId(id);
		return servicio;
	}

	private DisponibilidadMedica disponibilidad(Long id, Medico medico, EstadoDisponibilidad estado, LocalDate fecha,
			LocalTime horaInicio, LocalTime horaFin) {
		DisponibilidadMedica disponibilidad = new DisponibilidadMedica(medico, fecha, horaInicio, horaFin);
		disponibilidad.setId(id);
		disponibilidad.setEstado(estado);
		return disponibilidad;
	}

	private Cita cita(Paciente paciente, Medico medico, ServicioMedico servicioMedico,
			DisponibilidadMedica disponibilidad) {
		Cita cita = new Cita(paciente, medico, servicioMedico, disponibilidad, "Control", null);
		cita.setId(1L);
		return cita;
	}
}
