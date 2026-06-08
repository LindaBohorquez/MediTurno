package com.example.MediTurno.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import com.example.MediTurno.model.Cita;
import com.example.MediTurno.model.DisponibilidadMedica;
import com.example.MediTurno.model.Especialidad;
import com.example.MediTurno.model.EstadoCita;
import com.example.MediTurno.model.EstadoDisponibilidad;
import com.example.MediTurno.model.Medico;
import com.example.MediTurno.model.Paciente;
import com.example.MediTurno.model.ServicioMedico;

@DataJpaTest(properties = "spring.jpa.show-sql=false")
class RepositoryH2Test {

	@Autowired
	private EspecialidadRepository especialidadRepository;

	@Autowired
	private MedicoRepository medicoRepository;

	@Autowired
	private PacienteRepository pacienteRepository;

	@Autowired
	private ServicioMedicoRepository servicioMedicoRepository;

	@Autowired
	private DisponibilidadMedicaRepository disponibilidadMedicaRepository;

	@Autowired
	private CitaRepository citaRepository;

	@Test
	void detectaSolapamientosDeDisponibilidadEnH2() {
		Medico medico = medico("RM-SOL");
		LocalDate fecha = LocalDate.now().plusDays(7);
		disponibilidadMedicaRepository.saveAndFlush(new DisponibilidadMedica(medico, fecha, LocalTime.of(9, 0),
				LocalTime.of(10, 0)));

		boolean solapado = disponibilidadMedicaRepository.existeSolapamiento(medico.getId(), fecha,
				LocalTime.of(9, 30), LocalTime.of(10, 30), EstadoDisponibilidad.CANCELADA);
		boolean pegadoSinSolapar = disponibilidadMedicaRepository.existeSolapamiento(medico.getId(), fecha,
				LocalTime.of(10, 0), LocalTime.of(10, 30), EstadoDisponibilidad.CANCELADA);

		assertThat(solapado).isTrue();
		assertThat(pegadoSinSolapar).isFalse();
	}

	@Test
	void filtraDisponibilidadesPorMedicoFechaYEstado() {
		Medico medico = medico("RM-FIL");
		Medico otroMedico = medico("RM-OTRO");
		LocalDate fecha = LocalDate.now().plusDays(8);
		DisponibilidadMedica esperada = disponibilidadMedicaRepository.saveAndFlush(
				new DisponibilidadMedica(medico, fecha, LocalTime.of(8, 0), LocalTime.of(8, 30)));
		DisponibilidadMedica ocupada = new DisponibilidadMedica(medico, fecha, LocalTime.of(9, 0),
				LocalTime.of(9, 30));
		ocupada.setEstado(EstadoDisponibilidad.OCUPADA);
		disponibilidadMedicaRepository.saveAndFlush(ocupada);
		disponibilidadMedicaRepository.saveAndFlush(new DisponibilidadMedica(otroMedico, fecha, LocalTime.of(10, 0),
				LocalTime.of(10, 30)));

		List<DisponibilidadMedica> resultado = disponibilidadMedicaRepository
				.findByMedicoIdAndFechaAndEstadoOrderByHoraInicioAsc(medico.getId(), fecha,
						EstadoDisponibilidad.DISPONIBLE);

		assertThat(resultado).extracting(DisponibilidadMedica::getId).containsExactly(esperada.getId());
	}

	@Test
	void detectaCitaActivaPorDisponibilidad() {
		Especialidad especialidad = especialidadRepository.save(new Especialidad("General activa", "Consulta"));
		Paciente paciente = pacienteRepository.save(new Paciente("Ana", "Gomez", "PAC-ACT",
				"ana.activa@example.com", "3001234567", LocalDate.of(1995, 1, 10)));
		Medico medico = medicoRepository.save(new Medico("Carlos", "Perez", "MED-ACT",
				"carlos.activo@example.com", "3101234567", "RM-ACT", especialidad));
		ServicioMedico servicio = servicioMedicoRepository.save(new ServicioMedico("Consulta", "General", 30,
				BigDecimal.valueOf(50000), especialidad));
		DisponibilidadMedica disponibilidad = disponibilidadMedicaRepository.save(new DisponibilidadMedica(medico,
				LocalDate.now().plusDays(9), LocalTime.of(11, 0), LocalTime.of(11, 30)));
		citaRepository.saveAndFlush(new Cita(paciente, medico, servicio, disponibilidad, "Control", null));

		boolean activa = citaRepository.existeCitaActivaParaDisponibilidad(disponibilidad.getId(),
				List.of(EstadoCita.AGENDADA, EstadoCita.CONFIRMADA, EstadoCita.REPROGRAMADA));
		boolean noActivaParaEstadoDiferente = citaRepository.existeCitaActivaParaDisponibilidad(disponibilidad.getId(),
				List.of(EstadoCita.CANCELADA));

		assertThat(activa).isTrue();
		assertThat(noActivaParaEstadoDiferente).isFalse();
	}

	private Medico medico(String registroMedico) {
		String sufijo = registroMedico.toLowerCase();
		Especialidad especialidad = especialidadRepository.save(new Especialidad("Especialidad " + registroMedico,
				"Descripcion"));
		return medicoRepository.save(new Medico("Nombre", "Apellido", "DOC-" + registroMedico,
				sufijo + "@mediturno.com", "3100000000", registroMedico, especialidad));
	}
}
