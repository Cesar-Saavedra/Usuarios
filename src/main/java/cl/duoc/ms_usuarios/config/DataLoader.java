package cl.duoc.ms_usuarios.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import cl.duoc.ms_usuarios.Model.Perfil;
import cl.duoc.ms_usuarios.Repository.JuegoFavoritoRepository;
import cl.duoc.ms_usuarios.Repository.PerfilRepository;

@Configuration
public class DataLoader {

    CommandLineRunner initData(PerfilRepository perfilRepository, JuegoFavoritoRepository juegoFavoritoRepository){
        return args -> {
            Perfil perfil1 = new Perfil();
            perfil1.setNombre("Ash Ketchum");

            perfilRepository.save(perfil1);
        };

    }
}
