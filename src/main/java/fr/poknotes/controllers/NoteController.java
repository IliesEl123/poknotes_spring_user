package fr.poknotes.controllers;

import fr.poknotes.exception.ResourceNotFoundException;
import fr.poknotes.models.Note;
import fr.poknotes.repository.NoteRepository;
import fr.poknotes.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Annotation pour permettre l'accès à ce contrôleur depuis n'importe quelle origine
@CrossOrigin(origins = "*", maxAge = 3600)
// Annotation pour indiquer que ce contrôleur gère les requêtes HTTP vers "/api"
@RestController
@RequestMapping("/api")
public class NoteController {

    // Injection de dépendances pour la gestion des joueurs
    @Autowired
    private PlayerRepository playerRepository;

    // Injection de dépendances pour la gestion des notes
    @Autowired
    private NoteRepository noteRepository;

    // Méthode pour récupérer toutes les notes associées à un joueur en fonction de son pseudo
    @GetMapping("/players/{playerPseudo}/notes")
    public ResponseEntity<List<Note>> getAllNotesByPlayerPseudo(@PathVariable(value = "playerPseudo") String playerPseudo) throws ResourceNotFoundException {
        // Vérification si le joueur existe dans la base de données
        if (!playerRepository.existsByPseudo(playerPseudo)) {
            throw new ResourceNotFoundException("Not found Player with pseudo = " + playerPseudo);
        }

        // Récupération de toutes les notes associées au joueur
        List<Note> notes = noteRepository.findByPlayerPseudo(playerPseudo);
        // Envoi d'une réponse contenant toutes les notes associées au joueur
        return new ResponseEntity<>(notes, HttpStatus.OK);
    }

    // Méthode pour créer une nouvelle note pour un joueur en fonction de son pseudo
    @PostMapping("/players/{playerPseudo}/notes")
    public ResponseEntity<Note> createNote(@PathVariable(value = "playerPseudo") String playerPseudo,
                                           @RequestBody Note noteRequest) throws ResourceNotFoundException {
        // Vérification si le joueur existe dans la base de données
        Note note = playerRepository.findByPseudo(playerPseudo).map(player -> {
            noteRequest.setPlayer(player);
            return noteRepository.save(noteRequest);
        }).orElseThrow(() -> new ResourceNotFoundException("Not found Player with pseudo = " + playerPseudo));

        // Création d'une nouvelle note associée au joueur et enregistrement dans la base de données
        return new ResponseEntity<>(note, HttpStatus.CREATED);
    }

    // Méthode pour mettre à jour une note existante en fonction de son id
    @PutMapping("/notes/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable("id") long id, @RequestBody Note noteRequest) throws ResourceNotFoundException {
        // Récupération de la note existante
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("NoteId " + id + "not found"));

        // Mise à jour du contenu de la note
        note.setContent(noteRequest.getContent());

        // Enregistrement de la note mise à jour dans la base de données
        return new ResponseEntity<>(noteRepository.save(note), HttpStatus.OK);
    }

    // Méthode pour supprimer une note existante en fonction de son id
    @DeleteMapping("/notes/{id}")
    public ResponseEntity<HttpStatus> deleteNote(@PathVariable("id") long id) {
        // Suppression de la note existante dans la base de données
        noteRepository.deleteById(id);

        // Envoi d'une réponse pour confirmer la suppression de la note
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
}
