package com.saludvida.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

public class InventarioController {
	
	@Controller
	@RequestMapping("inventario")
	public class HomeController {
		
		@GetMapping
		 public String index() {
	        return "/inventario/categorias"; //ruta fisica de la carpeta
	    }
	}
}
