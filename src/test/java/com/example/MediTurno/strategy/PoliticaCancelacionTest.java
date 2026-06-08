package com.example.MediTurno.strategy;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import com.example.MediTurno.exception.ReglaNegocioException;
import com.example.MediTurno.model.Cita;
import com.example.MediTurno.model.DisponibilidadMedica;
import com.example.MediTurno.model.EstadoCita;
import com.example.MediTurno.model.Medico;
import com.example.MediTurno.model.Paciente;
import com.example.MediTurno.model.ServicioMedico;

class PoliticaCancelacionTest {

	@Test
	void estrategiaFlexibleAceptaCitaFutura() {
		Cita cita = citaPara(LocalDate.now().plusDays(1), LocalTime.of(9, 0));
		CancelacionFlexibleStrategy estrategia = new CancelacionFlexibleStrategy();

		assertDoesNotThrow(() -> estrategia.validar(cita));
	}

	@Test
	void estrategiaRestrictivaRechazaCancelacionTardia() {
		Cita cita = citaPara(LocalDate.now(), LocalTime.now().plusHours(2));
		CancelacionRestrictivaStrategy estrategia = new CancelacionRestrictivaStrategy();

		assertThrows(ReglaNegocioException.class, () -> estrategia.validar(cita));
	}

	@Test
	void estrategiaRestrictivaAceptaCitaConMasDeVeinticuatroHoras() {
		Cita cita = citaPara(LocalDate.now().plusDays(2), LocalTime.of(10, 0));
		CancelacionRestrictivaStrategy estrategia = new CancelacionRestrictivaStrategy();

		assertDoesNotThrow(() -> estrategia.validar(cita));
	}

	@Test
	void estrategiaFlexibleRechazaCitaYaCancelada() {
		Cita cita = citaPara(LocalDate.now().plusDays(1), LocalTime.of(9, 0));
		cita.setEstado(EstadoCita.CANCELADA);
		CancelacionFlexibleStrategy estrategia = new CancelacionFlexibleStrategy();

		assertThrows(ReglaNegocioException.class, () -> estrategia.validar(cita));
	}

	private Cita citaPara(LocalDate fecha, LocalTime horaInicio) {
		DisponibilidadMedica disponibilidad = new DisponibilidadMedica(new Medico(), fecha, horaInicio,
				horaInicio.plusMinutes(30));
		Cita cita = new Cita(new Paciente(), new Medico(), new ServicioMedico(), disponibilidad, "Control", null);
		cita.setEstado(EstadoCita.AGENDADA);
		return cita;
	}
}
