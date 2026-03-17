package br.com.techmarket_order_service.model;

import br.com.techmarket_order_service.model.enums.StatusProduto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "TM_PRODUTOS_SNAPSHOT")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProdutoSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tm_produto_seq")
    @SequenceGenerator(name = "tm_produto_seq", sequenceName = "tm_produto_seq", allocationSize = 1)
    @Column(name = "id_produto")
    private Long id;

    @Column(nullable = false, unique = true, length = 24, name = "id_mongo")
    private String idMongo;

    @Column(nullable = false, length = 50, unique = true)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "preco_unitario", nullable = false)
    private BigDecimal precoUnitario;

    @Column(nullable = false)
    private Integer estoque;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusProduto status;
}
