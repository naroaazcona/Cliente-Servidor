package es.deusto.sd.auctions.client.data;



public record Sesion( 
		Long id,
		String titulo,
		String deporte,
		Float distancia,
		Long horaInicio,
		Long horaFin,
		Float duracion
	) {}
