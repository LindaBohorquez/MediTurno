package com.example.MediTurno.service;

import com.example.MediTurno.exception.ReglaNegocioException;

final class Validaciones {

	private Validaciones() {
	}

	static String textoObligatorio(String valor, String campo) {
		if (valor == null || valor.isBlank()) {
			throw new ReglaNegocioException("El campo " + campo + " es obligatorio.");
		}
		return valor.trim();
	}

	static Long idObligatorio(Long valor, String campo) {
		if (valor == null) {
			throw new ReglaNegocioException("El campo " + campo + " es obligatorio.");
		}
		return valor;
	}
}
