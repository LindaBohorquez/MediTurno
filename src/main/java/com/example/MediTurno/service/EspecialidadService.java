package com.example.MediTurno.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.MediTurno.exception.RecursoNoEncontradoException;
import com.example.MediTurno.exception.ReglaNegocioException;
import com.example.MediTurno.model.Especialidad;
import com.example.MediTurno.repository.EspecialidadRepository;

@Service
@Transactional
public class EspecialidadService {

	private final EspecialidadRepository especialidadRepository;

	public EspecialidadService(EspecialidadRepository especialidadRepository) {
		this.especialidadRepository = especialidadRepository;
	}

	@Transactional(readOnly = true)
	public List<Especialidad> listar() {
		return especialidadRepository.findAll(Sort.by("nombre").ascending());
	}

	@Transactional(readOnly = true)
	public Especialidad obtener(Long id) {
		return especialidadRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Especialidad no encontrada con id " + id));
	}

	public Especialidad crear(Especialidad especialidad) {
		normalizar(especialidad);
		validarNombreUnico(null, especialidad.getNombre());
		especialidad.setActiva(true);
		return especialidadRepository.save(especialidad);
	}

	public Especialidad actualizar(Long id, Especialidad datos) {
		Especialidad especialidad = obtener(id);
		normalizar(datos);
		validarNombreUnico(id, datos.getNombre());
		especialidad.setNombre(datos.getNombre());
		especialidad.setDescripcion(datos.getDescripcion());
		especialidad.setActiva(datos.isActiva());
		return especialidad;
	}

	public void desactivar(Long id) {
		Especialidad especialidad = obtener(id);
		especialidad.setActiva(false);
	}

	private void normalizar(Especialidad especialidad) {
		especialidad.setNombre(Validaciones.textoObligatorio(especialidad.getNombre(), "nombre"));
	}

	private void validarNombreUnico(Long idActual, String nombre) {
		especialidadRepository.findByNombreIgnoreCase(nombre)
				.filter(especialidad -> !especialidad.getId().equals(idActual))
				.ifPresent(especialidad -> {
					throw new ReglaNegocioException("Ya existe una especialidad con ese nombre.");
				});
	}
}
