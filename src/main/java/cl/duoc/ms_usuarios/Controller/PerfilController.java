package cl.duoc.ms_usuarios.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.ms_usuarios.Dto.ActualizarPerfilDto;
import cl.duoc.ms_usuarios.Dto.AgregarJuegoDto;
import cl.duoc.ms_usuarios.Dto.PerfilRespuestaDto;
import cl.duoc.ms_usuarios.Service.PerfilService;
import cl.duoc.ms_usuarios.Security.JwtUtil;

/*
 * Controlador REST de ms-usuarios.
 * Puerto: 8082
 * Base URL: http://localhost:8082/api/perfil
 *
 * TODOS los endpoints requieren el header:
 *   Authorization: Bearer {token}
 * donde {token} es el JWT que devolvio ms-login al hacer login.
 *
 * Endpoints:
 * ==========
 * GET    /api/perfil/mio             -> Ver mi propio perfil
 * PUT    /api/perfil/mio             -> Actualizar mi perfil
 * GET    /api/perfil/{usuarioId}     -> Ver el perfil publico de otro usuario
 * POST   /api/perfil/mio/juegos      -> Agregar un juego favorito
 * DELETE /api/perfil/mio/juegos/{id} -> Eliminar un juego favorito
 */
@RestController
@RequestMapping("/api/perfil")
public class PerfilController {

    @Autowired
    private PerfilService perfilService;

    @Autowired
    private JwtUtil jwtUtil;

    // =========================================================
    // GET /api/perfil/mio
    // Ver mi propio perfil
    // =========================================================
    /*
     * Header: Authorization: Bearer {token}
     *
     * Respuesta 200:
     * {
     *   "id": 1,
     *   "usuarioId": 3,
     *   "nombre": "Pedro",
     *   "bio": "",
     *   "ciudad": "",
     *   "juegosFavoritos": []
     * }
     */
    @GetMapping("/mio")
    public ResponseEntity<?> verMiPerfil(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        // 1. Validar el token
        String token = validarHeader(authHeader);
        if (token == null) {
            return respuestaNoAutorizado("Token requerido. Usa: Authorization: Bearer {token}");
        }

        // 2. Extraer datos del usuario desde el token
        Integer usuarioId = jwtUtil.extraerId(token);
        String nombre = jwtUtil.extraerNombre(token);

        // 3. Obtener (o crear) el perfil
        try {
            PerfilRespuestaDto perfil = perfilService.obtenerPerfil(usuarioId, nombre);
            return ResponseEntity.ok(perfil);
        } catch (RuntimeException e) {
            return respuestaError(e.getMessage());
        }
    }

    // =========================================================
    // PUT /api/perfil/mio
    // Actualizar mi perfil
    // =========================================================
    /*
     * Header: Authorization: Bearer {token}
     * Body JSON:
     * {
     *   "nombre": "Pedro Gamer",
     *   "bio": "Fan del TCG",
     *   "ciudad": "Santiago"
     * }
     *
     * Puedes enviar solo los campos que quieres cambiar.
     */
    @PutMapping("/mio")
    public ResponseEntity<?> actualizarMiPerfil(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody ActualizarPerfilDto dto) {

        String token = validarHeader(authHeader);
        if (token == null) {
            return respuestaNoAutorizado("Token requerido.");
        }

        Integer usuarioId = jwtUtil.extraerId(token);

        try {
            PerfilRespuestaDto actualizado = perfilService.actualizarPerfil(usuarioId, dto);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return respuestaError(e.getMessage());
        }
    }

    // =========================================================
    // GET /api/perfil/{usuarioId}
    // Ver el perfil publico de cualquier usuario
    // =========================================================
    /*
     * Header: Authorization: Bearer {token}
     * Path param: usuarioId = el id del usuario en ms-login
     *
     * Util para mostrar el perfil de otros jugadores.
     */
    @GetMapping("/{usuarioId}")
    public ResponseEntity<?> verPerfilPublico(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Integer usuarioId) {

        String token = validarHeader(authHeader);
        if (token == null) {
            return respuestaNoAutorizado("Token requerido.");
        }

        try {
            PerfilRespuestaDto perfil = perfilService.obtenerPerfilPublico(usuarioId);
            return ResponseEntity.ok(perfil);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // =========================================================
    // POST /api/perfil/mio/juegos
    // Agregar un juego favorito
    // =========================================================
    /*
     * Header: Authorization: Bearer {token}
     * Body JSON:
     * {
     *   "nombreJuego": "Magic: The Gathering"
     * }
     *
     * Respuesta 201: el perfil actualizado con el nuevo juego en la lista.
     */
    @PostMapping("/mio/juegos")
    public ResponseEntity<?> agregarJuego(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody AgregarJuegoDto dto) {

        String token = validarHeader(authHeader);
        if (token == null) {
            return respuestaNoAutorizado("Token requerido.");
        }

        // Validar que el nombre del juego no este vacio
        if (dto.getNombreJuego() == null || dto.getNombreJuego().isBlank()) {
            return respuestaError("El nombre del juego no puede estar vacío.");
        }

        Integer usuarioId = jwtUtil.extraerId(token);

        try {
            PerfilRespuestaDto perfil = perfilService.agregarJuego(usuarioId, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(perfil);
        } catch (RuntimeException e) {
            return respuestaError(e.getMessage());
        }
    }

    // =========================================================
    // DELETE /api/perfil/mio/juegos/{juegoId}
    // Eliminar un juego favorito
    // =========================================================
    /*
     * Header: Authorization: Bearer {token}
     * Path param: juegoId = el id del juego favorito a eliminar
     *
     * Solo puedes eliminar juegos de TU propio perfil.
     * Respuesta 200: el perfil actualizado sin ese juego.
     */
    @DeleteMapping("/mio/juegos/{juegoId}")
    public ResponseEntity<?> eliminarJuego(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @PathVariable Integer juegoId) {

        String token = validarHeader(authHeader);
        if (token == null) {
            return respuestaNoAutorizado("Token requerido.");
        }

        Integer usuarioId = jwtUtil.extraerId(token);

        try {
            PerfilRespuestaDto perfil = perfilService.eliminarJuego(usuarioId, juegoId);
            return ResponseEntity.ok(perfil);
        } catch (RuntimeException e) {
            return respuestaError(e.getMessage());
        }
    }

    // =========================================================
    // METODOS PRIVADOS DE AYUDA
    // =========================================================

    /*
     * Saca el token del header y verifica que sea valido.
     * Devuelve null si el header es invalido o el token esta vencido.
     */
    private String validarHeader(String authHeader) {
        String token = jwtUtil.obtenerTokenDelHeader(authHeader);
        if (token == null || !jwtUtil.esTokenValido(token)) {
            return null;
        }
        return token;
    }

    // Respuesta estandar para errores de autorizacion (401)
    private ResponseEntity<?> respuestaNoAutorizado(String mensaje) {
        Map<String, String> error = new HashMap<>();
        error.put("error", mensaje);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // Respuesta estandar para errores de negocio (400)
    private ResponseEntity<?> respuestaError(String mensaje) {
        Map<String, String> error = new HashMap<>();
        error.put("error", mensaje);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
