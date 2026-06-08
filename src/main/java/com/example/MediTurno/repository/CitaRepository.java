package com.example.MediTurno.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.MediTurno.model.Cita;
import com.example.MediTurno.model.EstadoCita;

public interface CitaRepository extends JpaRepository<Cita, Long> {

	List<Cita> findByPacienteIdOrderByFechaDescHoraInicioDesc(Long pacienteId);

	List<Cita> findByMedicoIdOrderByFechaDescHoraInicioDesc(Long medicoId);

	List<Cita> findByFechaBetweenOrderByFechaAscHoraInicioAsc(LocalDate desde, LocalDate hasta);

	List<Cita> findByMedicoIdAndFechaBetweenOrderByFechaAscHoraInicioAsc(Long medicoId, LocalDate desde,
			LocalDate hasta);

	@Query("""
			select case when count(c) > 0 then true else false end
			from Cita c
			where c.disponibilidadMedica.id = :disponibilidadId
				and c.estado in :estados
			""")
	boolean existeCitaActivaParaDisponibilidad(@Param("disponibilidadId") Long disponibilidadId,
			@Param("estados") Collection<EstadoCita> estados);
}
