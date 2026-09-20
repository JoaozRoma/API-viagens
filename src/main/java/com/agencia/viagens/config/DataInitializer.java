package com.agencia.viagens.config;

import com.agencia.viagens.model.Destino;
import com.agencia.viagens.model.Role;
import com.agencia.viagens.model.Usuario;
import com.agencia.viagens.repository.DestinoRepository;
import com.agencia.viagens.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final DestinoRepository destinoRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           DestinoRepository destinoRepository,
                           PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.destinoRepository = destinoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Inicialização de Usuários Padrão para Teste
        if (!usuarioRepository.existsByUsername("admin")) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ROLE_ADMIN);
            usuarioRepository.save(admin);
        }

        if (!usuarioRepository.existsByUsername("user")) {
            Usuario user = new Usuario();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole(Role.ROLE_USER);
            usuarioRepository.save(user);
        }

        // Inicialização de Destinos de Exemplo (se a base estiver vazia)
        if (destinoRepository.count() == 0) {
            Destino d1 = new Destino(
                    "Fernando de Noronha",
                    "Pernambuco, Brasil",
                    "Arquipélago vulcânico paradisíaco famoso por praias intocadas e mergulho com tartarugas e golfinhos."
            );
            d1.registrarAvaliacao(9.8);
            d1.registrarAvaliacao(10.0);
            destinoRepository.save(d1);

            Destino d2 = new Destino(
                    "Gramado",
                    "Rio Grande do Sul, Brasil",
                    "Charmosa cidade na Serra Gaúcha com arquitetura alpina, chocolates artesanais e festivais de cinema e Natal."
            );
            d2.registrarAvaliacao(8.5);
            d2.registrarAvaliacao(9.0);
            destinoRepository.save(d2);

            Destino d3 = new Destino(
                    "Salvador",
                    "Bahia, Brasil",
                    "Capital histórica rica em cultura afro-brasileira, centro histórico no Pelourinho, gastronomia típica e praias tropicais."
            );
            d3.registrarAvaliacao(9.0);
            destinoRepository.save(d3);
        }
    }
}
