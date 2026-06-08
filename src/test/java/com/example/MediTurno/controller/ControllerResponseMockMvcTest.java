package com.example.MediTurno.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
class ControllerResponseMockMvcTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void crearEspecialidadResponde201() throws Exception {
		mockMvc.perform(post("/api/especialidades")
				.contentType("application/json")
				.content(json(Map.of(
						"nombre", "Pediatria " + System.nanoTime(),
						"descripcion", "Atencion infantil"))))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists());
	}

	@Test
	void solicitudInvalidaResponde400() throws Exception {
		mockMvc.perform(post("/api/especialidades")
				.contentType("application/json")
				.content("{}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("El campo nombre es obligatorio."));
	}

	@Test
	void recursoNoEncontradoResponde404() throws Exception {
		mockMvc.perform(get("/api/pacientes/{id}", 999999L))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Paciente no encontrado con id 999999"));
	}

	@Test
	void eliminarEspecialidadResponde204() throws Exception {
		Long especialidadId = id(mockMvc.perform(post("/api/especialidades")
				.contentType("application/json")
				.content(json(Map.of(
						"nombre", "Dermatologia " + System.nanoTime(),
						"descripcion", "Piel"))))
				.andExpect(status().isCreated())
				.andReturn());

		mockMvc.perform(delete("/api/especialidades/{id}", especialidadId))
				.andExpect(status().isNoContent());
	}

	private Long id(MvcResult result) throws Exception {
		JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
		return node.get("id").asLong();
	}

	private String json(Object value) throws Exception {
		return objectMapper.writeValueAsString(value);
	}
}
