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

import es.deusto.sd.auctions.client.data.Credendiales;
import es.deusto.sd.auctions.client.data.Reto;
import es.deusto.sd.auctions.client.proxies.IAuctionsServiceProxy;
import jakarta.servlet.http.HttpServletRequest;


/**
 * WebClientController class serves as the primary controller for the web client
 * application built with Spring Boot. It orchestrates the interactions between
 * the web application and the AuctionsService through the
 * RestTemplateServiceProxy, managing HTTP requests and responses while serving
 * Thymeleaf templates.
 * 
 * The use of the `@Controller` annotation in the WebClientController class
 * signifies that this class serves as a front controller in the Spring MVC
 * architecture. This annotation allows Spring to recognize and manage the class
 * as a web component, enabling it to handle HTTP requests and produce responses
 * based on user interactions.
 * 
 * Spring Boot's `@Controller` facilitates the use of model attributes through
 * the `Model` interface. The `model.addAttribute()` method is used to add
 * attributes to the model, making them accessible in the Thymeleaf templates.
 * This method takes a key-value pair, where the key is the name of the
 * attribute that can be referenced in the template, and the value is the actual
 * data to be passed. For instance, when `model.addAttribute("currentUrl",
 * currentUrl)` is called, the current URL is stored in the model with the key
 * "currentUrl", allowing it to be easily accessed in the corresponding
 * Thymeleaf view. This mechanism enables the dynamic rendering of content based
 * on the application state, ensuring that user interfaces are responsive and
 * adaptable to user interactions.
 * 
 * The methods of the controller return a `String`, which represents the name of
 * the Thymeleaf template to be rendered. This design pattern allows the
 * controller to define the appropriate view for each action. For instance, when
 * the `home` method is called, it returns the string "index", which tells
 * Spring to render the `index.html` Thymeleaf template. The mapping methods not
 * only process data but also dictate the presentation layer, facilitating a
 * clear separation between business logic and user interface concerns.
 * 
 * This class uses two distinct mappings to handle the login process, allowing
 * for a clear separation of responsibilities and improving code organization.
 * 
 * The `@GetMapping("/login")` method is responsible for displaying the login
 * page. This method prepares and returns the view containing the login form,
 * ensuring that users can easily access the interface needed to enter their
 * credentials.
 * 
 * On the other hand, the `@PostMapping("/login")` method handles the submission
 * of the form, processing user input, validating credentials, and managing the
 * authentication logic. This separation allows each method to have a single
 * responsibility, making the code easier to understand and maintain.
 * 
 * (Description generated with ChatGPT 4o mini)
 */
@Controller
public class WebClientController {
	
	@Autowired
	private IAuctionsServiceProxy auctionServiceProxy;
	
	private String token;
	
	@ModelAttribute
	public void añadirAtributos(Model model, HttpServletRequest request) {
		String currentUrl = ServletUriComponentsBuilder.fromRequestUri(request).toUriString();
		model.addAttribute("currentUrl", currentUrl); 
		model.addAttribute("token", token); 
	}
	@GetMapping("/")
	
	public String home(Model model) {
		List<Reto> retos;

		try {
			retos = auctionServiceProxy.getTodosRetos();
			model.addAttribute("reto", retos);
		} catch (RuntimeException e) {
			model.addAttribute("errorMessage", "Error al cargar Retos: " + e.getMessage());
		}

		return "index";
	}
	
	@GetMapping("/login")
	public String showLoginPage(@RequestParam(value = "redirectUrl", required = false) String redirectUrl,
			Model model) {
		// Add redirectUrl to the model if needed
		model.addAttribute("redirectUrl", redirectUrl);

		return "login"; // Return your login template
	}
	
	@PostMapping("/login")
	public String performLogin(@RequestParam("email") String email, @RequestParam("password") String password,
			@RequestParam(value = "redirectUrl", required = false) String redirectUrl, Model model) {
		Credendiales credenciales = new Credendiales(email, password);

		try {
			token = auctionServiceProxy.login(credenciales);

			// Redirect to the original page or root if redirectUrl is null
			return "redirect:" + (redirectUrl != null && !redirectUrl.isEmpty() ? redirectUrl : "/");
		} catch (RuntimeException e) {
			model.addAttribute("errorMessage", "Error al hacer Login: " + e.getMessage());
			return "login"; // Return to login page with error message
		}
	}
	
	@GetMapping("/logout")
	public String performLogout(@RequestParam(value = "redirectUrl", defaultValue = "/") String redirectUrl,
			Model model) {
		try {
			auctionServiceProxy.logout(token);
			token = null; // Clear the token after logout
			model.addAttribute("successMessage", "Logout correcto.");
		} catch (RuntimeException e) {
			model.addAttribute("errorMessage", "Error de logout: " + e.getMessage());
		}

		// Redirect to the specified URL after logout
		return "redirect:" + redirectUrl;
	}
	
	@GetMapping("/Reto/{name}")
	public String getRetosDeporte(@PathVariable("nombreDeporte") String nombreDeporte, Model model) {
		List<Reto> retos;
		
		try {
			retos = auctionServiceProxy.getRetosXDeporte(nombreDeporte);
			model.addAttribute("Retos", retos);
			model.addAttribute("Deporte", nombreDeporte);
		} catch (RuntimeException e) {
			model.addAttribute("errorMessage", "Error al cargar los Retos por cada Deporte: " + e.getMessage());
			model.addAttribute("Reto", null);
			model.addAttribute("Deporte", nombreDeporte);
			
		}
		
		return "Retos";
	}
	
	@GetMapping("/Reto/{id}")
	public String getArticleDetails(@PathVariable("id") Long id, Model model) {
		Reto reto;

		try {
			reto = auctionServiceProxy.getDetallesDeReto(id);
			model.addAttribute("Reto", reto);
		} catch (RuntimeException e) {
			model.addAttribute("errorMessage", "Error al cargar los detalles del Reto: " + e.getMessage());
			model.addAttribute("Reto", null);
		}

		return "Reto";
	}

	
	
}