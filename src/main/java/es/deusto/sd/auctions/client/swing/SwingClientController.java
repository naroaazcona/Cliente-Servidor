/**
 * This code is based on solutions provided by Claude Sonnet 3.5 and 
 * adapted using GitHub Copilot. It has been thoroughly reviewed 
 * and validated to ensure correctness and that it is free of errors.
 */
package es.deusto.sd.auctions.client.swing;

import java.util.ArrayList;
import java.util.List;



import es.deusto.sd.auctions.client.data.Credendiales;
import es.deusto.sd.auctions.client.data.Reto;
import es.deusto.sd.auctions.client.data.Sesion;
import es.deusto.sd.auctions.client.proxies.HttpServiceProxy;
import es.deusto.sd.auctions.client.proxies.IAuctionsServiceProxy;

/**
 * SwingClientController class acts as a Controller in the Model-View-Controller 
 * (MVC) architectural pattern, managing the interaction between the SwingClientGUI 
 * (the View) and the IAuctionsServiceProxy (the Model). This class is responsible 
 * for handling user input, communicating with the service layer, and updating 
 * the view accordingly.
 * 
 * The class encapsulates the logic for user authentication (login/logout), 
 * retrieving categories and articles, and placing bids on articles. By utilizing 
 * the IAuctionsServiceProxy interface, the controller can interact with various 
 * implementations of the service proxy, such as HttpServiceProxy or RestTemplateServiceProxy, 
 * without being tightly coupled to any specific implementation. This promotes flexibility 
 * and allows for easier testing and maintenance of the application.
 * 
 * (Description generated with ChatGPT 4o mini)
 */
public class SwingClientController {
	// Service proxy for interacting with the AuctionsService using HTTP-based implementation
	private IAuctionsServiceProxy serviceProxy = new HttpServiceProxy();
	// Token to be used during the session
    private String token;

    
    
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public boolean login(String email, String contrasenia) {
        try {
            Credendiales credenciales = new Credendiales(email, contrasenia);
            token = serviceProxy.login(credenciales);
            
            return true;
        } catch (RuntimeException e) {
            throw new RuntimeException("Error al hacer Login: " + e.getMessage());
        }
    }

    public void logout() {
        serviceProxy.logout(token);
    }
    
    public List<Reto> getMisRetos(String token) {
    	try {
            if (token == null) {
                throw new RuntimeException("No hay sesión activa");
            }
            return serviceProxy.getMisRetos(token);
        } catch (Exception e) {
            throw new RuntimeException("No se pudieron obtener tus retos: " + e.getMessage());
        
    }
   }
    
   public List<Reto> getTodosRetos(){
	   return serviceProxy.getTodosRetos();
   }
   public List<Reto> getRetosXDeporte(String Deporte){
	   return serviceProxy.getRetosXDeporte(Deporte);
   }
   
   public Reto getDetallesDeReto(Long IdReto) {
	   return serviceProxy.getDetallesDeReto(IdReto);
   }
   
   public List<Sesion> getTodasSesiones(String token){
	   return serviceProxy.getTodasSesiones(token);
   }
   public List<Sesion> getSesionesXReto(long idReto){
	   return serviceProxy.getSesionesPorReto(idReto);
   }
   public Sesion getDetalleDeSesion(Long IdSesion){
	   return serviceProxy.getDetalleSesion(IdSesion);
   }
   
}