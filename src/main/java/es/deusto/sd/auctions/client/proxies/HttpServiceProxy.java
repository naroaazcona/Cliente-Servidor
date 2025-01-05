/**
 * This code is based on solutions provided by Claude Sonnet 3.5 and 
 * adapted using GitHub Copilot. It has been thoroughly reviewed 
 * and validated to ensure correctness and that it is free of errors.
 */
package es.deusto.sd.auctions.client.proxies;

import java.io.IOException;


import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.deusto.sd.auctions.client.data.Credendiales;
import es.deusto.sd.auctions.client.data.Reto;
import es.deusto.sd.auctions.client.data.Sesion;



/**
 * HttpServiceProxy class is an implementation of the Service Proxy design pattern
 * that communicates with the AuctionsService using simple HTTP requests via Java's
 * HttpClient. This class serves as an intermediary for the client to perform 
 * CRUD operations, such as user authentication (login/logout), retrieving categories 
 * and articles, and placing bids on articles. By encapsulating the HTTP request logic 
 * and handling various exceptions, this proxy provides a cleaner interface for clients 
 * to interact with the underlying service.
 * 
 * The class uses Java's HttpClient which allows for asynchronous and synchronous 
 * communication with HTTP servers. It leverages the `HttpRequest` and `HttpResponse` 
 * classes to construct and send requests, simplifying the process of making HTTP calls. 
 * The ObjectMapper from the Jackson library is employed to serialize and deserialize 
 * JSON data, facilitating easy conversion between Java objects and their JSON 
 * representations. This is particularly useful for converting complex data structures, 
 * like the `Credentials`, `Category`, and `Article` classes, into JSON format for 
 * transmission in HTTP requests, and vice versa for processing the responses.
 * 
 * The absence of the @Service annotation indicates that this class is not managed 
 * by a Spring container, which means that it will not benefit from Spring's 
 * dependency injection features. Instead, it operates independently, which can 
 * be suitable for applications preferring a more lightweight approach without 
 * the overhead of a full Spring context.
 * 
 * (Description generated with ChatGPT 4o mini)
 */
public class HttpServiceProxy implements IAuctionsServiceProxy {
    private static final String BASE_URL = "http://localhost:8081";
    private final HttpClient httpCliente;
    private final ObjectMapper objectMapper;
    
    public HttpServiceProxy() {
        this.httpCliente = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }
    
    
	@Override
	public String login(Credendiales credenciales) {
		 try {
	            String credentialsJson = objectMapper.writeValueAsString(credenciales);

	            HttpRequest request = HttpRequest.newBuilder()
	                .uri(URI.create(BASE_URL + "/auth/login"))
	                .header("Content-Type", "application/json")
	                .POST(HttpRequest.BodyPublishers.ofString(credentialsJson))
	                .build();

	            HttpResponse<String> response = httpCliente.send(request, HttpResponse.BodyHandlers.ofString());

	            return switch (response.statusCode()) {
	                case 200 -> response.body(); // Successful login, returns token
	                case 401 -> throw new RuntimeException("No autorizado: credenciales inválidas");
	                default -> throw new RuntimeException("Error al hacer login con el status code: " + response.statusCode());
	            };
	        } catch (IOException | InterruptedException e) {
	            throw new RuntimeException("Error al iniciar sesión", e);
	        }
	    }
		
	
	@Override
	public void logout(String token) {
		
		try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/logout"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(token))
                .build();

            HttpResponse<Void> response = httpCliente.send(request, HttpResponse.BodyHandlers.discarding());

            switch (response.statusCode()) {
                case 204 -> {} 
                case 401 -> throw new RuntimeException("No autorizado: Token no valido, error al hacer logout");
                default -> throw new RuntimeException("Error al hacer logout con el status code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error en el logout", e);
        }
    }
	@Override
	public List<Reto> getTodosRetos() {
		try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auctions/retos"))
                .header("Content-Type", "application/json")
                .GET()
                .build();
            HttpResponse<String> response = httpCliente.send(request, HttpResponse.BodyHandlers.ofString());
            
            return switch (response.statusCode()) {
            case 200 -> objectMapper.readValue(response.body(), objectMapper.getTypeFactory().constructCollectionType(List.class, Reto.class));
            case 204 -> throw new RuntimeException("Contenido no encontrado: No se han encontrado retos");
            case 500 -> throw new RuntimeException("Error interno del servidor al buscar retos");
            default -> throw new RuntimeException("No se pudieron recuperar retos con código de estado: " + response.statusCode());
            };
			}catch (IOException | InterruptedException e) {
	            throw new RuntimeException("Error while fetching categories", e);
	        }		
	}

	@Override
	public Reto getDetallesDeReto(Long IdReto) {
		try {
		   HttpRequest request = HttpRequest.newBuilder()
	                .uri(URI.create(BASE_URL + "/auctions/retos/" + IdReto))
	                .header("Content-Type", "application/json")
	                .GET()
	                .build();

	            HttpResponse<String> response = httpCliente.send(request, HttpResponse.BodyHandlers.ofString());

	            return switch (response.statusCode()) {
	                case 200 -> objectMapper.readValue(response.body(), Reto.class);
	                case 404 -> throw new RuntimeException("Not Found: Reto no encontrado");
	                case 500 -> throw new RuntimeException("Error interno del servidor al buscar retos");
	                default -> throw new RuntimeException("No se pudieron recuperar retos con código de estado: " + response.statusCode());
	            };
	        } catch (IOException | InterruptedException e) {
	            throw new RuntimeException("\n" + "Error al obtener los detalles del reto", e);
	        }
		
	}

	@Override
	public List<Reto> getRetosXDeporte(String Deporte) {
		try {
            // Encode the category name to handle spaces and special characters
            String encodedCategoryName = URLEncoder.encode(Deporte, StandardCharsets.UTF_8);
        	
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auctions/retos/" + encodedCategoryName))
                .header("Content-Type", "application/json")
                .GET()
                .build();

            HttpResponse<String> response = httpCliente.send(request, HttpResponse.BodyHandlers.ofString());

            return switch (response.statusCode()) {
                case 200 -> objectMapper.readValue(response.body(), objectMapper.getTypeFactory().constructCollectionType(List.class, Reto.class));
                case 204 -> throw new RuntimeException("Not Content: Este deporte no tiene retos");
                case 404 -> throw new RuntimeException("Not Found: Reto no encontrado");
                case 500 -> throw new RuntimeException("Error interno del servidor al buscar retos");
                default -> throw new RuntimeException("No se pudieron recuperar retos con código de estado: " + response.statusCode());
            };
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error al obtener los detalles del reto", e);
        }
    }

	@Override
	public List<Sesion> getTodasSesiones() {
		try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auctions/sesiones"))
                .header("Content-Type", "application/json")
                .GET()
                .build();
            HttpResponse<String> response = httpCliente.send(request, HttpResponse.BodyHandlers.ofString());
            
            return switch (response.statusCode()) {
            case 200 -> objectMapper.readValue(response.body(), objectMapper.getTypeFactory().constructCollectionType(List.class, Sesion.class));
            case 204 -> throw new RuntimeException("Contenido no encontrado: No se han encontrado sesiones");
            case 500 -> throw new RuntimeException("Error interno del servidor al buscar sesiones");
            default -> throw new RuntimeException("No se pudieron recuperar sesiones con código de estado: " + response.statusCode());
            };
			}catch (IOException | InterruptedException e) {
	            throw new RuntimeException("Error al obtener las sesiones", e);
	        }		
	}

	@Override
	public Sesion getDetalleSesion(Long idSesion) {
		try {
			   HttpRequest request = HttpRequest.newBuilder()
		                .uri(URI.create(BASE_URL + "/auctions/retos/" + idSesion))
		                .header("Content-Type", "application/json")
		                .GET()
		                .build();

		            HttpResponse<String> response = httpCliente.send(request, HttpResponse.BodyHandlers.ofString());

		            return switch (response.statusCode()) {
		                case 200 -> objectMapper.readValue(response.body(), Sesion.class);
		                case 404 -> throw new RuntimeException("Not Found: Sesion no encontrada");
		                case 500 -> throw new RuntimeException("Error interno del servidor al buscar la sesion");
		                default -> throw new RuntimeException("No se pudieron recuperar los detalles del reto con código de estado: " + response.statusCode());
		            };
		        } catch (IOException | InterruptedException e) {
		            throw new RuntimeException("\n" + "Error al obtener los detalles del reto", e);
		        }
			
		}

	@Override
	public List<Sesion> getSesionesPorReto(String nombreReto) {
		try {
            // Encode the category name to handle spaces and special characters
            String encodedCategoryName = URLEncoder.encode(nombreReto, StandardCharsets.UTF_8);
        	
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auctions/retos/" + encodedCategoryName))
                .header("Content-Type", "application/json")
                .GET()
                .build();

            HttpResponse<String> response = httpCliente.send(request, HttpResponse.BodyHandlers.ofString());

            return switch (response.statusCode()) {
                case 200 -> objectMapper.readValue(response.body(), objectMapper.getTypeFactory().constructCollectionType(List.class, Reto.class));
                case 204 -> throw new RuntimeException("Not Content: Este reto no tiene sesiones");
                case 404 -> throw new RuntimeException("Not Found: Reto no encontrado");
                case 500 -> throw new RuntimeException("Error interno del servidor al buscar retos");
                default -> throw new RuntimeException("No se pudieron recuperar retos con código de estado: " + response.statusCode());
            };
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error al obtener los detalles del reto", e);
        }
	}
	

	
	
}