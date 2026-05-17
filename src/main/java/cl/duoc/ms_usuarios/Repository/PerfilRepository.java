package cl.duoc.ms_usuarios.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.ms_usuarios.model.Perfil;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Integer> {

    // Busca un perfil por el id del usuario que viene del ms-login
    Optional<Perfil> findByUsuarioId(Integer usuarioId);

    // Verifica si ya existe un perfil para ese usuarioId
    boolean existsByUsuarioId(Integer usuarioId);

}
