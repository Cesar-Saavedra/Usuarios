package cl.duoc.ms_usuarios.Dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * DTO de RESPUESTA: lo que devuelve el servidor cuando piden ver un perfil.
 *
 * Incluye los datos del perfil + la lista de juegos favoritos.
 * Usamos un DTO (y no la entidad directamente) para controlar
 * exactamente que datos mostramos al cliente.
 *
 * Ejemplo de respuesta JSON:
 * {
 *   "id": 1,
 *   "usuarioId": 3,
 *   "nombre": "Pedro",
 *   "bio": "Jugador de Magic desde 2018",
 *   "ciudad": "Santiago",
 *   "juegosFavoritos": ["Magic: The Gathering", "Pokemon TCG"]
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfilRespuestaDto {

    private Integer id;
    private Integer usuarioId;
    private String nombre;
    private String bio;
    private String ciudad;
    private List<String> juegosFavoritos;

}
