package cl.duoc.ms_usuarios.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.duoc.ms_usuarios.dto.ActualizarPerfilDto;
import cl.duoc.ms_usuarios.dto.AgregarJuegoDto;
import cl.duoc.ms_usuarios.dto.PerfilRespuestaDto;
import cl.duoc.ms_usuarios.model.JuegoFavorito;
import cl.duoc.ms_usuarios.model.Perfil;
import cl.duoc.ms_usuarios.repository.JuegoFavoritoRepository;
import cl.duoc.ms_usuarios.repository.PerfilRepository;

@Service
public class PerfilService {

    @Autowired
    private PerfilRepository perfilRepository;

    @Autowired
    private JuegoFavoritoRepository juegoFavoritoRepository;

    // =========================================================
    // CREAR PERFIL
    // =========================================================

    /*
     * Crea un perfil nuevo para un usuario que acaba de registrarse.
     *
     * Esto se llama la primera vez que el usuario entra a ms-usuarios.
     * Si el perfil ya existe, lanza excepcion.
     *
     * @param usuarioId  el id del usuario en ms-login (viene del JWT)
     * @param nombre     el nombre inicial (viene del JWT tambien)
     */
    public PerfilRespuestaDto crearPerfil(Integer usuarioId, String nombre) {

        // Verificar que no exista un perfil para este usuarioId
        if (perfilRepository.existsByUsuarioId(usuarioId)) {
            throw new RuntimeException("Ya existe un perfil para el usuario: " + usuarioId);
        }

        // Crear el perfil con datos minimos
        Perfil perfil = new Perfil();
        perfil.setUsuarioId(usuarioId);
        perfil.setNombre(nombre);
        perfil.setBio("");
        perfil.setCiudad("");

        Perfil guardado = perfilRepository.save(perfil);

        // Devolver el perfil recien creado (sin juegos favoritos aun)
        return construirRespuesta(guardado);
    }

    // =========================================================
    // VER PERFIL
    // =========================================================

    /*
     * Devuelve el perfil de un usuario, buscando por su usuarioId.
     * Si no existe el perfil, lo crea automaticamente con datos basicos.
     *
     * @param usuarioId  id del usuario en ms-login
     * @param nombre     nombre que viene del JWT (por si hay que crear el perfil)
     */
    public PerfilRespuestaDto obtenerPerfil(Integer usuarioId, String nombre) {

        // Buscar el perfil. Si no existe, crearlo automaticamente.
        Perfil perfil = perfilRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    // El perfil no existe, lo creamos con datos del JWT
                    Perfil nuevo = new Perfil();
                    nuevo.setUsuarioId(usuarioId);
                    nuevo.setNombre(nombre);
                    nuevo.setBio("");
                    nuevo.setCiudad("");
                    return perfilRepository.save(nuevo);
                });

        return construirRespuesta(perfil);
    }

    /*
     * Devuelve el perfil de cualquier usuario por su usuarioId.
     * Se usa para ver el perfil publico de otra persona.
     */
    public PerfilRespuestaDto obtenerPerfilPublico(Integer usuarioId) {
        Perfil perfil = perfilRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado para usuarioId: " + usuarioId));
        return construirRespuesta(perfil);
    }

    // =========================================================
    // ACTUALIZAR PERFIL
    // =========================================================

    /*
     * Actualiza los datos del perfil del usuario autenticado.
     *
     * Solo el propio usuario puede editar su perfil
     * (el controller verifica que el usuarioId del JWT coincida).
     */
    public PerfilRespuestaDto actualizarPerfil(Integer usuarioId, ActualizarPerfilDto dto) {

        Perfil perfil = perfilRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado. Crea tu perfil primero."));

        // Solo actualizamos los campos que no sean nulos en el DTO
        if (dto.getNombre() != null && !dto.getNombre().isBlank()) {
            perfil.setNombre(dto.getNombre());
        }
        if (dto.getBio() != null) {
            perfil.setBio(dto.getBio());
        }
        if (dto.getCiudad() != null) {
            perfil.setCiudad(dto.getCiudad());
        }

        Perfil actualizado = perfilRepository.save(perfil);
        return construirRespuesta(actualizado);
    }

    // =========================================================
    // JUEGOS FAVORITOS
    // =========================================================

    /*
     * Agrega un juego a la lista de favoritos del usuario.
     */
    public PerfilRespuestaDto agregarJuego(Integer usuarioId, AgregarJuegoDto dto) {

        Perfil perfil = perfilRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado."));

        JuegoFavorito juego = new JuegoFavorito();
        juego.setPerfil(perfil);
        juego.setNombreJuego(dto.getNombreJuego());
        juegoFavoritoRepository.save(juego);

        // Devolver el perfil actualizado con el nuevo juego
        return construirRespuesta(perfil);
    }

    /*
     * Elimina un juego favorito de la lista.
     * Verifica que el juego pertenezca al perfil del usuario autenticado.
     */
    public PerfilRespuestaDto eliminarJuego(Integer usuarioId, Integer juegoId) {

        Perfil perfil = perfilRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado."));

        JuegoFavorito juego = juegoFavoritoRepository.findById(juegoId)
                .orElseThrow(() -> new RuntimeException("Juego favorito no encontrado: " + juegoId));

        // Verificar que el juego pertenezca al perfil del usuario autenticado
        if (!juego.getPerfil().getId().equals(perfil.getId())) {
            throw new RuntimeException("No puedes eliminar un juego que no es tuyo.");
        }

        juegoFavoritoRepository.delete(juego);
        return construirRespuesta(perfil);
    }

    // =========================================================
    // METODO AUXILIAR PRIVADO
    // =========================================================

    /*
     * Construye el DTO de respuesta a partir de una entidad Perfil.
     * Trae los juegos favoritos de la BD y los convierte a una lista de Strings.
     */
    private PerfilRespuestaDto construirRespuesta(Perfil perfil) {

        // Traer los juegos favoritos del perfil
        List<JuegoFavorito> juegos = juegoFavoritoRepository.findByPerfilId(perfil.getId());

        // Convertir la lista de objetos JuegoFavorito a lista de Strings
        List<String> nombresJuegos = juegos.stream()
                .map(JuegoFavorito::getNombreJuego)
                .toList();

        return new PerfilRespuestaDto(
                perfil.getId(),
                perfil.getUsuarioId(),
                perfil.getNombre(),
                perfil.getBio(),
                perfil.getCiudad(),
                nombresJuegos
        );
    }
}
