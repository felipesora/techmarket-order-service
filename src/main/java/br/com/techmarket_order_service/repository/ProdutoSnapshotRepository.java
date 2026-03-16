package br.com.techmarket_order_service.repository;

import br.com.techmarket_order_service.model.ProdutoSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProdutoSnapshotRepository extends JpaRepository<ProdutoSnapshot, Long> {

    Optional<ProdutoSnapshot> findByIdMongo(String idMongo);
}
