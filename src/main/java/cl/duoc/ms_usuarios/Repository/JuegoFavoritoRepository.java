package cl.duoc.ms_usuarios.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.ms_usuarios.Model.JuegoFavorito;


@Repository
public interface JuegoFavoritoRepository extends JpaRepository<JuegoFavorito, Integer> {

    // Trae todos los juegos favoritos de un perfil
    List<JuegoFavorito> findByPerfilId(Integer perfilId);

    // Elimina todos los juegos favoritos de un perfil
    // (util si se elimina el perfil)
    void deleteByPerfilId(Integer perfilId);

}
