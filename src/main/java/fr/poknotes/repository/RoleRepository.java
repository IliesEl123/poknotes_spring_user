package fr.poknotes.repository;

import fr.poknotes.models.ERole;
import fr.poknotes.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository  extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}
