# Passagens Vargas — Back-End

Sistema de **venda e gestão de passagens de ônibus** (multi-empresa). A plataforma permite que empresas de transporte cadastrem ônibus, viagens, rotas e preços, e que clientes comprem passagens pela web ou através de funcionários da empresa no balcão. Inclui gestão de pagamentos, reembolsos, faturas e relatórios em PDF.

- **Grupo / Artefato:** `com.alvaro.empresas:passagens` (v0.0.1-SNAPSHOT)
- **Fuso horário da aplicação:** `America/La_Paz` (Bolívia)

---

## Sumário

- [Stack tecnológica](#stack-tecnológica)
- [Domínio do negócio](#domínio-do-negócio)
- [Arquitetura e estrutura de pacotes](#arquitetura-e-estrutura-de-pacotes)
- [Segurança e autenticação](#segurança-e-autenticação)
- [API REST](#api-rest)
- [Persistência](#persistência)
- [Configuração](#configuração)
- [Como compilar e executar](#como-compilar-e-executar)
- [Documentação da API (Swagger)](#documentação-da-api-swagger)

---

## Stack tecnológica

| Item | Tecnologia |
|------|-----------|
| Linguagem | Java 17 |
| Framework | Spring Boot 3.4.2 |
| Build | Maven (via wrapper `./mvnw`) |
| Persistência | Spring Data JPA / Hibernate |
| Banco (dev) | H2 (em memória) |
| Banco (prod) | MySQL |
| Segurança | Spring Security 6 + JWT (Auth0 `java-jwt` 4.4.0) |
| E-mail | Spring Mail (SMTP) + Thymeleaf (templates) |
| Geração de PDF | Apache PDFBox 3.0.2, iText html2pdf 4.0.2, Flying Saucer 9.1.22 |
| Documentação | SpringDoc OpenAPI 2.8.6 (Swagger UI) |
| Utilidades | Lombok, Spring Validation |

---

## Domínio do negócio

O sistema é multi-empresa (cada **Empresa** é um operador de transporte). A hierarquia central é:

```
Empresa ──< Onibus ──< Piso (decks/assentos)
   │           │
   └──< Viagem ─┘
         ├──< Parada (SAIDA / DESTINO) ──> Lugar ──> Cidade ──> Departamento
         ├──< Preco (por piso)
         └──< FaturaPassagem ──< Passagem ──> (Reembolso opcional)
```

### Principais entidades

| Entidade | Descrição |
|----------|-----------|
| `EmpresaModel` | Operador de transporte. Pode ser habilitado/bloqueado. |
| `OnibusModel` | Veículo (placa). Pertence a uma empresa. |
| `PisoModel` | Andar/deck do ônibus: linhas, colunas, assentos, posições bloqueadas. |
| `ViagemModel` | Viagem com data/hora de saída; controla valores arrecadados (dinheiro / web / não-web). |
| `ParadaModel` | Ponto de uma viagem, tipo `SAIDA` ou `DESTINO`, com plataforma e horário. |
| `LugarModel` / `CidadeModel` / `DepartamentoModel` | Hierarquia geográfica das paradas. |
| `PrecoModel` | Preço por piso de uma viagem; controla assentos disponíveis. |
| `PassagemModel` | Passagem individual (assento, passageiro: CPF/nome/nascimento, método de pagamento). |
| `FaturaPassagemModel` | Fatura agrupando passagens (valor total, desconto, taxa de serviço, status de pagamento). |
| `FaturaReembolsoModel` | Fatura de reembolso vinculada a uma passagem. |
| `ContatoModel` | Dados de contato associados a uma fatura. |
| `UsuarioModel` / `RoleModel` | Usuário (implementa `UserDetails`) e seus papéis. |

### Enums relevantes

- `TipoPagamento`: `QR`, `DINHEIRO`, `DEBITO`, `CREDITO`
- `TipoParada`: `SAIDA`, `DESTINO`
- `RoleList`: `ROLE_ADMIN`, `ROLE_CLIENTE`, `ROLE_EMPRESA_ADMIN`, `ROLE_EMPRESA_FUNCIONARIO`

### Regras de negócio configuráveis

- Compra permitida até **30 minutos antes** da saída (`api.viaje.min-time-before-buy-pasaje-min`).
- Janela de busca de viagens por dia: **3 dias** (`api.viaje.max-time-viaje-day`).
- Taxa de serviço sobre a passagem: **10%** (`api.passagem.taxa.uso = 0.1`).

---

## Arquitetura e estrutura de pacotes

Arquitetura em camadas (`Resource` → `Service` → `Repository` → `Model`), organizada por domínio. Base de pacote: `com.alvaro.empresas.passagens`.

```
src/main/java/com/alvaro/empresas/passagens/
├── PassagensApplication.java        # Entry point
├── configuracoes/                   # Config geral, exceptions, JPA custom, SpringDoc, validations
├── dtos/                            # DTOs de entrada/saída (passagens, precos, viagens...)
├── enums/                           # TipoPagamento, TipoParada, TipoSolicitacao
├── helpers/                         # PDF (PassagensPDF), beans (usuário logado), thymeleaf, utils
├── models/                          # Entidades núcleo (Viagem, Passagem, Empresa, Preco, Contato)
├── repositories/                    # Repositórios JPA do núcleo
├── resources/                       # Controllers REST do núcleo
├── services/                        # Regras de negócio do núcleo + EmailService
├── onibus/                          # Subdomínio: ônibus e pisos (model/repo/resource/service/dto)
├── paradas/                         # Subdomínio: parada, lugar, cidade, departamento
├── pagamentos/                      # Subdomínio: faturas, reembolsos, relatórios
└── security/                        # Usuário, roles, JWT, filtros e config de segurança

src/main/resources/
├── application.properties           # Config base (perfil ativo: h2)
├── application-h2.properties        # Perfil desenvolvimento (H2)
├── application-mysql.properties     # Perfil MySQL (staging/teste)
├── application-prod.properties      # Perfil produção
├── application-test.properties      # Perfil de testes
└── templates/                       # Templates Thymeleaf de e-mail
```

---

## Segurança e autenticação

- **Spring Security 6**, sessão *stateless* (`SessionCreationPolicy.STATELESS`), CSRF desabilitado.
- Autenticação via **JWT** (Auth0 `java-jwt`). O `SecurityFilter` extrai o token `Bearer` do header `Authorization`, valida pelo `TokenService` e popula o `SecurityContext`.
- Segredo de assinatura do token: `api.security.token.secret` (variável `JWT_SECRET`).
- `UsuarioModel` implementa `UserDetails`; papéis carregados da tabela de junção `tb_usuario_role`.
- Autorização por método com `@PreAuthorize("hasRole(...)")` / `hasAnyRole(...)`.
- Configuração por perfil (`MainSecurity`): perfis `h2`/`mysql` usam rotas e CORS de desenvolvimento (console H2 liberado); perfil `prod` usa rotas e CORS restritos.
- **CORS** dev permite `http://localhost:5173` (front-end padrão).

### Papéis (roles)

| Papel | Escopo |
|-------|--------|
| `ROLE_ADMIN` | Administração global da plataforma |
| `ROLE_EMPRESA_ADMIN` | Administração de uma empresa (ônibus, viagens, funcionários) |
| `ROLE_EMPRESA_FUNCIONARIO` | Operação de venda no balcão |
| `ROLE_CLIENTE` | Cliente final |

### Fluxo de autenticação

1. `POST /auth/register` → envia código de verificação por e-mail.
2. `POST /auth/validar` → confirma o cadastro com o código.
3. `POST /auth/login` → retorna `TokenDTO` (JWT).
4. `POST /auth/refresh` → renova o token.
5. Recuperação de senha: `POST /auth/forget_password` → `PUT /auth/reset_password`.

---

## API REST

Base URL local: `http://localhost:8080`. A maioria dos endpoints exige `Authorization: Bearer <token>`. Abaixo, os controllers e seus principais endpoints.

### Autenticação — `/auth`
| Método | Caminho | Descrição |
|--------|---------|-----------|
| POST | `/auth/login` | Login → `TokenDTO` |
| POST | `/auth/refresh` | Renovar token |
| POST | `/auth/register` | Registrar usuário (envia código) |
| POST | `/auth/validar` | Validar cadastro com código |
| POST | `/auth/forget_password` | Solicitar código de redefinição de senha |
| PUT | `/auth/reset_password` | Redefinir senha com código |

### Usuários — `/usuarios`
| Método | Caminho | Descrição |
|--------|---------|-----------|
| GET | `/usuarios/mydata` | Dados do usuário logado |
| PUT | `/usuarios/update` | Atualizar perfil (envia código) |
| PUT | `/usuarios/validar_update` | Validar atualização com código |

### Empresas — `/empresas`
| Método | Caminho | Descrição | Papel |
|--------|---------|-----------|-------|
| POST | `/empresas/admin` | Cadastrar admin da empresa | ADMIN |
| DELETE | `/empresas/admin/{email}` | Remover admin da empresa | ADMIN |
| GET | `/empresas` | Listar empresas (paginado) | ADMIN |
| GET | `/empresas/{id}` | Detalhe da empresa | — |
| GET | `/empresas/{id}/onibus` | Ônibus da empresa (paginado) | ADMIN / EMPRESA_* |
| GET | `/empresas/{id}/viagens` | Viagens por mês (`?mesAnalise=`) | — |
| GET | `/empresas/{id}/relatorio` | Relatório mensal em PDF | ADMIN / EMPRESA_ADMIN |
| POST | `/empresas` | Criar empresa | ADMIN |
| PUT | `/empresas/{id}` | Atualizar empresa | ADMIN |
| DELETE | `/empresas/{id}/bloquedCount` | Bloquear empresa | ADMIN |
| DELETE | `/empresas/{id}` | Excluir empresa | ADMIN |

### Funcionários — `/funcionarios`
| Método | Caminho | Descrição | Papel |
|--------|---------|-----------|-------|
| GET | `/funcionarios?idEmpresa=` | Listar funcionários da empresa | ADMIN / EMPRESA_ADMIN |
| POST | `/funcionarios?idEmpresa=` | Cadastrar funcionário | EMPRESA_ADMIN |
| DELETE | `/funcionarios?idEmpresa=&email=` | Remover funcionário | EMPRESA_ADMIN |

### Ônibus — `/onibus`
| Método | Caminho | Descrição | Papel |
|--------|---------|-----------|-------|
| GET | `/onibus/{id}` | Detalhe do ônibus | — |
| GET | `/onibus/{id}/viagens?mesAnalise=` | Viagens do ônibus por mês | — |
| POST | `/onibus` | Criar ônibus | EMPRESA_ADMIN |
| PUT | `/onibus/{id}` | Atualizar ônibus | EMPRESA_ADMIN |
| DELETE | `/onibus/{id}` | Excluir ônibus | EMPRESA_ADMIN |

### Pisos — `/pisos`
| Método | Caminho | Descrição | Papel |
|--------|---------|-----------|-------|
| GET | `/pisos/{id}` | Detalhe do piso | — |
| PUT | `/pisos/{id}` | Atualizar piso | EMPRESA_ADMIN |

### Viagens (consulta) — `/viagens`
| Método | Caminho | Descrição |
|--------|---------|-----------|
| GET | `/viagens/{id}` | Detalhe da viagem |
| GET | `/viagens/{id}/vender` | Viagem para venda (disponibilidade de assentos) |
| GET | `/viagens` | Buscar viagens por dia (origem/destino/data) |

### Viagens (empresa) — `/empresa/viagens`
| Método | Caminho | Descrição | Papel |
|--------|---------|-----------|-------|
| GET | `/empresa/viagens/{id}/pdf` | Relatório da viagem em PDF | — |
| GET | `/empresa/viagens/{id}/pagamentos` | Pagamentos da viagem (paginado) | — |
| GET | `/empresa/viagens/{idEmpresa}` | Viagens da empresa por dia | — |
| POST | `/empresa/viagens` | Criar viagem | EMPRESA_ADMIN / FUNCIONARIO |
| POST | `/empresa/viagens/duplicate` | Duplicar viagem | EMPRESA_ADMIN / FUNCIONARIO |
| PUT | `/empresa/viagens/{id}` | Atualizar viagem | EMPRESA_ADMIN / FUNCIONARIO |
| DELETE | `/empresa/viagens/{id}` | Excluir viagem | EMPRESA_ADMIN / FUNCIONARIO |

### Passagens — `/passagens`
| Método | Caminho | Descrição | Papel |
|--------|---------|-----------|-------|
| GET | `/passagens/{id}` | Detalhe da passagem | — |
| GET | `/passagens/{id}/download` | Baixar passagem em PDF | — |
| POST | `/passagens/comprar` | Comprar passagem (web, público) | — |
| POST | `/passagens/vender` | Vender passagem (balcão) | EMPRESA_FUNCIONARIO / ADMIN |
| DELETE | `/passagens/{id}` | Reembolsar passagem | EMPRESA_FUNCIONARIO / ADMIN |

### Preços — `/precos`
| Método | Caminho | Descrição | Papel |
|--------|---------|-----------|-------|
| GET | `/precos/{id}` | Detalhe do preço | — |
| PUT | `/precos/{id}` | Atualizar preço | EMPRESA_ADMIN / FUNCIONARIO |
| GET | `/precos/{id}/passagens` | Passagens vendidas para o preço | ADMIN / EMPRESA_* |

### Pagamentos — `/pagamentos`
| Método | Caminho | Descrição | Papel |
|--------|---------|-----------|-------|
| POST | `/pagamentos/{id}/pagar-qr` | Marcar fatura como paga (QR) | ADMIN |
| GET | `/pagamentos/{id}/download` | Baixar fatura em PDF | — |

### Localização — `/paradas`, `/lugares`, `/cidades`, `/departamentos`
CRUD com paginação. Criação/edição/exclusão geralmente exigem `ROLE_ADMIN` (paradas também por `EMPRESA_*`). Exemplos:
- `GET /cidades?nome=` (filtro), `GET /cidades/{id}/lugares`
- `GET /lugares/{id}/paradas`

> A lista completa e os esquemas de request/response estão disponíveis no Swagger UI (ver abaixo).

---

## Persistência

- Entidades estendem `IEntityStandart` (id `UUID`, timestamps de auditoria via Lombok).
- Repositórios estendem `JpaRepository` + interface custom `ICustomRepository`.
- Estratégia de DDL por perfil:
  - **h2:** `ddl-auto=update` (cria/atualiza schema automaticamente)
  - **mysql:** `ddl-auto=validate` (valida schema existente)
  - **prod:** `ddl-auto=none` (migrações manuais)

### Principais tabelas

`tb_empresa`, `tb_onibus`, `tb_piso`, `tb_viagem`, `tb_parada`, `tb_lugar`, `tb_cidade`, `tb_departamento`, `tb_preco`, `tb_passagem`, `tb_fatura_passagem`, `tb_fatura_reembolso`, `tb_contato`, `tb_usuario`, `tb_role`, `tb_usuario_role`.

Índices definidos em viagem (`empresa`, `data_hora_saida`), parada (lugar/empresa/viagem), passagem (preço/fatura), fatura (viagem/data), entre outros, para otimizar as consultas de busca de viagens e relatórios.

---

## Configuração

Perfil ativo padrão: **`h2`** (definido em `application.properties`).

### Variáveis de ambiente

| Variável | Uso | Padrão |
|----------|-----|--------|
| `JWT_SECRET` | Segredo de assinatura do JWT | `pastaMasterChef` |
| `BASE_URL_FRONT` | URL do front-end (links de e-mail) | `http://localhost:5173` |
| `DB_HOST` | URL JDBC do banco (MySQL/prod) | — |
| `DB_USER` | Usuário do banco | — |
| `DB_PASSWORD` | Senha do banco | — |

### Parâmetros de negócio (`application.properties`)

| Propriedade | Valor | Significado |
|-------------|-------|-------------|
| `api.viaje.max-time-viaje-day` | `3` | Janela (dias) de busca de viagens |
| `api.viaje.min-time-before-buy-pasaje-min` | `30` | Antecedência mínima de compra (min) |
| `api.passagem.taxa.uso` | `0.1` | Taxa de serviço (10%) |
| `spring.jackson.time-zone` | `America/La_Paz` | Fuso horário |

> ⚠️ **Atenção de segurança:** o arquivo `application.properties` versionado contém credenciais de SMTP e um segredo de JWT padrão em texto plano. Recomenda-se mover esses valores para variáveis de ambiente/secret manager e rotacionar as credenciais expostas.

---

## Como compilar e executar

**Pré-requisitos:** JDK 17 e (para produção) um banco MySQL acessível. O Maven já vem embarcado via wrapper (`./mvnw`).

### Compilar / empacotar
```bash
./mvnw clean package
```

### Executar em desenvolvimento (H2 em memória, perfil padrão)
```bash
./mvnw spring-boot:run
```
A aplicação sobe na porta padrão **8080**. O console do H2 fica em `http://localhost:8080/h2-console`.

### Executar com MySQL (staging/teste)
```bash
export DB_HOST=jdbc:mysql://localhost:3306/teste
export DB_USER=root
export DB_PASSWORD=senha123
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=mysql"
```

### Executar em produção
```bash
export JWT_SECRET=<segredo-forte>
export BASE_URL_FRONT=https://seu-frontend.com
export DB_HOST=jdbc:mysql://<host>:3306/<database>
export DB_USER=<usuario>
export DB_PASSWORD=<senha>
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
# ou, a partir do jar empacotado:
java -jar target/passagens-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Executar testes
```bash
./mvnw test
```

---

## Documentação da API (Swagger)

Com a aplicação em execução, a UI interativa do SpringDoc/OpenAPI fica disponível em:

- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

Os endpoints protegidos usam o esquema de segurança `bearer-key` — informe o token JWT obtido no `/auth/login` para testá-los.
