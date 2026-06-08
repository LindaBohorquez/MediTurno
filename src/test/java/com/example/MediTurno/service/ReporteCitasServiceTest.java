package com.example.MediTurno.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.MediTurno.dto.ReporteCitasResponse;
import com.example.MediTurno.exception.ReglaNegocioException;
import com.example.MediTurno.model.Cita;
import com.example.MediTurno.model.EstadoCita;
import com.example.MediTurno.repository.CitaRepository;

@ExtendWith(MockitoExtension.class)
class ReporteCitasServiceTest {

	@Mock
	private CitaRepository citaRepository;

	@Test
	void generaReporteConConteoPorEstados() {
		LocalDate desde = LocalDate.now().minusDays(1);
		LocalDate hasta = LocalDate.now().plusDays(1);
		when(citaRepository.findByFechaBetweenOrderByFechaAscHoraInicioAsc(desde, hasta)).thenReturn(List.of(
				cita(EstadoCita.AGENDADA),
				cita(EstadoCita.CONFIRMADA),
				cita(EstadoCita.CANCELADA),
				cita(EstadoCita.REPROGRAMADA),
				cita(EstadoCita.ATENDIDA),
				cita(EstadoCita.REPROGRAMADA)));
		ReporteCitasService service = new ReporteCitasService(citaRepository);

		ReporteCitasResponse reporte = service.generar(desde, hasta, null);

		assertThat(reporte.total()).isEqualTo(6);
		assertThat(reporte.agendadas()).isEqualTo(1);
		assertThat(reporte.confirmadas()).isEqualTo(1);
		assertThat(reporte.canceladas()).isEqualTo(1);
		assertThat(reporte.reprogramadas()).isEqualTo(2);
		assertThat(reporte.atendidas()).isEqualTo(1);
	}

	@Test
	void usaFiltroDeMedicoCuandoSeEnviaMedicoId() {
		LocalDate desde = LocalDate.now().minusDays(1);
		LocalDate hasta = LocalDate.now().plusDays(1);
		when(citaRepository.findByMedicoIdAndFechaBetweenOrderByFechaAscHoraInicioAsc(9L, desde, hasta))
				.thenReturn(List.of(cita(EstadoCita.CONFIRMADA)));
		ReporteCitasService service = new ReporteCitasService(citaRepository);

		ReporteCitasResponse reporte = service.generar(desde, hasta, 9L);

		assertThat(reporte.total()).isEqualTo(1);
		assertThat(reporte.confirmadas()).isEqualTo(1);
		verify(citaRepository).findByMedicoIdAndFechaBetweenOrderByFechaAscHoraInicioAsc(9L, desde, hasta);
	}

	@Test
	void rechazaRangoDeFechasInvalido() {
		ReporteCitasService service = new ReporteCitasService(citaRepository);

		assertThrows(ReglaNegocioException.class,
				() -> service.generar(LocalDate.now().plusDays(2), LocalDate.now(), null));
	}

	private Cita cita(EstadoCita estado) {
		Cita cita = new Cita();
		cita.setEstado(estado);
		return cita;
	}
}
