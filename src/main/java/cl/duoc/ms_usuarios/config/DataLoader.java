package cl.duoc.ms_usuarios.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import cl.duoc.ms_usuarios.model.Perfil;
import cl.duoc.ms_usuarios.repository.JuegoFavoritoRepository;
import cl.duoc.ms_usuarios.repository.PerfilRepository;

@Configuration
public class DataLoader {

    CommandLineRunner initData(PerfilRepository perfilRepository, JuegoFavoritoRepository juegoFavoritoRepository){
        return args -> {
            Perfil perfil1 = new Perfil();
            perfil1.setId(null);
            perfil1.setUsuarioId(1); // Este id debe coincidir con el id del usuario en ms-login
            perfil1.setNombre("Ash Ketchum");
            perfil1.setBio("Entrenador Pokémon de Pueblo Paleta. Mi sueño es ser el mejor del mundo.");
            perfil1.setCiudad("Pueblo Paleta");

            Perfil perfil2 = new Perfil();
            perfil2.setId(null);
            perfil2.setUsuarioId(2); // Este id debe coincidir con el id del usuario en ms-login
            perfil2.setNombre("Gary Oak"); 
            perfil2.setBio("Rival de Ash. Entrenador talentoso y orgulloso. Siempre buscando superar a Ash.");
            perfil2.setCiudad("Pueblo Paleta"); 
            
            Perfil perfil3 = new Perfil();
            perfil3.setId(null);
            perfil3.setUsuarioId(3); // Este id debe coincidir con el id del usuario en ms-login
            perfil3.setNombre("Admin Tienda Centro");
            perfil3.setBio("Administrador de la Tienda Centro. Encargado de gestionar los productos y promociones.");
            perfil3.setCiudad("Santiago");

            perfilRepository.save(perfil1);
            perfilRepository.save(perfil2);
            perfilRepository.save(perfil3);
        };

    }
}
