package cl.duoc.ms_usuarios.dto;

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

    private String nombre;
    private String bio;
    private String ciudad;

}
