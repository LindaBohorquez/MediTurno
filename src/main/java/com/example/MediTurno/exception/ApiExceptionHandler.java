package com.example.MediTurno.exception;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(RecursoNoEncontradoException.class)
	public ResponseEntity<Map<String, Object>> manejarNoEncontrado(RecursoNoEncontradoException exception,
			HttpServletRequest request) {
		return construirRespuesta(HttpStatus.NOT_FOUND, exception.getMessage(), request);
	}

	@ExceptionHandler({ ReglaNegocioException.class, IllegalStateException.class, IllegalArgumentException.class })
	public ResponseEntity<Map<String, Object>> manejarReglaNegocio(RuntimeException exception,
			HttpServletRequest request) {
		return construirRespuesta(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
	}

	private ResponseEntity<Map<String, Object>> construirRespuesta(HttpStatus status, String mensaje,
			HttpServletRequest request) {
		return ResponseEntity.status(status).body(Map.of(
				"timestamp", LocalDateTime.now(),
				"status", status.value(),
				"error", status.getReasonPhrase(),
				"message", mensaje,
				"path", request.getRequestURI()));
	}
}
