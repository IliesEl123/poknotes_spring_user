package fr.poknotes.controllers;

import fr.poknotes.exception.ResourceNotFoundException;
import fr.poknotes.models.Player;
import fr.poknotes.repository.NoteRepository;
import fr.poknotes.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

// Annotation pour permettre l'accès à ce contrôleur depuis n'importe quelle origine
@CrossOrigin(origins = "*", maxAge = 3600)
// Annotation pour indiquer que ce contrôleur gère les requêtes HTTP vers "/api"
@RestController
@RequestMapping("/api")
public class PlayerController {

    // Injection de dépendances pour la gestion des joueurs
    @Autowired
    PlayerRepository playerRepository;

    // Injection de dépendances pour la gestion des notes
    @Autowired
    NoteRepository noteRepository;


    // Méthode pour récupérer tous les joueurs
    @GetMapping("/players")
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    // Méthode pour récupérer un joueur en fonction de son pseudo
    @GetMapping("/players/{pseudo}")
    public ResponseEntity<Player> getPlayerByPseudo(@PathVariable(value = "pseudo") String playerPseudo)
            throws ResourceNotFoundException {
        // Recherche du joueur dans la base de données en utilisant son pseudo
        Player player = playerRepository.findByPseudo(playerPseudo)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found for this pseudo :: " + playerPseudo));
        // Envoi d'une réponse contenant le joueur trouvé
        return ResponseEntity.ok().body(player);
    }

    // Méthode pour rechercher des joueurs en fonction d'un pseudo partiel
    @GetMapping("/players/search/{pseudo}")
    public List<Player> getPlayersByPseudoContaining(@PathVariable(value = "pseudo") String playerPseudo){
        // Recherche des joueurs dans la base de données en utilisant un pseudo partiel
        return playerRepository.findByPseudoContainingIgnoreCase(playerPseudo);
    }

    // Méthode pour créer un nouveau joueur
    @PostMapping("/players")
    public Player createPlayer(@Valid @RequestBody Player player) {
        // Enregistrement du nouveau joueur dans la base de données
        return playerRepository.save(player);
    }

    // Méthode pour mettre à jour un joueur existant en fonction de son pseudo
    @PutMapping("/players/{pseudo}")
    public ResponseEntity<Player> updatePlayer(@PathVariable(value = "pseudo") String playerPseudo,
                                               @Valid @RequestBody Player playerDetails) throws ResourceNotFoundException {
        // Recherche du joueur existant dans la base de données en utilisant son pseudo
        Player player = playerRepository.findByPseudo(playerPseudo)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found for this pseudo :: " + playerPseudo));

        // Mise à jour des informations du joueur existant
        player.setPseudo(playerDetails.getPseudo());
        // Enregistrement des informations mises à jour dans la base de données
        final Player updatedPlayer = playerRepository.save(player);
        // Envoi d'une réponse contenant le joueur mis à jour
        return ResponseEntity.ok(updatedPlayer);
    }

    // Méthode pour supprimer un joueur existant en fonction de son pseudo
    @DeleteMapping("/players/{pseudo}")
    public Map<String, Boolean> deletePlayer(@PathVariable(value = "pseudo") String playerPseudo)
            throws ResourceNotFoundException {
        // Recherche du joueur existant dans la base de données en utilisant son pseudo
        Player player = playerRepository.findByPseudo(playerPseudo)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found for this pseudo :: " + playerPseudo
                ));

        // Suppression des notes du joueur dans la base de données
        noteRepository.deleteByPlayerPseudo(playerPseudo);

        // Suppression du joueur dans la base de données
        playerRepository.delete(player);

        // Envoi d'une réponse indiquant que la suppression a bien été effectuée
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }
}