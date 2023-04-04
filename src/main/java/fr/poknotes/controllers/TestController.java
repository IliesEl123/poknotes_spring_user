package fr.poknotes.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Annotation pour permettre l'accès à ce contrôleur depuis n'importe quelle origine
@CrossOrigin(origins = "*", maxAge = 3600)
// Annotation pour indiquer que ce contrôleur gère les requêtes HTTP vers "/api/test"
@RestController
@RequestMapping("/api/test")
public class TestController {

    // Méthode accessible par tout le monde
    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    // Méthode accessible seulement par les utilisateurs ayant le rôle USER ou ADMIN
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String userAccess() {
        return "User Content.";
    }

    // Méthode accessible seulement par les utilisateurs ayant le rôle ADMIN
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }
}
