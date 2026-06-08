package com.example.MediTurno.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.MediTurno.model.Especialidad;

public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {

	Optional<Especialidad> findByNombreIgnoreCase(String nombre);
}
