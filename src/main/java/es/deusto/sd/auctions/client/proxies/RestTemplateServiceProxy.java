/**
 * This code is based on solutions provided by Claude Sonnet 3.5 and 
 * adapted using GitHub Copilot. It has been thoroughly reviewed 
 * and validated to ensure correctness and that it is free of errors.
 */
package es.deusto.sd.auctions.client.proxies;

import java.util.List;



import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import es.deusto.sd.auctions.client.data.Credendiales;
import es.deusto.sd.auctions.client.data.Reto;
import es.deusto.sd.auctions.client.data.Sesion;


@Service
public class RestTemplateServiceProxy implements IAuctionsServiceProxy{

    private final RestTemplate restTemplate;

    @Value("${api.base.url}")
    private String apiBaseUrl;

    public RestTemplateServiceProxy(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

	@Override
	public String login(Credendiales credenciales) {
		 String url = apiBaseUrl + "/autorizacion/login";
	        
	        try {
	            return restTemplate.postForObject(url, credenciales, String.class);
	        } catch (HttpStatusCodeException e) {
	            switch (e.getStatusCode().value()) {
	                case 401 -> throw new RuntimeException("Login failed: Credenciales no validas.");
	                default -> throw new RuntimeException("Error al hacer login: " + e.getStatusText());    
	            }
	      }
	}

	@Override
	public void logout(String token) {
		   String url = apiBaseUrl + "/autorizacion/logout";
	        
	        try {
	            restTemplate.postForObject(url, token, Void.class);
	        } catch (HttpStatusCodeException e) {
	            switch (e.getStatusCode().value()) {
	                case 401 -> throw new RuntimeException("Logout failed: Token no valido.");
	                default -> throw new RuntimeException("Error al ahcer logout: " + e.getStatusText());
	            }
	        }
	    }

	

	@Override
	public List<Reto> getMisRetos(String token) {
		String url = apiBaseUrl + "/strava/reto/retosAceptados";
        
        try {
            return restTemplate.getForObject(url, List.class);
        } catch (HttpStatusCodeException e) {
            switch (e.getStatusCode().value()) {
                case 404 -> throw new RuntimeException("Retos no encontrados.");
                default -> throw new RuntimeException("No se pudieron recuperar los Retos: " + e.getStatusText());
            }
        }
	}

	@Override
	public List<Reto> getTodosRetos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Reto> getRetosXDeporte(String Deporte) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Reto getDetallesDeReto(Long IdReto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Sesion> getSesionesPorReto(String nombreReto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Sesion> getTodasSesiones() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Sesion getDetalleSesion(Long idSesion) {
		// TODO Auto-generated method stub
		return null;
	}
    

}