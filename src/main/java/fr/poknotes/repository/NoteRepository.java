package fr.poknotes.repository;

import fr.poknotes.models.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByPlayerPseudo(String pseudo);

    @Transactional
    void deleteByPlayerPseudo(String playerPseudo);
}
