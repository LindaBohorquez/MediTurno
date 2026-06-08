package com.example.MediTurno.dto;

import com.example.MediTurno.strategy.TipoPoliticaCancelacion;

public record CancelarCitaRequest(String motivo, TipoPoliticaCancelacion politica) {
}
