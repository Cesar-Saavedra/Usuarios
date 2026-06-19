package cl.duoc.ms_usuarios.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/*
 * DTO de PETICION: los datos que el usuario manda para actualizar su perfil.
 *
 * Solo incluye los campos que el usuario PUEDE cambiar.
 * No incluye usuarioId porque ese viene del JWT (no del body).
 *
 * Ejemplo de body JSON:
 * {
 *   "nombre": "Pedro Gamer",
 *   "bio": "Fanático del TCG competitivo",
 *   "ciudad": "Valparaíso"
 * }
 */
@Data
public class ActualizarPerfilDto {

    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @Size(max = 500, message = "La bio no puede superar los 500 caracteres")
    private String bio;

    @Size(max = 100, message = "La ciudad no puede superar los 100 caracteres")
    private String ciudad;

}
