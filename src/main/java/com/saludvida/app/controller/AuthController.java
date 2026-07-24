package com.saludvida.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.saludvida.app.model.dto.request.LoginRequestDTO;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AuthController {

    private static final String CORREO_ADMIN = "admin@saludvida.com";
    private static final String PASSWORD_ADMIN = "admin123";

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

        boolean credencialesValidas = CORREO_ADMIN.equalsIgnoreCase(login.getCorreo())
                && PASSWORD_ADMIN.equals(login.getPassword());

        if (!credencialesValidas) {
            model.addAttribute("error", "Correo o contraseña incorrectos");
            return "auth/login";
        }

        session.setAttribute("usuarioSesion", login.getCorreo());
        session.setAttribute("nombreUsuario", "Administrador SaludVida");

        return "redirect:/home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }
}
