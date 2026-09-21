package com.agencia.viagens.repository;

import com.agencia.viagens.model.Destino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DestinoRepository extends JpaRepository<Destino, Long> {

    List<Destino> findByNomeContainingIgnoreCase(String nome);

    List<Destino> findByLocalizacaoContainingIgnoreCase(String localizacao);

    List<Destino> findByNomeContainingIgnoreCaseAndLocalizacaoContainingIgnoreCase(
            String nome,
            String localizacao);
}
