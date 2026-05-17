package cl.duoc.ms_usuarios.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Tabla "juegos_favoritos" en la base de datos ms_usuarios.
 *
 * Un perfil puede tener MUCHOS juegos favoritos.
 * Un juego favorito pertenece a UN perfil.
 * Eso es una relacion de "muchos a uno" (ManyToOne).
 *
 * Ejemplos de nombres de juego:
 *   "Magic: The Gathering", "Pokemon TCG", "Yu-Gi-Oh", "Catan"
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "juegos_favoritos")
public class JuegoFavorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Relacion: este juego pertenece a un perfil
    // FetchType.LAZY = no carga el perfil completo a menos que lo pidamos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfil_id", nullable = false)
    private Perfil perfil;

    // Nombre del juego (ej: "Magic: The Gathering")
    @Column(nullable = false)
    private String nombreJuego;

}
