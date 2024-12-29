package es.deusto.sd.auctions.client.data;

public record Reto ( 
	Long idReto,
	String NombreReto,
	Long fechaInicio,
	Long fechaFin,
	String deporte,
	String tipoReto,
	String UsuarioCreador
	
	) {}
