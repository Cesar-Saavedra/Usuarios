package cl.duoc.ms_usuarios.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Tabla "perfiles" en la base de datos ms_usuarios.
 *
 * Cada fila de esta tabla representa el perfil extendido de un usuario.
 * El campo "usuarioId" es el id que viene del ms-login (tabla usuarios).
 * Con ese id conectamos los dos microservicios SIN necesitar una FK real
 * entre bases de datos distintas.
 *
 * Ejemplo: si en ms-login Pedro tiene id=3,
 * en esta tabla habrá un perfil con usuarioId=3.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "perfiles")
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Id del usuario en ms-login. Cada usuario tiene UN perfil.
    @Column(nullable = false, unique = true)
    private Integer usuarioId;

    // Nombre que muestra el perfil (puede ser diferente al nombre de registro)
    @Column(nullable = false)
    private String nombre;

    // Texto libre que el usuario escribe sobre si mismo
    @Column(length = 500)
    private String bio;

    // Ciudad donde vive el jugador (ej: "Santiago", "Valparaiso")
    @Column
    private String ciudad;

}
