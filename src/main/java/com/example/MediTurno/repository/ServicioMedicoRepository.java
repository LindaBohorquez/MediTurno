package com.example.MediTurno.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.MediTurno.model.ServicioMedico;

public interface ServicioMedicoRepository extends JpaRepository<ServicioMedico, Long> {

	List<ServicioMedico> findByActivoTrueOrderByNombreAsc();

	List<ServicioMedico> findByEspecialidadIdAndActivoTrueOrderByNombreAsc(Long especialidadId);
}
