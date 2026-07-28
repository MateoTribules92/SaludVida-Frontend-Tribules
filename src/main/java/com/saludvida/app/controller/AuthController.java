package com.saludvida.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.saludvida.app.model.dto.request.LoginRequestDTO;
import com.saludvida.app.model.dto.response.LoginResponseDTO;
import com.saludvida.app.services.IAuthService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AuthController {

    private final IAuthService authService;

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login(Model model, HttpSession session) {
        if (session != null && session.getAttribute("usuarioSesion") != null) {
            return "redirect:/home";
        }

        model.addAttribute("login", new LoginRequestDTO());
        return "auth/login";
    }

    @PostMapping("/login")
    public String autenticar(
            @Valid @ModelAttribute("login") LoginRequestDTO login,
            BindingResult result,
            HttpSession session,
            Model model) {

        if (result.hasErrors()) {
            return "auth/login";
        }

        var usuarioAutenticado = authService.login(login);

        if (usuarioAutenticado.isEmpty()) {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "auth/login";
        }

        LoginResponseDTO usuario = usuarioAutenticado.get();
        session.setAttribute("usuarioSesion", usuario.getCorreo());
        session.setAttribute("idUsuario", usuario.getIdUsuario());
        session.setAttribute("idRol", usuario.getIdRol());
        session.setAttribute("idFarmacia", usuario.getIdFarmacia());
        session.setAttribute("nombreUsuario", usuario.getNombres());
        session.setAttribute("rolUsuario", usuario.getCodigoRol());
        session.setAttribute("nombreRol", usuario.getNombreRol());

        return "redirect:/home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }
}
