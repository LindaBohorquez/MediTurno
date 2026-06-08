package com.example.MediTurno.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.MediTurno.model.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

	Optional<Paciente> findByDocumento(String documento);

	Optional<Paciente> findByCorreoIgnoreCase(String correo);
}
