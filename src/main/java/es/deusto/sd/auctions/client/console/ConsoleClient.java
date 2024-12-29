/**
 * This code is based on solutions provided by Claude Sonnet 3.5 and 
 * adapted using GitHub Copilot. It has been thoroughly reviewed 
 * and validated to ensure correctness and that it is free of errors.
 */
package es.deusto.sd.auctions.client.console;

import java.util.List;


import es.deusto.sd.auctions.client.data.Credendiales;
import es.deusto.sd.auctions.client.data.Reto;
import es.deusto.sd.auctions.client.proxies.HttpServiceProxy;
import es.deusto.sd.auctions.client.proxies.IAuctionsServiceProxy;

/**
 * ConsoleClient class serves as a basic client implementation for the AuctionsService 
 * in a console environment. This class manages user interactions through the console, 
 * performing operations such as user login, loading categories, retrieving articles, 
 * and placing bids. It utilizes the IAuctionsServiceProxy to interact with the service layer, 
 * enabling the application to execute various auction-related functionalities.
 * 
 * (Description generated with ChatGPT 4o mini)
 */
public class ConsoleClient {
	// Service proxy for interacting with the AuctionsService using HTTP-based implementation
	private final IAuctionsServiceProxy serviceProxy = new HttpServiceProxy();	
	// Token to be used during the session
	private String token;
	// Default email and password for login
	private String defaultEmail = "gorka.ortuzar@gmail.com";
	private String defaultPassword = "1";
	

	//Prueba
	public boolean login() {
		try {
			Credendiales credenciales = new Credendiales(defaultEmail, defaultPassword);

			token = serviceProxy.login(credenciales);
			return true;
		} catch (RuntimeException e) {
			return false;
		}
	}
	public boolean cargarRetos() {
		try {
			List<Reto> retos = serviceProxy.getTodosRetos();
			
			if(retos == null || retos.isEmpty()) {
				return false;
			}
			retos.forEach(reto->reto.NombreReto());
			return true;
		}catch (RuntimeException e) {
			throw new RuntimeException("\n" + "Error al obtener los retos", e);
		}
		
	}
	
	public static void main(String[] args) {
        ConsoleClient cliente = new ConsoleClient();
        
        if(!cliente.login()|| !cliente.cargarRetos()) {
        	System.out.println("Saliendo de la aplicación por fallo en uno de los pasos.");
        }
        
		
	}
}