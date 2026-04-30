<div align="center">

# 🛒 TechMarket Order Service

### Serviço responsável pela criação, processamento e gerenciamento de pedidos da plataforma TechMarket.

<br/>

[![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge\&logo=openjdk\&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge\&logo=spring-boot\&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge\&logo=springsecurity\&logoColor=white)](https://spring.io/projects/spring-security)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge\&logo=postgresql\&logoColor=white)](https://www.postgresql.org/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-FF6600?style=for-the-badge\&logo=rabbitmq\&logoColor=white)](https://www.rabbitmq.com/)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge\&logo=docker\&logoColor=white)](https://www.docker.com/)

</div>

---

## 📋 Índice

- [Sobre o Order Service](#-sobre-o-order-service)
- [Principais Funcionalidades](#️-principais-funcionalidades)
- [Arquitetura e Papel no Sistema](#-arquitetura-e-papel-no-sistema)
- [Tecnologias Utilizadas](#️-tecnologias-utilizadas)
- [Dependências Relevantes](#-dependências-relevantes)
- [Fluxo de Pedido](#-fluxo-de-pedido)
- [Boas Práticas Aplicadas](#-boas-práticas-aplicadas)
- [Integração com Outros Serviços](#-integração-com-outros-serviços)
- [Repositórios](#-repositórios)
- [Autor](#-autor)

---

## 💡 Sobre o Order Service

O **Order Service** é o microsserviço responsável pelo gerenciamento de pedidos dentro da plataforma TechMarket. Ele orquestra a criação, validação e acompanhamento dos pedidos realizados pelos usuários.

Este serviço atua como um ponto central no fluxo de compra, garantindo a consistência dos dados e coordenando a comunicação com outros serviços, especialmente através de eventos assíncronos.

Foi projetado com foco em:

* **Consistência de dados**
* **Orquestração de processos**
* **Desacoplamento via eventos**
* **Escalabilidade**

---

## ⚙️ Principais Funcionalidades

* 🛒 Criação de pedidos
* 📄 Consulta de pedidos por usuário
* 📊 Acompanhamento de status do pedido
* ✔️ Validação de disponibilidade de produtos
* 💰 Cálculo de valores totais
* 📢 Publicação de eventos (ex: pedido criado)
* 🔐 Proteção de endpoints com JWT
* 🔍 Registro no Eureka (Service Discovery)

---

## 🧱 Arquitetura e Papel no Sistema

O Order Service se posiciona como:

```
Frontend → Gateway → Order Service → PostgreSQL
                         ↓
                     RabbitMQ → Payment Service
                         ↓
                  Product Service
```

### Responsabilidades:

| Responsabilidade | Descrição                       |
| ---------------- | ------------------------------- |
| Pedido           | Criação e gerenciamento         |
| Persistência     | Armazenamento em PostgreSQL     |
| Validação        | Consulta de produtos/estoque    |
| Orquestração     | Coordenação do fluxo de compra  |
| Eventos          | Publicação de eventos de pedido |
| Segurança        | Validação de JWT                |
| Integração       | Registro no Eureka              |

---

## 🛠️ Tecnologias Utilizadas

### Backend

* Java 21
* Spring Boot 3.5
* Spring Web
* Spring Data JPA
* Spring Validation
* Spring Security

### Banco de Dados

* PostgreSQL (Relacional)

### Mensageria

* RabbitMQ
* Spring AMQP

### Segurança

* JWT (JSON Web Token)
* Biblioteca `java-jwt` (Auth0)

### Cloud & Infra

* Spring Cloud Netflix Eureka Client
* Docker

### Utilitários

* Lombok

---

## 📦 Dependências Relevantes

Principais dependências do projeto:

* `spring-boot-starter-data-jpa`
* `spring-boot-starter-web`
* `spring-boot-starter-validation`
* `spring-boot-starter-security`
* `spring-boot-starter-amqp`
* `java-jwt`
* `postgresql`
* `spring-cloud-starter-netflix-eureka-client`

---

## 🔄 Fluxo de Pedido

1. Usuário cria um pedido via frontend
2. Requisição passa pelo Gateway
3. Order Service valida produtos (Product Service)
4. Pedido é persistido no banco
5. Evento `pedido_criado` é publicado no RabbitMQ
6. Payment Service consome o evento
7. Status do pedido é atualizado conforme processamento

---

## 📊 Boas Práticas Aplicadas

* Arquitetura em camadas (Controller → Service → Repository)
* Separação de responsabilidades (SRP)
* Uso de DTOs para comunicação externa
* Validações com Bean Validation
* Persistência com JPA (ORM)
* Comunicação assíncrona orientada a eventos
* Segurança stateless com JWT
* Uso de Service Discovery (Eureka)

---

## 🔗 Integração com Outros Serviços

| Serviço         | Integração                        |
| --------------- | --------------------------------- |
| Gateway         | Roteamento do serviço             |
| Identity        | Validação de autenticação via JWT |
| Product Service | Validação de produtos e estoque   |
| Payment Service | Consumo de eventos de pedido      |
| Discovery       | Registro via Eureka               |
| RabbitMQ        | Publicação de eventos             |

---

## 📁 Repositórios

O TechMarket é organizado como um **monorepo com submódulos Git**. Cada serviço possui seu próprio repositório:

| Serviço | Descrição | Repositório |
|---------|-----------|-------------|
| 🗂️ **techmarket** | Repositório principal (monorepo + Docker Compose) | [github.com/felipesora/techmarket](https://github.com/felipesora/techmarket) |
| 🔍 **discovery-service** | Eureka Server para service discovery | [github.com/felipesora/techmarket-discovery-service](https://github.com/felipesora/techmarket-discovery-service) |
| 🌐 **gateway-service** | API Gateway com Spring Cloud Gateway | [github.com/felipesora/techmarket-gateway-service](https://github.com/felipesora/techmarket-gateway-service) |
| 🔐 **identity-service** | Autenticação e gerenciamento de usuários (JWT) | [github.com/felipesora/techmarket-identity-service](https://github.com/felipesora/techmarket-identity-service) |
| 📦 **product-service** | Catálogo e gerenciamento de produtos | [github.com/felipesora/techmarket-product-service](https://github.com/felipesora/techmarket-product-service) |
| 🛒 **order-service** | Criação e acompanhamento de pedidos | [github.com/felipesora/techmarket-order-service](https://github.com/felipesora/techmarket-order-service) |
| 💳 **payment-service** | Processamento de pagamentos via mensageria | [github.com/felipesora/techmarket-payment-service](https://github.com/felipesora/techmarket-payment-service) |
| 🖥️ **techmarket-web** | Frontend da plataforma em Angular | [github.com/felipesora/techmarket-web](https://github.com/felipesora/techmarket-web) |

---

## 👨‍💻 Autor

Desenvolvido por **Felipe Sora**

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/felipesora)
[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/felipesora)
