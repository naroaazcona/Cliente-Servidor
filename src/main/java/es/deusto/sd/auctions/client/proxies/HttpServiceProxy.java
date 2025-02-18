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
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
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
    private static final String BASE_URL = "http://localhost:8080";
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
            
            System.out.println("Intentando conectar a: " + BASE_URL + "/autorizacion/login");
            System.out.println("Enviando credenciales: " + credentialsJson);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/autorizacion/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(credentialsJson))
                .build();

            HttpResponse<String> response = httpCliente.send(request, HttpResponse.BodyHandlers.ofString());
            
            System.out.println("Código de respuesta: " + response.statusCode());
            System.out.println("Cuerpo de respuesta: " + response.body());

            return switch (response.statusCode()) {
                case 200 -> response.body();
                case 401 -> throw new RuntimeException("Credenciales inválidas");
                default -> throw new RuntimeException("Error inesperado en el servidor: " + response.statusCode());
            };
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error de conexión al intentar iniciar sesión: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("La operación fue interrumpida", e);
        }
    }
	

	@Override
	public void logout(String token) {
	    try {
	        HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(BASE_URL + "/autorizacion/logout"))
	            .header("Content-Type", "application/json")
	            .POST(HttpRequest.BodyPublishers.ofString(token))
	            .build();

	        HttpResponse<Void> response = httpCliente.send(request, HttpResponse.BodyHandlers.discarding());

	        // Solo hay dos posibles respuestas según el controlador: 204 y 401
	        switch (response.statusCode()) {
	            case 204 -> {} // Logout exitoso, no hacer nada
	            case 401 -> throw new RuntimeException("Token inválido");
	            default -> throw new RuntimeException("Error inesperado en el servidor: " + response.statusCode());
	        }
	    } catch (IOException | InterruptedException e) {
	        throw new RuntimeException("Error de conexión al intentar cerrar sesión", e);
	    }
	}

	
	@Override
	public List<Reto> getTodosRetos() {
	    try {
	        HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(BASE_URL + "/strava/retos"))
	            .header("Content-Type", "application/json")
	            .GET()
	            .build();

	        HttpResponse<String> response = httpCliente.send(request, HttpResponse.BodyHandlers.ofString());
	        
	        // Loggear el JSON recibido
	        System.out.println("JSON recibido del servidor:");
	        System.out.println(response.body());
	        
	        if (response.statusCode() == 200) {
	            // Configurar ObjectMapper para ser más estricto con el mapeo
	            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	            objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
	            
	            List<Reto> retos = objectMapper.readValue(
	                response.body(),
	                objectMapper.getTypeFactory().constructCollectionType(List.class, Reto.class)
	            );
	            
	            // Verificar cada reto
	            for (Reto reto : retos) {
	                System.out.println("Reto mapeado: " + reto);
	                if (reto.id() == null) {
	                    System.out.println("Advertencia: Reto con ID null encontrado: " + reto.nombre());
	                }
	            }
	            
	            return retos;
	        } else {
	            throw new RuntimeException("Error al obtener retos: " + response.statusCode());
	        }
	    } catch (IOException | InterruptedException e) {
	        throw new RuntimeException("Error en la comunicación", e);
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
	public List<Sesion> getTodasSesiones(String token) {
	    try {
	        System.out.println("Intentando obtener todas las sesiones del usuario...");
	        
	        HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(BASE_URL + "/strava/sesion/usuario?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8)))
	            .header("Content-Type", "application/json")
	            .GET()
	            .build();
	        


	        HttpResponse<String> response = httpCliente.send(request, HttpResponse.BodyHandlers.ofString());
	        System.out.println("Respuesta del servidor: " + response.body());
	        System.out.println("Código de respuesta: " + response.statusCode());
	        System.out.println("Cuerpo de respuesta: " + response.body());
	        
	        return switch (response.statusCode()) {
	            case 200 -> {
	                List<Sesion> sesiones = objectMapper.readValue(response.body(), 
	                    objectMapper.getTypeFactory().constructCollectionType(List.class, Sesion.class));
	                System.out.println("Se obtuvieron " + sesiones.size() + " sesiones exitosamente");
	                yield sesiones;
	            }
	            case 204 -> throw new RuntimeException("No se encontraron sesiones para este usuario");
	            case 401 -> throw new RuntimeException("Token no válido");
	            case 500 -> throw new RuntimeException("Error interno del servidor");
	            default -> throw new RuntimeException("Error inesperado al obtener las sesiones. Código: " + response.statusCode());
	        };
	    } catch (IOException | InterruptedException e) {
	        System.err.println("Error al obtener las sesiones: " + e.getMessage());
	        throw new RuntimeException("Error al obtener las sesiones del usuario", e);
	    }
	}
	


	@Override
	public Sesion getDetalleSesion(Long idSesion) {
		try {
			   HttpRequest request = HttpRequest.newBuilder()
		                .uri(URI.create(BASE_URL + "/strava/sesion/" + idSesion))
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
	public List<Sesion> getSesionesPorReto(Long retoId) {  // Cambiado a Long retoId en lugar de String nombreReto
	    try {
	        System.out.println("Iniciando búsqueda de sesiones para el reto ID: " + retoId);
	        
	        // Construir la URL directamente con el ID
	        String url = BASE_URL + "/strava/retos/" + retoId + "/sesiones";
	        System.out.println("Intentando conectar a: " + url);
	        
	        HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(url))
	            .header("Content-Type", "application/json")
	            .GET()
	            .build();

	        HttpResponse<String> response = httpCliente.send(request, HttpResponse.BodyHandlers.ofString());
	        
	        System.out.println("Código de respuesta: " + response.statusCode());
	        System.out.println("Cuerpo de la respuesta: " + response.body());

	        return switch (response.statusCode()) {
	            case 200 -> {
	                List<Sesion> sesiones = objectMapper.readValue(
	                    response.body(), 
	                    objectMapper.getTypeFactory().constructCollectionType(List.class, Sesion.class)
	                );
	                System.out.println("Sesiones encontradas: " + sesiones.size());
	                yield sesiones;
	            }
	            case 404 -> {
	                System.out.println("Reto no encontrado en el servidor");
	                throw new RuntimeException("Not Found: Reto no encontrado");
	            }
	            case 500 -> throw new RuntimeException("Error interno del servidor al buscar sesiones");
	            default -> throw new RuntimeException("No se pudieron recuperar las sesiones. Código: " + response.statusCode());
	        };
	    } catch (IOException | InterruptedException e) {
	        System.err.println("Error en la comunicación: " + e.getMessage());
	        throw new RuntimeException("Error al obtener las sesiones del reto", e);
	    }
	}
	

	
	
	
	@Override
	public List<Reto> getMisRetos(String token) {
	    try {
	        System.out.println("Intentando obtener retos aceptados del usuario...");
	        
	        // Construir la URL con el parámetro de token
	        String url = BASE_URL + "/strava/reto/retosAceptados?Token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
	        System.out.println("Enviando petición a: " + url);
	        
	        HttpRequest request = HttpRequest.newBuilder()
	            .uri(URI.create(url))
	            .header("Content-Type", "application/json")
	            .GET()
	            .build();

	        HttpResponse<String> response = httpCliente.send(request, HttpResponse.BodyHandlers.ofString());
	        
	        System.out.println("Código de estado de la respuesta: " + response.statusCode());
	        System.out.println("Cuerpo de la respuesta: " + response.body());
	        
	        return switch (response.statusCode()) {
	            case 200 -> {
	                List<Reto> retos = objectMapper.readValue(response.body(), 
	                    objectMapper.getTypeFactory().constructCollectionType(List.class, Reto.class));
	                System.out.println("Se analizaron con éxito " + retos.size() + " retos");
	                yield retos;
	            }
	            case 204 -> new ArrayList<>();  // Retornar lista vacía en lugar de lanzar excepción
	            case 401 -> throw new RuntimeException("Token no válido");
	            default -> throw new RuntimeException("Error inesperado al obtener los retos. Código: " + response.statusCode());
	        };
	    } catch (IOException e) {
	        System.err.println("Error de IO: " + e.getMessage());
	        e.printStackTrace();
	        throw new RuntimeException("Error de conexión al obtener los retos: " + e.getMessage());
	    } catch (InterruptedException e) {
	        System.err.println("Error de interrupción: " + e.getMessage());
	        Thread.currentThread().interrupt();
	        throw new RuntimeException("La operación fue interrumpida");
	    }
	}



	@Override
	public void guardarSesion(Long retoId, Sesion sesion, String token) {
		// TODO Auto-generated method stub
		
	}

	
	
	
	

	
}