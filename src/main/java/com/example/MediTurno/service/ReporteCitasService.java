package com.example.MediTurno.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.MediTurno.dto.ReporteCitasResponse;
import com.example.MediTurno.exception.ReglaNegocioException;
import com.example.MediTurno.model.Cita;
import com.example.MediTurno.model.EstadoCita;
import com.example.MediTurno.repository.CitaRepository;

@Service
@Transactional(readOnly = true)
public class ReporteCitasService {

	private final CitaRepository citaRepository;

	public ReporteCitasService(CitaRepository citaRepository) {
		this.citaRepository = citaRepository;
	}

	public ReporteCitasResponse generar(LocalDate desde, LocalDate hasta, Long medicoId) {
		LocalDate fechaDesde = desde == null ? LocalDate.now().minusMonths(1) : desde;
		LocalDate fechaHasta = hasta == null ? LocalDate.now().plusMonths(1) : hasta;
		if (fechaDesde.isAfter(fechaHasta)) {
			throw new ReglaNegocioException("La fecha desde no puede ser posterior a la fecha hasta.");
		}
		List<Cita> citas = medicoId == null
				? citaRepository.findByFechaBetweenOrderByFechaAscHoraInicioAsc(fechaDesde, fechaHasta)
				: citaRepository.findByMedicoIdAndFechaBetweenOrderByFechaAscHoraInicioAsc(medicoId, fechaDesde,
						fechaHasta);
		return new ReporteCitasResponse(
				citas.size(),
				contar(citas, EstadoCita.AGENDADA),
				contar(citas, EstadoCita.CONFIRMADA),
				contar(citas, EstadoCita.CANCELADA),
				contar(citas, EstadoCita.REPROGRAMADA),
				contar(citas, EstadoCita.ATENDIDA),
				citas);
	}

	private long contar(List<Cita> citas, EstadoCita estado) {
		return citas.stream().filter(cita -> cita.getEstado() == estado).count();
	}
}
