package br.com.techmarket_order_service.repository;

import br.com.techmarket_order_service.model.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Page<Pedido> findByIdUsuario(Long idUsuario, Pageable pageable);

    @Query("""
    SELECT COUNT(p)
    FROM Pedido p
    WHERE p.dataCriacao >= :inicio
    AND p.dataCriacao < :fim
    """)
    Long contarPedidosDeHoje(
            @Param("inicio") OffsetDateTime inicio,
            @Param("fim") OffsetDateTime fim
    );
}
