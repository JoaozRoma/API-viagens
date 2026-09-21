package com.agencia.viagens.config;

import com.agencia.viagens.model.Destino;
import com.agencia.viagens.model.Role;
import com.agencia.viagens.model.Usuario;
import com.agencia.viagens.repository.DestinoRepository;
import com.agencia.viagens.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
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
    @Transactional
    public void run(String... args) {
        criarUsuarioSeAusente("admin", "admin123", Role.ROLE_ADMIN);
        criarUsuarioSeAusente("user", "user123", Role.ROLE_USER);

        if (destinoRepository.count() == 0) {
            Destino noronha = new Destino(
                    "Fernando de Noronha",
                    "Pernambuco, Brasil",
                    "Arquipélago vulcânico conhecido por praias preservadas e mergulho.");
            noronha.registrarAvaliacao(9.8);
            noronha.registrarAvaliacao(10.0);
            destinoRepository.save(noronha);

            Destino gramado = new Destino(
                    "Gramado",
                    "Rio Grande do Sul, Brasil",
                    "Cidade da Serra Gaúcha conhecida pelo turismo e pelos eventos sazonais.");
            gramado.registrarAvaliacao(8.5);
            gramado.registrarAvaliacao(9.0);
            destinoRepository.save(gramado);

            Destino salvador = new Destino(
                    "Salvador",
                    "Bahia, Brasil",
                    "Capital histórica com patrimônio cultural, gastronomia e praias.");
            salvador.registrarAvaliacao(9.0);
            destinoRepository.save(salvador);
        }
    }

    private void criarUsuarioSeAusente(String username,
                                       String senha,
                                       Role role) {
        if (!usuarioRepository.existsByUsername(username)) {
            Usuario usuario = new Usuario(
                    username,
                    passwordEncoder.encode(senha),
                    role);
            usuarioRepository.save(usuario);
        }
    }
}
