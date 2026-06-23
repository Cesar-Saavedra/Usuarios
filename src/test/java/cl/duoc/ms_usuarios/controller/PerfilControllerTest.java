package cl.duoc.ms_usuarios.controller;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.duoc.ms_usuarios.dto.ActualizarPerfilDto;
import cl.duoc.ms_usuarios.dto.AgregarJuegoDto;
import cl.duoc.ms_usuarios.dto.PerfilRespuestaDto;
import cl.duoc.ms_usuarios.security.JwtUtil;
import cl.duoc.ms_usuarios.service.PerfilService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(PerfilController.class)
public class PerfilControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PerfilService perfilService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private PerfilRespuestaDto perfilEjemplo;

    @BeforeEach
    void setUp(){
        perfilEjemplo = new PerfilRespuestaDto(1, 3, "Pedro", "Bio", "Santiago", Collections.emptyList());
    }

    // =====================================================================
    // GET /api/perfil/mio
    // =====================================================================

    @Test
    void verMiPerfil_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/perfil/mio"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void verMiPerfil_retorna200() throws Exception {
        when(jwtUtil.obtenerTokenDelHeader("Bearer token-bueno")).thenReturn("token-bueno");
        when(jwtUtil.esTokenValido("token-bueno")).thenReturn(true);
        when(jwtUtil.extraerId("token-bueno")).thenReturn(3);
        when(jwtUtil.extraerNombre("token-bueno")).thenReturn("Pedro");
        when(perfilService.obtenerPerfil(3, "Pedro")).thenReturn(perfilEjemplo);

        mockMvc.perform(get("/api/perfil/mio").header("Authorization", "Bearer token-bueno"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Pedro"));
    }

    // =====================================================================
    // PUT /api/perfil/mio
    // =====================================================================

    @Test
    void actualizarMiPerfil_sinToken_retorna401() throws Exception {
        ActualizarPerfilDto dto = new ActualizarPerfilDto();
        dto.setBio("Nueva bio");

        mockMvc.perform(put("/api/perfil/mio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void actualizarMiPerfil_exitoso_retorna200() throws Exception {
        ActualizarPerfilDto dto = new ActualizarPerfilDto();
        dto.setBio("Nueva bio");

        when(jwtUtil.obtenerTokenDelHeader("Bearer token-bueno")).thenReturn("token-bueno");
        when(jwtUtil.esTokenValido("token-bueno")).thenReturn(true);
        when(jwtUtil.extraerId("token-bueno")).thenReturn(3);
        when(perfilService.actualizarPerfil(3, dto)).thenReturn(perfilEjemplo);

        mockMvc.perform(put("/api/perfil/mio")
                        .header("Authorization", "Bearer token-bueno")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void actualizarMiPerfil_errorDeNegocio_retorna400() throws Exception {
        ActualizarPerfilDto dto = new ActualizarPerfilDto();
        dto.setBio("Nueva bio");

        when(jwtUtil.obtenerTokenDelHeader("Bearer token-bueno")).thenReturn("token-bueno");
        when(jwtUtil.esTokenValido("token-bueno")).thenReturn(true);
        when(jwtUtil.extraerId("token-bueno")).thenReturn(99);
        when(perfilService.actualizarPerfil(99, dto))
                .thenThrow(new RuntimeException("Perfil no encontrado. Crea tu perfil primero."));

        mockMvc.perform(put("/api/perfil/mio")
                        .header("Authorization", "Bearer token-bueno")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // =====================================================================
    // GET /api/perfil/{usuarioId}
    // =====================================================================

    @Test
    void verPerfilPublico_sinToken_retorna401() throws Exception {
        mockMvc.perform(get("/api/perfil/3"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void verPerfilPublico_encontrado_retorna200() throws Exception {
        when(jwtUtil.obtenerTokenDelHeader("Bearer token-bueno")).thenReturn("token-bueno");
        when(jwtUtil.esTokenValido("token-bueno")).thenReturn(true);
        when(perfilService.obtenerPerfilPublico(3)).thenReturn(perfilEjemplo);

        mockMvc.perform(get("/api/perfil/3").header("Authorization", "Bearer token-bueno"))
                .andExpect(status().isOk());
    }

    @Test
    void verPerfilPublico_noEncontrado_retorna404() throws Exception {
        when(jwtUtil.obtenerTokenDelHeader("Bearer token-bueno")).thenReturn("token-bueno");
        when(jwtUtil.esTokenValido("token-bueno")).thenReturn(true);
        when(perfilService.obtenerPerfilPublico(99))
                .thenThrow(new RuntimeException("Perfil no encontrado para usuarioId: 99"));

        mockMvc.perform(get("/api/perfil/99").header("Authorization", "Bearer token-bueno"))
                .andExpect(status().isNotFound());
    }

    // =====================================================================
    // POST /api/perfil/mio/juegos
    // =====================================================================

    @Test
    void agregarJuego_sinToken_retorna401() throws Exception {
        AgregarJuegoDto dto = new AgregarJuegoDto();
        dto.setNombreJuego("Magic: The Gathering");

        mockMvc.perform(post("/api/perfil/mio/juegos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void agregarJuego_exitoso_retorna201() throws Exception {
        AgregarJuegoDto dto = new AgregarJuegoDto();
        dto.setNombreJuego("Magic: The Gathering");

        PerfilRespuestaDto conJuego = new PerfilRespuestaDto(1, 3, "Pedro", "Bio", "Santiago",
                Arrays.asList("Magic: The Gathering"));

        when(jwtUtil.obtenerTokenDelHeader("Bearer token-bueno")).thenReturn("token-bueno");
        when(jwtUtil.esTokenValido("token-bueno")).thenReturn(true);
        when(jwtUtil.extraerId("token-bueno")).thenReturn(3);
        when(perfilService.agregarJuego(3, dto)).thenReturn(conJuego);

        mockMvc.perform(post("/api/perfil/mio/juegos")
                        .header("Authorization", "Bearer token-bueno")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.juegosFavoritos[0]").value("Magic: The Gathering"));
    }

    // =====================================================================
    // DELETE /api/perfil/mio/juegos/{juegoId}
    // =====================================================================

    @Test
    void eliminarJuego_sinToken_retorna401() throws Exception {
        mockMvc.perform(delete("/api/perfil/mio/juegos/5"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void eliminarJuego_exitoso_retorna200() throws Exception {
        when(jwtUtil.obtenerTokenDelHeader("Bearer token-bueno")).thenReturn("token-bueno");
        when(jwtUtil.esTokenValido("token-bueno")).thenReturn(true);
        when(jwtUtil.extraerId("token-bueno")).thenReturn(3);
        when(perfilService.eliminarJuego(3, 5)).thenReturn(perfilEjemplo);

        mockMvc.perform(delete("/api/perfil/mio/juegos/5").header("Authorization", "Bearer token-bueno"))
                .andExpect(status().isOk());
    }

    @Test
    void eliminarJuego_noEsTuyo_retorna400() throws Exception {
        when(jwtUtil.obtenerTokenDelHeader("Bearer token-bueno")).thenReturn("token-bueno");
        when(jwtUtil.esTokenValido("token-bueno")).thenReturn(true);
        when(jwtUtil.extraerId("token-bueno")).thenReturn(3);
        when(perfilService.eliminarJuego(3, 5))
                .thenThrow(new RuntimeException("No puedes eliminar un juego que no es tuyo."));

        mockMvc.perform(delete("/api/perfil/mio/juegos/5").header("Authorization", "Bearer token-bueno"))
                .andExpect(status().isBadRequest());
    }
}
