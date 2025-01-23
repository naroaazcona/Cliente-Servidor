/**
 * This code is based on solutions provided by Claude Sonnet 3.5 and 
 * adapted using GitHub Copilot. It has been thoroughly reviewed 
 * and validated to ensure correctness and that it is free of errors.
 */
package es.deusto.sd.auctions.client.proxies;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

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
	    String url = apiBaseUrl + "/strava/retos";
	    
	    try {
	        List<Map<String, Object>> retosMaps = restTemplate.getForObject(url, List.class);
	        
	        ObjectMapper objectMapper = new ObjectMapper();
	        List<Reto> retos = retosMaps.stream()
	            .map(retoMap -> objectMapper.convertValue(retoMap, Reto.class))
	            .collect(Collectors.toList());
	        
	        System.out.println("Número de retos obtenidos: " + retos.size());
	        return retos;
	    } catch (HttpStatusCodeException e) {
	        switch (e.getStatusCode().value()) {
	            case 404 -> throw new RuntimeException("Retos no encontrados.");
	            case 500 -> throw new RuntimeException("Error interno del servidor al obtener retos.");
	            default -> throw new RuntimeException("No se pudieron recuperar los Retos: " + e.getStatusText());
	        }
	    } catch (Exception e) {
	        System.err.println("Error al obtener retos: " + e.getMessage());
	        throw new RuntimeException("Error al obtener retos", e);
	    }
	}

	@Override
	public List<Reto> getRetosXDeporte(String Deporte) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Reto getDetallesDeReto(Long IdReto) {
	    try {
	        String url = apiBaseUrl + "/strava/reto/" + IdReto;
	        return restTemplate.getForObject(url, Reto.class);
	    } catch (HttpClientErrorException e) {
	        if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
	            throw new RuntimeException("Reto no encontrado con ID: " + IdReto);
	        }
	        throw new RuntimeException("Error al obtener detalles del reto", e);
	    }
	
	}

	@Override
	public List<Sesion> getSesionesPorReto(Long id) {
		  try {
		        String url = apiBaseUrl + "/strava/retos/" + id + "/sesiones";
		        
		        ParameterizedTypeReference<List<Sesion>> responseType = 
		            new ParameterizedTypeReference<List<Sesion>>() {};
		        
		        ResponseEntity<List<Sesion>> response = 
		            restTemplate.exchange(url, HttpMethod.GET, null, responseType);
		        
		        List<Sesion> sesiones = response.getBody();
		        
		        System.out.println("Sesiones obtenidas: " + (sesiones != null ? sesiones.size() : "0"));
		        
		        return sesiones;
		    } catch (RestClientException e) {
		        System.err.println("Error al obtener sesiones: " + e.getMessage());
		        throw new RuntimeException("No se pudieron recuperar las sesiones", e);
		    }
	}

	@Override
	public List<Sesion> getTodasSesiones(String token) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Sesion getDetalleSesion(Long idSesion) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void guardarSesion(Long retoId, Sesion sesion,String token) {
	    try {
	        System.out.println("RestTemplateProxy - Guardando sesión para reto: " + retoId);
	        
	        
	        String url = this.apiBaseUrl + "/strava/retos/" + retoId + "/sesiones";
	        
	        if (token != null) {
	            url += "?token=" + token;
	        }
	        
	        System.out.println("URL de la petición: " + url);
	        
	        restTemplate.postForObject(url, sesion, Void.class);
	        
	        System.out.println("RestTemplateProxy - Sesión guardada exitosamente");
	        
	    } catch (Exception e) {
	        System.err.println("RestTemplateProxy - Error al guardar la sesión: " + e.getMessage());
	        throw new RuntimeException("Error al guardar la sesión: " + e.getMessage());
	    }
	}


}