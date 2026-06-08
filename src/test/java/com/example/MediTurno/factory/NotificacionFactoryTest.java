package com.example.MediTurno.factory;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;

class NotificacionFactoryTest {

	@Test
	void creaNotificacionPorCanal() {
		Notificador email = notificador(CanalNotificacion.EMAIL);
		Notificador sms = notificador(CanalNotificacion.SMS);
		Notificador whatsapp = notificador(CanalNotificacion.WHATSAPP);
		NotificacionFactory factory = new NotificacionFactory(List.of(email, sms, whatsapp));

		assertSame(email, factory.crear(CanalNotificacion.EMAIL));
		assertSame(sms, factory.crear(CanalNotificacion.SMS));
		assertSame(whatsapp, factory.crear(CanalNotificacion.WHATSAPP));
	}

	@Test
	void usaEmailComoCanalPorDefecto() {
		Notificador email = notificador(CanalNotificacion.EMAIL);
		NotificacionFactory factory = new NotificacionFactory(List.of(email));

		assertSame(email, factory.crear(null));
	}

	private Notificador notificador(CanalNotificacion canal) {
		Notificador notificador = mock(Notificador.class);
		when(notificador.canal()).thenReturn(canal);
		return notificador;
	}
}
