package com.agencia.viagens.repository;

import com.agencia.viagens.model.Destino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DestinoRepository extends JpaRepository<Destino, Long> {

    List<Destino> findByNomeContainingIgnoreCase(String nome);

    List<Destino> findByLocalizacaoContainingIgnoreCase(String localizacao);

    List<Destino> findByNomeContainingIgnoreCaseAndLocalizacaoContainingIgnoreCase(String nome, String localizacao);

    @Query("SELECT d FROM Destino d WHERE " +
           "(:nome IS NULL OR LOWER(d.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
           "(:localizacao IS NULL OR LOWER(d.localizacao) LIKE LOWER(CONCAT('%', :localizacao, '%')))")
    List<Destino> pesquisarPorNomeELocalizacao(@Param("nome") String nome, @Param("localizacao") String localizacao);
}
