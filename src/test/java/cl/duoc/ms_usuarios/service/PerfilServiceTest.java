package cl.duoc.ms_usuarios.service;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import cl.duoc.ms_usuarios.dto.ActualizarPerfilDto;
import cl.duoc.ms_usuarios.dto.AgregarJuegoDto;
import cl.duoc.ms_usuarios.dto.PerfilRespuestaDto;
import cl.duoc.ms_usuarios.model.JuegoFavorito;
import cl.duoc.ms_usuarios.model.Perfil;
import cl.duoc.ms_usuarios.repository.JuegoFavoritoRepository;
import cl.duoc.ms_usuarios.repository.PerfilRepository;

@ExtendWith(MockitoExtension.class)
public class PerfilServiceTest {

    @Mock
    private PerfilRepository perfilRepository;

    @Mock
    private JuegoFavoritoRepository juegoFavoritoRepository;

    @InjectMocks
    private PerfilService perfilService;

    private Perfil perfilEjemplo;

    @BeforeEach
    void setUp(){
        perfilEjemplo = new Perfil();
        perfilEjemplo.setId(1);
        perfilEjemplo.setUsuarioId(3);
        perfilEjemplo.setNombre("Pedro");
        perfilEjemplo.setBio("");
        perfilEjemplo.setCiudad("");
    }

    // =====================================================================
    // crearPerfil
    // =====================================================================

    @Test
    void crearPerfil_exitoso(){
        when(perfilRepository.existsByUsuarioId(3)).thenReturn(false);
        when(perfilRepository.save(any(Perfil.class))).thenReturn(perfilEjemplo);
        when(juegoFavoritoRepository.findByPerfilId(1)).thenReturn(Arrays.asList());

        PerfilRespuestaDto resultado = perfilService.crearPerfil(3, "Pedro");

        assertEquals("Pedro", resultado.getNombre());
        assertEquals(3, resultado.getUsuarioId());
    }

    @Test
    void crearPerfil_yaExiste_lanzaExcepcion(){
        when(perfilRepository.existsByUsuarioId(3)).thenReturn(true);

        RuntimeException error = assertThrows(RuntimeException.class, () ->
                perfilService.crearPerfil(3, "Pedro"));

        assertEquals("Ya existe un perfil para el usuario: 3", error.getMessage());
        verify(perfilRepository, never()).save(any());
    }

    // =====================================================================
    // obtenerPerfil
    // =====================================================================

    @Test
    void obtenerPerfil_existente(){
        when(perfilRepository.findByUsuarioId(3)).thenReturn(Optional.of(perfilEjemplo));
        when(juegoFavoritoRepository.findByPerfilId(1)).thenReturn(Arrays.asList());

        PerfilRespuestaDto resultado = perfilService.obtenerPerfil(3, "Pedro");

        assertEquals("Pedro", resultado.getNombre());
    }

    @Test
    void obtenerPerfil_noExiste_loCrea(){
        when(perfilRepository.findByUsuarioId(99)).thenReturn(Optional.empty());
        when(perfilRepository.save(any(Perfil.class))).thenReturn(perfilEjemplo);
        when(juegoFavoritoRepository.findByPerfilId(1)).thenReturn(Arrays.asList());

        PerfilRespuestaDto resultado = perfilService.obtenerPerfil(99, "Nuevo");

        assertEquals("Pedro", resultado.getNombre());
        verify(perfilRepository).save(any(Perfil.class));
    }

    // =====================================================================
    // obtenerPerfilPublico
    // =====================================================================

    @Test
    void obtenerPerfilPublico_encontrado(){
        when(perfilRepository.findByUsuarioId(3)).thenReturn(Optional.of(perfilEjemplo));
        when(juegoFavoritoRepository.findByPerfilId(1)).thenReturn(Arrays.asList());

        PerfilRespuestaDto resultado = perfilService.obtenerPerfilPublico(3);

        assertEquals(3, resultado.getUsuarioId());
    }

    @Test
    void obtenerPerfilPublico_noEncontrado(){
        when(perfilRepository.findByUsuarioId(99)).thenReturn(Optional.empty());

        RuntimeException error = assertThrows(RuntimeException.class, () ->
                perfilService.obtenerPerfilPublico(99));

        assertEquals("Perfil no encontrado para usuarioId: 99", error.getMessage());
    }

    // =====================================================================
    // actualizarPerfil
    // =====================================================================

    @Test
    void actualizarPerfil_exitoso(){
        ActualizarPerfilDto dto = new ActualizarPerfilDto();
        dto.setBio("Fan del TCG");
        dto.setCiudad("Valparaiso");

        when(perfilRepository.findByUsuarioId(3)).thenReturn(Optional.of(perfilEjemplo));
        when(perfilRepository.save(any(Perfil.class))).thenReturn(perfilEjemplo);
        when(juegoFavoritoRepository.findByPerfilId(1)).thenReturn(Arrays.asList());

        perfilService.actualizarPerfil(3, dto);

        assertEquals("Fan del TCG", perfilEjemplo.getBio());
        assertEquals("Valparaiso", perfilEjemplo.getCiudad());
    }

    @Test
    void actualizarPerfil_noEncontrado(){
        ActualizarPerfilDto dto = new ActualizarPerfilDto();
        when(perfilRepository.findByUsuarioId(99)).thenReturn(Optional.empty());

        RuntimeException error = assertThrows(RuntimeException.class, () ->
                perfilService.actualizarPerfil(99, dto));

        assertEquals("Perfil no encontrado. Crea tu perfil primero.", error.getMessage());
    }

    // =====================================================================
    // agregarJuego
    // =====================================================================

    @Test
    void agregarJuego_exitoso(){
        AgregarJuegoDto dto = new AgregarJuegoDto();
        dto.setNombreJuego("Magic: The Gathering");

        when(perfilRepository.findByUsuarioId(3)).thenReturn(Optional.of(perfilEjemplo));
        when(juegoFavoritoRepository.findByPerfilId(1)).thenReturn(
                Arrays.asList(new JuegoFavorito(1, perfilEjemplo, "Magic: The Gathering")));

        PerfilRespuestaDto resultado = perfilService.agregarJuego(3, dto);

        assertEquals(1, resultado.getJuegosFavoritos().size());
        verify(juegoFavoritoRepository).save(any(JuegoFavorito.class));
    }

    @Test
    void agregarJuego_perfilNoEncontrado(){
        AgregarJuegoDto dto = new AgregarJuegoDto();
        dto.setNombreJuego("Magic: The Gathering");

        when(perfilRepository.findByUsuarioId(99)).thenReturn(Optional.empty());

        RuntimeException error = assertThrows(RuntimeException.class, () ->
                perfilService.agregarJuego(99, dto));

        assertEquals("Perfil no encontrado.", error.getMessage());
    }

    // =====================================================================
    // eliminarJuego
    // =====================================================================

    @Test
    void eliminarJuego_exitoso(){
        JuegoFavorito juego = new JuegoFavorito(5, perfilEjemplo, "Magic: The Gathering");

        when(perfilRepository.findByUsuarioId(3)).thenReturn(Optional.of(perfilEjemplo));
        when(juegoFavoritoRepository.findById(5)).thenReturn(Optional.of(juego));
        when(juegoFavoritoRepository.findByPerfilId(1)).thenReturn(Arrays.asList());

        perfilService.eliminarJuego(3, 5);

        verify(juegoFavoritoRepository).delete(juego);
    }

    @Test
    void eliminarJuego_perfilNoEncontrado(){
        when(perfilRepository.findByUsuarioId(99)).thenReturn(Optional.empty());

        RuntimeException error = assertThrows(RuntimeException.class, () ->
                perfilService.eliminarJuego(99, 5));

        assertEquals("Perfil no encontrado.", error.getMessage());
    }

    @Test
    void eliminarJuego_juegoNoEncontrado(){
        when(perfilRepository.findByUsuarioId(3)).thenReturn(Optional.of(perfilEjemplo));
        when(juegoFavoritoRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException error = assertThrows(RuntimeException.class, () ->
                perfilService.eliminarJuego(3, 99));

        assertEquals("Juego favorito no encontrado: 99", error.getMessage());
    }

    @Test
    void eliminarJuego_noPerteneceAlUsuario(){
        Perfil otroPerfil = new Perfil();
        otroPerfil.setId(999);
        otroPerfil.setUsuarioId(7);

        JuegoFavorito juegoDeOtro = new JuegoFavorito(5, otroPerfil, "Pokemon TCG");

        when(perfilRepository.findByUsuarioId(3)).thenReturn(Optional.of(perfilEjemplo));
        when(juegoFavoritoRepository.findById(5)).thenReturn(Optional.of(juegoDeOtro));

        RuntimeException error = assertThrows(RuntimeException.class, () ->
                perfilService.eliminarJuego(3, 5));

        assertEquals("No puedes eliminar un juego que no es tuyo.", error.getMessage());
    }
}
