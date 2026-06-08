package com.example.MediTurno.integration;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.jpa.show-sql=false",
		"spring.h2.console.enabled=false"
})
class CitaFlowMockMvcIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void ejecutaFlujoCompletoPorHttp() throws Exception {
		String sufijo = String.valueOf(System.nanoTime());
		Long especialidadId = id(postJson("/api/especialidades", Map.of(
				"nombre", "Medicina general " + sufijo,
				"descripcion", "Atencion primaria")));

		Long medicoId = id(postJson("/api/medicos", Map.of(
				"nombres", "Carlos",
				"apellidos", "Perez",
				"documento", "MED-" + sufijo,
				"correo", "medico." + sufijo + "@mediturno.com",
				"telefono", "3101234567",
				"registroMedico", "RM-" + sufijo,
				"especialidadId", especialidadId)));

		Long pacienteId = id(postJson("/api/pacientes", Map.of(
				"nombres", "Ana",
				"apellidos", "Gomez",
				"documento", "PAC-" + sufijo,
				"correo", "paciente." + sufijo + "@example.com",
				"telefono", "3001234567",
				"fechaNacimiento", "1995-01-10")));

		Long servicioId = id(postJson("/api/servicios-medicos", Map.of(
				"nombre", "Consulta general",
				"descripcion", "Consulta medica general",
				"duracionMinutos", 30,
				"tarifa", BigDecimal.valueOf(50000),
				"especialidadId", especialidadId)));

		LocalDate fechaInicial = LocalDate.now().plusDays(10);
		Long disponibilidadId = id(postJson("/api/disponibilidades", Map.of(
				"medicoId", medicoId,
				"fecha", fechaInicial.toString(),
				"horaInicio", "09:00:00",
				"horaFin", "09:30:00")));

		Long citaId = id(postJson("/api/citas", Map.of(
				"pacienteId", pacienteId,
				"medicoId", medicoId,
				"servicioMedicoId", servicioId,
				"disponibilidadMedicaId", disponibilidadId,
				"motivo", "Control general",
				"observaciones", "Primera cita")));

		mockMvc.perform(get("/api/disponibilidades/{id}", disponibilidadId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.estado").value("OCUPADA"));

		mockMvc.perform(patch("/api/citas/{id}/cancelar", citaId)
				.contentType("application/json")
				.content(json(Map.of("motivo", "Paciente no puede asistir", "politica", "FLEXIBLE"))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.estado").value("CANCELADA"));

		mockMvc.perform(get("/api/disponibilidades/{id}", disponibilidadId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.estado").value("DISPONIBLE"));

		Long nuevaDisponibilidadId = id(postJson("/api/disponibilidades", Map.of(
				"medicoId", medicoId,
				"fecha", fechaInicial.plusDays(1).toString(),
				"horaInicio", "10:00:00",
				"horaFin", "10:30:00")));

		mockMvc.perform(patch("/api/citas/{id}/reprogramar", citaId)
				.contentType("application/json")
				.content(json(Map.of(
						"nuevaDisponibilidadId", nuevaDisponibilidadId,
						"observaciones", "Cambio solicitado"))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.estado").value("REPROGRAMADA"))
				.andExpect(jsonPath("$.disponibilidadMedica.estado").value("OCUPADA"));

		mockMvc.perform(get("/api/reportes/citas")
				.param("desde", fechaInicial.minusDays(1).toString())
				.param("hasta", fechaInicial.plusDays(2).toString()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.total").value(greaterThanOrEqualTo(1)))
				.andExpect(jsonPath("$.reprogramadas").value(greaterThanOrEqualTo(1)));
	}

	private MvcResult postJson(String url, Object body) throws Exception {
		return mockMvc.perform(post(url)
				.contentType("application/json")
				.content(json(body)))
				.andExpect(status().isCreated())
				.andReturn();
	}

	private Long id(MvcResult result) throws Exception {
		JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
		return node.get("id").asLong();
	}

	private String json(Object value) throws Exception {
		return objectMapper.writeValueAsString(value);
	}
}
