package cl.duoc.ms_usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/*
 * DTO de PETICION: los datos para agregar un juego favorito.
 *
 * Ejemplo de body JSON:
 * {
 *   "nombreJuego": "Magic: The Gathering"
 * }
 */
@Data
public class AgregarJuegoDto {

    @NotBlank(message = "El nombre del juego no puede estar vacio")
    @Size(max = 100, message = "El nombre del juego no puede superar los 100 caracteres")
    private String nombreJuego;

}
