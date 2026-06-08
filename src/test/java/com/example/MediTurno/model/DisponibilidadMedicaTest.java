package com.example.MediTurno.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

class DisponibilidadMedicaTest {

	@Test
	void reservaDisponibilidadLibre() {
		DisponibilidadMedica disponibilidad = new DisponibilidadMedica(new Medico(), LocalDate.now().plusDays(1),
				LocalTime.of(8, 0), LocalTime.of(8, 30));

		disponibilidad.reservar();

		assertEquals(EstadoDisponibilidad.OCUPADA, disponibilidad.getEstado());
	}

	@Test
	void liberaDisponibilidadOcupada() {
		DisponibilidadMedica disponibilidad = new DisponibilidadMedica(new Medico(), LocalDate.now().plusDays(1),
				LocalTime.of(8, 0), LocalTime.of(8, 30));
		disponibilidad.reservar();

		disponibilidad.liberar();

		assertEquals(EstadoDisponibilidad.DISPONIBLE, disponibilidad.getEstado());
	}

	@Test
	void noReservaDisponibilidadOcupada() {
		DisponibilidadMedica disponibilidad = new DisponibilidadMedica(new Medico(), LocalDate.now().plusDays(1),
				LocalTime.of(8, 0), LocalTime.of(8, 30));
		disponibilidad.reservar();

		assertThrows(IllegalStateException.class, disponibilidad::reservar);
	}
}
