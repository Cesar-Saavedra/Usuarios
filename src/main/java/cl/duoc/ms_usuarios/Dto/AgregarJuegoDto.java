package cl.duoc.ms_usuarios.Dto;

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

    private String nombreJuego;

}
