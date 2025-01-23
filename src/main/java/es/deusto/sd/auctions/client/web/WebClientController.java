/**
 * This code is based on solutions provided by Claude Sonnet 3.5 and 
 * adapted using GitHub Copilot. It has been thoroughly reviewed 
 * and validated to ensure correctness and that it is free of errors.
 */
package es.deusto.sd.auctions.client.web;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;

import es.deusto.sd.auctions.client.data.Credendiales;
import es.deusto.sd.auctions.client.data.Reto;
import es.deusto.sd.auctions.client.data.Sesion;
import es.deusto.sd.auctions.client.proxies.IAuctionsServiceProxy;
import jakarta.servlet.http.HttpServletRequest;



@Controller
public class WebClientController {

  @Autowired
  private IAuctionsServiceProxy auctionServiceProxy;
  private String token;
  
  @GetMapping("/")
  public String mostrarHome() {
      return "home";
  }
  @GetMapping("/login")
  public String mostrarLogin() {
      return "login";
  }

 
  @PostMapping("/login")
  public String procesarLogin(
		      @RequestParam(value = "email") String email, 
		      @RequestParam(value = "password") String password, 
		      Model model
		  )
  {
      try {
          // Crear objeto de credenciales
          Credendiales credenciales = new Credendiales(email, password);
          
          // Intentar hacer login a través del proxy
          token = auctionServiceProxy.login(credenciales);
          
          // Si el login es exitoso, redirigir a la página de retos
          return "redirect:/reto";
      } catch (RuntimeException e) {
          // Si hay un error (credenciales incorrectas, etc.)
          model.addAttribute("errorMessage", "Credenciales incorrectas. Por favor, inténtelo de nuevo.");
          return "login"; // Volver a la página de login con mensaje de error
      }
  }
      
  @GetMapping("/reto")
  public String mostrarRetos(Model model) {
      try {
          System.out.println("Intentando obtener todos los retos");
          
          // Obtener TODOS los retos
          List<Reto> retos = auctionServiceProxy.getTodosRetos();
          
          System.out.println("Número de retos obtenidos: " + (retos != null ? retos.size() : "0"));
          
          // Imprimir detalles de cada reto
          if (retos != null) {
              for (Reto reto : retos) {
                  System.out.println("Reto: " + reto.nombre()+ ", Deporte: " + reto.deporte());
              }
          }
          
          // Añadir los retos al modelo
          model.addAttribute("retos", retos);
          
          // Renderizar la vista de retos
          return "reto";
      } catch (Exception e) {
          // Manejar cualquier error 
          System.err.println("Error al obtener retos: " + e.getMessage());
          e.printStackTrace();
          
          model.addAttribute("errorMessage", "No se pudieron cargar los retos: " + e.getMessage());
          return "reto";
      }
  }
  
  @GetMapping("/reto/{id}/detalles")
  public String mostrarDetallesReto(@PathVariable("id") Long IdReto, Model model) {
	  try {
		  System.out.println("Entra aqui"+ IdReto);

	        Reto reto = auctionServiceProxy.getDetallesDeReto(IdReto);
			  System.out.println("Entra aqui"  );
	        model.addAttribute("reto", reto);
			  System.out.println("Entra aqui2");
	        return "detalleReto";
	    } catch (RuntimeException e) {
	        model.addAttribute("errorMessage", "No se pudo encontrar el reto con ID: " + IdReto);
	        return "reto";
	    }
  }
  
  @GetMapping("/reto/{retoId}/sesiones")
  public String sesionesReto(@PathVariable("retoId") Long id, Model model) {
      try {
    	  System.out.println("id" + id);
          // Obtener las sesiones del reto específico
          List<Sesion> sesiones = auctionServiceProxy.getSesionesPorReto(id);
          System.out.println("Sesiones" + sesiones);
          // Obtener los detalles del reto para contexto adicional
          Reto reto = auctionServiceProxy.getDetallesDeReto(id);
          
          // Agregar las sesiones y los detalles del reto al modelo
          model.addAttribute("sesiones", sesiones);
          model.addAttribute("reto", reto);
          
          // Devolver la vista de sesiones
          return "sesiones"; // Asegúrate de tener un template llamado sesiones.html
      } catch (RuntimeException e) {
          // Manejo de errores
          model.addAttribute("errorMessage", "No se pudieron cargar las sesiones del reto: " + e.getMessage());
          return "reto"; // Redirigir a la página de retos en caso de error
      }
  
  }
  
  @PostMapping("/reto/{retoId}/sesiones")
  public String guardarSesion(@PathVariable("retoId") Long retoId, 
                             @ModelAttribute Sesion sesion, 
                             Model model) {
      try {
          System.out.println("Guardando nueva sesión para reto: " + retoId);
          System.out.println("Datos de la sesión: " + sesion);
          
          // Convertir las horas de String a Long (milliseconds desde epoch)
          if (sesion.horaInicio() != null) {
              String[] horaInicioParts = sesion.horaInicio().toString().split(":");
              long horaInicioMs = (Long.parseLong(horaInicioParts[0]) * 3600000) + 
                                 (Long.parseLong(horaInicioParts[1]) * 60000);
              sesion = new Sesion(
                  sesion.id(),
                  sesion.titulo(),
                  sesion.deporte(),
                  sesion.distancia(),
                  horaInicioMs,
                  sesion.horaFin(),
                  sesion.duracion()
              );
          }
          
          if (sesion.horaFin() != null) {
              String[] horaFinParts = sesion.horaFin().toString().split(":");
              long horaFinMs = (Long.parseLong(horaFinParts[0]) * 3600000) + 
                              (Long.parseLong(horaFinParts[1]) * 60000);
              sesion = new Sesion(
                  sesion.id(),
                  sesion.titulo(),
                  sesion.deporte(),
                  sesion.distancia(),
                  sesion.horaInicio(),
                  horaFinMs,
                  sesion.duracion()
              );
          }

          // Guardar la sesión usando el proxy
          auctionServiceProxy.guardarSesion(retoId, sesion, token);
          
          // Redirigir a la vista de sesiones del reto
          return "redirect:/reto/" + retoId + "/sesiones";
          
      } catch (Exception e) {
          System.err.println("Error al guardar la sesión: " + e.getMessage());
          e.printStackTrace();
          
          // En caso de error, volver a la página de sesiones con mensaje de error
          model.addAttribute("errorMessage", "Error al guardar la sesión: " + e.getMessage());
          return sesionesReto(retoId, model);
      }
  }
  
  

}