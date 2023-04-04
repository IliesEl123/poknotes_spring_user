package fr.poknotes.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;

import fr.poknotes.models.ERole;
import fr.poknotes.models.Role;
import fr.poknotes.models.User;
import fr.poknotes.payload.request.LoginRequest;
import fr.poknotes.payload.request.SignupRequest;
import fr.poknotes.payload.response.JwtResponse;
import fr.poknotes.payload.response.MessageResponse;
import fr.poknotes.repository.RoleRepository;
import fr.poknotes.repository.UserRepository;
import fr.poknotes.security.jwt.JwtUtils;
import fr.poknotes.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Annotation pour permettre l'accès à ce contrôleur depuis n'importe quelle origine
@CrossOrigin(origins = "*", maxAge = 3600)
// Annotation pour indiquer que ce contrôleur gère les requêtes HTTP vers "/api/auth"
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Injection de dépendances pour l'authentification
    @Autowired
    AuthenticationManager authenticationManager;
    // Injection de dépendances pour la gestion des utilisateurs
    @Autowired
    UserRepository userRepository;
    // Injection de dépendances pour la gestion des rôles
    @Autowired
    RoleRepository roleRepository;
    // Injection de dépendances pour l'encodage des mots de passe
    @Autowired
    PasswordEncoder encoder;
    // Injection de dépendances pour la génération de jetons JWT
    @Autowired
    JwtUtils jwtUtils;

    // Méthode pour la connexion d'un utilisateur
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // Validation des informations d'identification de l'utilisateur
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        // Enregistrement de l'authentification dans le contexte de sécurité
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // Génération d'un jeton JWT pour l'utilisateur authentifié
        String jwt = jwtUtils.generateJwtToken(authentication);

        // Obtention des détails de l'utilisateur authentifié
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        // Obtention des rôles de l'utilisateur authentifié
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        // Envoi d'une réponse contenant le jeton JWT et les détails de l'utilisateur authentifié
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    // Méthode pour l'enregistrement d'un nouvel utilisateur
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        // Vérification si l'username fourni est déjà utilisé par un autre utilisateur
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }
        // Vérification si l'email fourni est déjà utilisé par un autre utilisateur
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Création d'un nouvel utilisateur avec les informations de la demande d'inscription
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));
        // Attribution d'un ou plusieurs rôles à l'utilisateur
        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();
        if (strRoles == null) {
            // Si aucun rôle n'est fourni, l'utilisateur est enregistré avec le rôle "ROLE_USER"
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            // Si un ou plusieurs rôles sont fournis, l'utilisateur est enregistré avec ces rôles
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        // Enregistrement du nouvel utilisateur dans la base de données
        userRepository.save(user);
        // Envoi d'une réponse pour confirmer l'enregistrement de l'utilisateur
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}