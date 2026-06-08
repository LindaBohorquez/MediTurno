package com.example.MediTurno.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.MediTurno.model.DisponibilidadMedica;
import com.example.MediTurno.model.EstadoDisponibilidad;

public interface DisponibilidadMedicaRepository extends JpaRepository<DisponibilidadMedica, Long> {

	List<DisponibilidadMedica> findByEstadoOrderByFechaAscHoraInicioAsc(EstadoDisponibilidad estado);

	List<DisponibilidadMedica> findByFechaAndEstadoOrderByHoraInicioAsc(LocalDate fecha, EstadoDisponibilidad estado);

	List<DisponibilidadMedica> findByMedicoIdAndEstadoOrderByFechaAscHoraInicioAsc(Long medicoId,
			EstadoDisponibilidad estado);

	List<DisponibilidadMedica> findByMedicoIdAndFechaAndEstadoOrderByHoraInicioAsc(Long medicoId, LocalDate fecha,
			EstadoDisponibilidad estado);

	@Query("""
			select case when count(d) > 0 then true else false end
			from DisponibilidadMedica d
			where d.medico.id = :medicoId
				and d.fecha = :fecha
				and d.estado <> :estadoExcluido
				and d.horaInicio < :horaFin
				and d.horaFin > :horaInicio
			""")
	boolean existeSolapamiento(@Param("medicoId") Long medicoId, @Param("fecha") LocalDate fecha,
			@Param("horaInicio") LocalTime horaInicio, @Param("horaFin") LocalTime horaFin,
			@Param("estadoExcluido") EstadoDisponibilidad estadoExcluido);
}
