![CI](https://github.com/IgorInfnetTPSeASSESSMENTS/TP5-Projeto-De-Bloco/actions/workflows/ci.yml/badge.svg)
![CD](https://github.com/IgorInfnetTPSeASSESSMENTS/TP5-Projeto-De-Bloco/actions/workflows/cd.yml/badge.svg)
![Security](https://github.com/IgorInfnetTPSeASSESSMENTS/TP5-Projeto-De-Bloco/actions/workflows/security.yml/badge.svg)

# ADOPET – Sistema Web de Gestão de Abrigos, Pets e Solicitações de Adoção

Este repositório contém o sistema **ADOPET**, uma aplicação web CRUD desenvolvida em **Java 21** com **Spring Boot**, **Spring MVC** e **Thymeleaf**.

Nesta versão, o projeto evoluiu para um sistema mais completo, permitindo gerenciar:

* **abrigos**
* **pets de cada abrigo**
* **solicitações de adoção**

O sistema foi construído com foco em:

* arquitetura modular
* separação clara de responsabilidades
* robustez diante de falhas
* validação rigorosa de entradas
* testes automatizados em múltiplos níveis
* alta cobertura de código

---

# Objetivo do Sistema

O objetivo do ADOPET é demonstrar, de forma prática, a construção de um sistema web robusto aplicando boas práticas de engenharia de software.

A aplicação permite cadastrar e gerenciar abrigos, cadastrar e manter pets vinculados a esses abrigos e controlar o fluxo completo de solicitações de adoção.

Além do CRUD funcional, o projeto também busca demonstrar:

* modelagem de domínio com **Value Objects**
* uso de **Command/Query Separation (CQS)**
* tratamento adequado de falhas externas
* aplicação de **fail early** e **fail gracefully**
* testes unitários, de integração, web e end-to-end
* testes parametrizados e fuzz testing

---

# Tecnologias Utilizadas

* **Java 21**
* **Maven**
* **Spring Boot 4**
* **Spring MVC**
* **Thymeleaf**
* **Jakarta Bean Validation**
* **JUnit 5**
* **Mockito**
* **Selenium WebDriver**
* **JaCoCo**

---

# Arquitetura do Projeto

O sistema foi organizado em camadas com responsabilidades bem definidas.

```text
src/main/java
└── adopet
    ├── application
    │   ├── adoption
    │   └── shelterandpet
    ├── domain
    │   ├── adoption
    │   └── shelterandpet
    ├── exception
    ├── gateway
    ├── infrastructure
    │   └── memory
    └── web
        ├── controller
        ├── dto
        │   ├── adoption
        │   └── shelterandpet
        └── exception
```

## Camadas

### Domain

Contém as regras de negócio centrais e os objetos do domínio.

O domínio foi dividido em dois módulos:

#### `domain.adoption`

Responsável pela lógica de solicitações de adoção.

Principais classes:

* `AdoptionRequest`
* `ApplicantName`
* `ApplicantEmail`
* `ApplicantPhone`
* `ApplicantDocument`
* `ReasonText`
* `HousingType`
* `AdoptionRequestStatus`
* `EligibilityAnalysis`

#### `domain.shelterandpet`

Responsável pela lógica de abrigos e pets.

Principais classes:

* `Shelter`
* `Pet`
* `PhoneNumber`
* `Email`
* `PetName`
* `AgeYears`
* `WeightKg`
* `PetType`
* `PetStatus`

As validações principais do sistema ficam concentradas nessa camada, especialmente nos **Value Objects**.

---

### Application

Contém os casos de uso da aplicação, separados por contexto de negócio e organizados no estilo **CQS (Command/Query Separation)**.

#### `application.adoption`

Principais componentes:

* `CreateAdoptionRequestCommandHandler`
* `UpdateAdoptionRequestCommandHandler`
* `ApproveAdoptionRequestCommandHandler`
* `RejectAdoptionRequestCommandHandler`
* `CancelAdoptionRequestCommandHandler`
* `DeleteAdoptionRequestCommandHandler`
* `RetryEligibilityAnalysisCommandHandler`
* `ListAdoptionRequestsQuery`
* `GetAdoptionRequestDetailsQuery`

#### `application.shelterandpet`

Principais componentes:

* `RegisterShelterCommandHandler`
* `UpdateShelterCommandHandler`
* `DeleteShelterCommandHandler`
* `ListSheltersQuery`
* `RegisterPetCommandHandler`
* `UpdatePetCommandHandler`
* `DeletePetCommandHandler`
* `ListShelterPetsQuery`
* `ImportShelterPetsCommandHandler`
* `ExportShelterPetsCommandHandler`

Essa camada coordena as operações do sistema sem concentrar regras de interface ou detalhes de persistência.

---

### Gateway

Define interfaces que abstraem dependências externas e detalhes de infraestrutura.

Exemplos:

* `ShelterGateway`
* `PetGateway`
* `AdoptionRequestGateway`
* `EligibilityAnalysisGateway`
* `NotificationGateway`

Essas interfaces permitem desacoplamento entre o núcleo da aplicação e suas implementações concretas.

---

### Infrastructure

Contém implementações concretas das interfaces definidas em `gateway`.

Implementações atuais:

* `InMemoryShelterGateway`
* `InMemoryPetGateway`
* `InMemoryAdoptionRequestGateway`
* `ProgrammableEligibilityAnalysisGateway`
* `ProgrammableNotificationGateway`

A persistência utilizada no projeto é **em memória**, suficiente para os objetivos acadêmicos do trabalho e ideal para testes automatizados rápidos.

---

### Web

Contém a interface web da aplicação.

Inclui:

* controllers MVC
* DTOs de formulários
* tratamento global de exceções
* integração com templates Thymeleaf

Principais controllers:

* `HomeController`
* `ShelterController`
* `PetController`
* `AdoptionRequestController`

---

# Funcionalidades Implementadas

## Abrigos

O sistema permite:

* cadastrar abrigo
* listar abrigos
* editar abrigo
* excluir abrigo

## Pets

Para cada abrigo, o sistema permite:

* cadastrar pet
* listar pets do abrigo
* editar pet
* excluir pet
* importar pets via CSV
* exportar pets via CSV

## Solicitações de adoção

Para cada pet, o sistema permite:

* criar solicitação de adoção
* listar solicitações do pet
* visualizar detalhes
* editar solicitação
* aprovar solicitação
* rejeitar solicitação
* cancelar solicitação
* excluir solicitação
* executar nova análise automática
* configurar modos de simulação dos serviços externos

---

# Modelo de Domínio

## Shelter

Representa um abrigo cadastrado no sistema.

Campos principais:

* `Long id`
* `String name`
* `PhoneNumber phoneNumber`
* `Email email`

## Pet

Representa um pet vinculado a um abrigo.

Campos principais:

* `Long id`
* `PetType type`
* `PetName name`
* `String breed`
* `AgeYears age`
* `String color`
* `WeightKg weight`
* `PetStatus status`

## AdoptionRequest

Representa uma solicitação de adoção de um pet.

Campos principais:

* `Long id`
* `Long petId`
* `Long shelterId`
* `ApplicantName applicantName`
* `ApplicantEmail applicantEmail`
* `ApplicantPhone applicantPhone`
* `ApplicantDocument applicantDocument`
* `HousingType housingType`
* `boolean hasOtherPets`
* `ReasonText reason`
* `AdoptionRequestStatus status`
* `EligibilityAnalysis eligibilityAnalysis`
* `LocalDateTime createdAt`
* `LocalDateTime updatedAt`

---

# Regras de Negócio Importantes

## Solicitações de adoção

Regras principais implementadas:

* o `petId` deve ser válido
* o `shelterId` deve ser válido
* nome, email, telefone, documento, tipo de moradia e motivo são obrigatórios
* `updatedAt` não pode ser anterior a `createdAt`
* somente solicitações em `PENDING` ou `UNDER_REVIEW` podem ser editadas
* somente solicitações em `PENDING` ou `UNDER_REVIEW` podem ser aprovadas ou rejeitadas
* solicitações aprovadas não podem ser canceladas
* solicitações rejeitadas não podem ser canceladas
* uma solicitação já cancelada não pode ser cancelada novamente

## Pets

Regras principais:

* nome do pet não pode ser vazio
* raça não pode ser vazia
* cor não pode ser vazia
* idade não pode ser negativa
* idade deve estar dentro de faixa válida
* peso deve ser positivo
* peso deve estar dentro de faixa válida

## Abrigos

Regras principais:

* nome do abrigo não pode ser vazio
* telefone do abrigo não pode ser vazio
* email do abrigo não pode ser vazio
* email deve ser válido

---

# Fluxo de Status das Solicitações

## Status possíveis

* `PENDING`
* `UNDER_REVIEW`
* `APPROVED`
* `REJECTED`
* `CANCELLED`

## Resultados possíveis da análise automática

* `ELIGIBLE`
* `NOT_ELIGIBLE`
* `REQUIRES_MANUAL_REVIEW`
* `UNAVAILABLE`
* `NOT_REQUESTED`

## Regras de transição principais

* `ELIGIBLE` → `UNDER_REVIEW`
* `REQUIRES_MANUAL_REVIEW` → `PENDING`
* `UNAVAILABLE` → `PENDING`
* `NOT_ELIGIBLE` → `REJECTED`

Além disso, o método `withEligibilityAnalysis(...)` recalcula o status da solicitação com base no resultado da análise.

---

# Serviços Externos Simulados

Para demonstrar robustez do sistema, o projeto simula dois serviços externos.

## Serviço de análise de elegibilidade

Classe:

```text
ProgrammableEligibilityAnalysisGateway
```

Responsável por simular a análise automática de elegibilidade do solicitante.

## Serviço de notificação

Classe:

```text
ProgrammableNotificationGateway
```

Responsável por simular o envio de notificações ao solicitante.

## Modos de simulação

Os serviços podem ser configurados em cenários como:

* `SUCCESS`
* `TIMEOUT`
* `NETWORK_ERROR`
* `SERVICE_UNAVAILABLE`
* `INVALID_RESPONSE`
* `UNEXPECTED_FAILURE`

Esses cenários permitem testar o comportamento da aplicação em situações adversas sem depender de APIs externas reais.

---

# Fail Early e Fail Gracefully

## Fail Early

O sistema valida entradas o mais cedo possível.

Exemplos:

* ids obrigatórios ausentes ou inválidos
* campos obrigatórios em branco
* emails inválidos
* telefones inválidos
* documentos inválidos
* nomes muito curtos ou muito longos
* motivos muito curtos
* pesos e idades inválidos

Essas validações ocorrem principalmente nos **Value Objects**, nos DTOs com Bean Validation e nos command handlers.

## Fail Gracefully

Quando há falhas em dependências externas, a aplicação tenta preservar a operação principal sempre que possível.

Exemplos:

* falha na análise automática → a solicitação continua registrada com resultado `UNAVAILABLE`
* falha na notificação → a operação principal continua e apenas a notificação falha
* erros inesperados → o usuário recebe mensagem amigável, sem exposição de stacktrace

---

# Interface Web

A aplicação possui interface web com páginas para:

* página inicial
* listagem de abrigos
* cadastro e edição de abrigo
* listagem de pets de um abrigo
* cadastro e edição de pet
* importação e exportação CSV
* listagem de solicitações de adoção por pet
* criação de solicitação
* edição de solicitação
* detalhes da solicitação

A interface também exibe:

* mensagens de sucesso
* mensagens de erro
* contexto do pet e do abrigo em telas de solicitação
* modos atuais de simulação de análise e notificação

---

# Tratamento de Exceções

O projeto possui tratamento centralizado de exceções web através de:

```text
adopet.web.exception.WebExceptionHandler
```

Esse componente captura exceções de negócio como:

* `InvalidUserInputException`
* `DuplicateEntityException`
* `EntityNotFoundException`
* `InvalidStateTransitionException`
* `ExternalServiceException`

Também existe tratamento para exceções inesperadas, retornando uma mensagem genérica e segura ao usuário.

---

# Testes Automatizados

O projeto possui cobertura em múltiplos níveis.

## Testes de domínio

Validam:

* invariantes das entidades
* validação dos value objects
* transições de status
* comportamento de métodos de domínio
* equals e hashCode quando aplicável

## Testes da camada application

Validam:

* command handlers
* queries
* cenários de sucesso
* cenários de falha
* regras de transição
* integração com gateways simulados
* comportamento de fallback em falhas externas

## Testes da infraestrutura em memória

Validam:

* persistência em memória
* filtros de consulta
* atualização de entidades
* comportamento dos gateways programáveis

## Testes web com MockMvc

Validam:

* rotas HTTP
* formulários e binding
* mensagens exibidas ao usuário
* redirecionamentos
* validação de campos
* tratamento de exceções

## Testes Selenium

Simulam o comportamento real do usuário no navegador.

Validam fluxos como:

* CRUD de solicitações de adoção
* navegação entre páginas
* cadastro de abrigo e pet dentro do fluxo
* criação de contexto para testes avançados
* edição, aprovação, rejeição e exclusão
* comportamento visual da interface

## Testes parametrizados

Foram utilizados para validar múltiplos cenários com menos duplicação de código, especialmente em:

* entradas inválidas
* cenários de validação
* entradas maliciosas

## Fuzz testing

O projeto inclui fuzz testing para fortalecer a robustez do sistema diante de entradas inesperadas.

Exemplos de campos exercitados:

* nomes
* emails
* telefones
* documentos
* tipos de moradia
* motivos da adoção

Objetivo:

garantir que entradas aleatórias ou potencialmente problemáticas não derrubem o sistema nem violem regras críticas.

---

# Testes de Robustez e Cenários Adversos

Além do CRUD tradicional, o projeto inclui testes voltados para exigências de robustez.

## Simulação de erros de rede e timeout

Os testes Selenium avançados exercitam o sistema com serviços externos programáveis em cenários como:

* timeout na análise de elegibilidade
* timeout na notificação
* repetição de análise após falha anterior

## Entradas inválidas

Há testes cobrindo formulários com dados inválidos, verificando se a interface continua estável e se o usuário recebe feedback apropriado.

## Entradas maliciosas

Também existem testes com entradas suspeitas ou maliciosas, com o objetivo de garantir que o sistema:

* não quebre
* não exponha falhas inesperadas
* trate os dados como entrada comum de usuário
* preserve o fluxo seguro da aplicação

---

# Como Executar a Aplicação

## Compilar o projeto

```bash
mvn clean compile
```

## Executar a aplicação

```bash
mvn spring-boot:run
```

A aplicação estará disponível em:

```text
http://localhost:8080
```

---

# Como Executar os Testes

## Executar toda a suíte

```bash
mvn test
```

## Executar testes em modo headless

```bash
mvn test -Dheadless=true
```

## Executar apenas uma classe específica

Exemplo:

```bash
mvn -Dtest=AdoptionRequestSimulationTest test
```

## Executar relatório de cobertura

```bash
mvn clean test jacoco:report
```

O relatório será gerado em:

```text
target/site/jacoco/index.html
```

---

# Cobertura de Código

O projeto foi desenvolvido para manter alta cobertura de código, com atenção especial a:

* branch coverage
* decisões de negócio
* caminhos de falha
* tratamento de exceções
* cenários alternativos
* robustez diante de entradas inválidas

A meta mínima exigida é **85% de cobertura**, e o projeto foi estruturado para trabalhar acima desse patamar.

---

# Estrutura do Projeto

```text
src/main/java
└── adopet
    ├── application
    │   ├── adoption
    │   └── shelterandpet
    ├── domain
    │   ├── adoption
    │   └── shelterandpet
    ├── exception
    ├── gateway
    ├── infrastructure
    │   └── memory
    └── web
        ├── controller
        ├── dto
        │   ├── adoption
        │   └── shelterandpet
        └── exception

src/main/resources
└── templates

src/test/java
└── adopet
    ├── application
    │   ├── adoption
    │   └── shelterandpet
    ├── domain
    │   ├── adoption
    │   └── shelterandpet
    ├── infrastructure
    │   └── memory
    ├── selenium
    │   └── tests
    └── web
```

---

# Evolução do Projeto

## TP1

Versão inicial com:

* arquitetura em camadas
* interface **console**
* persistência **in-memory**
* separação entre **commands e queries**
* modelagem inicial do domínio
* validações básicas de entrada
* primeiros testes unitários

---

## TP2

Evolução com:

* introdução da **interface web**
* uso de **Spring Boot**
* templates com **Thymeleaf**
* **validação de formulários**
* **tratamento global de erros**
* testes **MVC com MockMvc**
* testes **end-to-end com Selenium**
* **importação e exportação CSV**
* melhoria da organização do código e da separação de responsabilidades

---

## TP3

Criação de um novo sistema CRUD web completo para gerenciamento de **solicitações de adoção**, desenvolvido do zero com foco em robustez, modularidade e testes avançados.

Principais evoluções:

* novo domínio de solicitações de adoção
* CRUD completo de solicitações
* arquitetura modular baseada em `domain / application / gateway / infrastructure / web`
* uso de **Command/Query Separation (CQS)**
* validações de domínio com **Value Objects**
* simulação de serviços externos
* suporte a cenários de falha como timeout e erro de rede
* aplicação de **fail early** e **fail gracefully**
* testes de domínio, application, infraestrutura e web
* testes Selenium
* testes parametrizados
* fuzz testing
* geração de relatório de cobertura com JaCoCo

---

## TP4

Integração e consolidação do sistema em uma aplicação web mais completa, reunindo o gerenciamento de:

* **abrigos**
* **pets**
* **solicitações de adoção**

Principais evoluções desta etapa:

* expansão da modelagem para o contexto `shelterandpet`
* CRUD completo de abrigos
* CRUD completo de pets por abrigo
* manutenção da arquitetura modular com separação por contexto (`adoption` e `shelterandpet`)
* preservação do uso de CQS na camada de aplicação
* reaproveitamento e integração das práticas de robustez desenvolvidas no TP3
* manutenção da persistência em memória para simplicidade e velocidade de testes
* testes automatizados cobrindo o sistema completo
* fluxos Selenium mais completos, incluindo criação de contexto com abrigo + pet + solicitação
* execução headless para pipeline e integração contínua

---

# Considerações Finais

O ADOPET demonstra a construção incremental de um sistema web robusto, testável e bem organizado, aplicando boas práticas de engenharia de software em todas as camadas.

Entre os principais pontos do projeto estão:

* arquitetura modular e coesa
* separação clara entre domínio, aplicação, infraestrutura e web
* validações fortes com Value Objects
* tratamento cuidadoso de erros
* simulação de falhas externas
* interface web funcional
* testes automatizados extensivos
* preocupação real com robustez, previsibilidade e manutenção

Este projeto atende aos objetivos acadêmicos da disciplina ao demonstrar não apenas o funcionamento do sistema, mas também a qualidade da sua construção interna.
