package fr.poknotes.repository;

import fr.poknotes.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    Optional<Player> findByPseudo(String pseudo);


    List<Player> findByPseudoContainingIgnoreCase(String pseudo);

    boolean existsByPseudo(String playerPseudo);
}
