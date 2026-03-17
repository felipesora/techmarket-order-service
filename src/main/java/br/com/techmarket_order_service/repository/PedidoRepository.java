package br.com.techmarket_order_service.repository;

import br.com.techmarket_order_service.model.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Page<Pedido> findByIdUsuario(Long idUsuario, Pageable pageable);
}
