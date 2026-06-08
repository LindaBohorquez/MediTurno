package com.example.MediTurno.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.MediTurno.model.Medico;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

	Optional<Medico> findByDocumento(String documento);

	Optional<Medico> findByCorreoIgnoreCase(String correo);

	Optional<Medico> findByRegistroMedicoIgnoreCase(String registroMedico);

	List<Medico> findByActivoTrueOrderByApellidosAscNombresAsc();

	List<Medico> findByEspecialidadIdAndActivoTrueOrderByApellidosAscNombresAsc(Long especialidadId);
}
