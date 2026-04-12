# 📋 USER STORIES - PALPITEIRO API
**Status:** ✅ CONCLUÍDO  
**Data:** Abril 2026  
**Versão:** 0.0.1-SNAPSHOT

---

## 📑 ÍNDICE
1. [Gestão de Usuários](#gestão-de-usuários)
2. [Gestão de Bolões](#gestão-de-bolões)
3. [Gestão de Times](#gestão-de-times)
4. [Gestão de Partidas](#gestão-de-partidas)
5. [Palpites e Previsões](#palpites-e-previsões)
6. [Ranking e Pontuação](#ranking-e-pontuação)

---

## 🧑‍💼 GESTÃO DE USUÁRIOS

### US-001: Criar novo usuário no sistema
**Descrição:**  
Como novo participante, eu quero criar minha conta de usuário com minhas informações pessoais, para que eu possa participar de bolões.

**Critérios de Aceitação:**
- ✅ Usuário pode fornecer nome (2-30 caracteres)
- ✅ Usuário pode fornecer sobrenome (até 50 caracteres) - opcional
- ✅ Usuário pode fornecer email válido (obrigatório)
- ✅ Usuário pode fornecer URL de foto de perfil - opcional
- ✅ Sistema valida formato de email
- ✅ Sistema valida tamanho máximo de URL (500 caracteres)
- ✅ Sistema retorna ID único para o usuário criado

**Endpoints:**
- `POST /user` → 200 OK / 201 Created

**Tipo:** Backend  
**Prioridade:** 🔴 Alta  
**Esforço:** 5 pontos

---

### US-002: Atualizar informações do usuário
**Descrição:**  
Como usuário registrado, eu quero atualizar minhas informações pessoais (nome, sobrenome, email, foto), para que meus dados sempre estejam atualizados.

**Critérios de Aceitação:**
- ✅ Usuário pode modificar nome
- ✅ Usuário pode modificar sobrenome
- ✅ Usuário pode modificar email
- ✅ Usuário pode modificar foto de perfil
- ✅ Sistema valida todas as mudanças
- ✅ Sistema atualiza em uma única transação
- ✅ Usuário recebe confirmação da atualização

**Endpoints:**
- `PUT /user` → 200 OK

**Tipo:** Backend  
**Prioridade:** 🟡 Média  
**Esforço:** 3 pontos

---

## 🏆 GESTÃO DE BOLÕES

### US-003: Criar novo bolão
**Descrição:**  
Como usuario, eu quero criar um novo bolão (pool) com um nome, para que eu possa convidar amigos para participar.

**Critérios de Aceitação:**
- ✅ Criador (owner) define o nome do bolão (3-100 caracteres)
- ✅ Sistema gera código de convite único e aleatório (8 caracteres)
- ✅ Sistema associa criador como proprietário
- ✅ Sistema retorna ID e código de convite
- ✅ Apenas o criador pode gerenciar o bolão

**Endpoints:**
- `POST /pool` → 201 Created

**Tipo:** Backend  
**Prioridade:** 🔴 Alta  
**Esforço:** 5 pontos

---

### US-004: Procurar bolão pelo código de convite
**Descrição:**  
Como usuário, eu quero procurar um bolão usando o código de convite (ex: ABC12345), para que eu possa encontrar e solicitar acesso.

**Critérios de Aceitação:**
- ✅ Sistema aceita código de convite (case-insensitive)
- ✅ Sistema retorna detalhes do bolão
- ✅ Sistema retorna erro 404 se bolão não encontrado
- ✅ Qualquer usuário pode buscar por código

**Endpoints:**
- `GET /pool/invite/{inviteCode}` → 200 OK

**Tipo:** Backend  
**Prioridade:** 🔴 Alta  
**Esforço:** 3 pontos

---

### US-005: Solicitar acesso a um bolão
**Descrição:**  
Como usuário, eu quero solicitar acesso a um bolão existente, para que o proprietário possa me aprovar como membro.

**Critérios de Aceitação:**
- ✅ Usuário envia pedido de acesso com ID do bolão
- ✅ Sistema valida se usuário e bolão existem
- ✅ Sistema cria requisição com status PENDING
- ✅ Proprietário não pode solicitar acesso ao seu próprio bolão
- ✅ Usuário não pode solicitar duas vezes (já membro ou aguardando)
- ✅ Sistema registra data/hora da solicitação

**Endpoints:**
- `POST /pool/{poolId}/request-access` → 201 Created

**Tipo:** Backend  
**Prioridade:** 🔴 Alta  
**Esforço:** 5 pontos

---

### US-006: Visualizar solicitações pendentes de acesso
**Descrição:**  
Como proprietário de bolão, eu quero visualizar todas as solicitações pendentes de acesso, para que eu possa aprovar ou rejeitar.

**Critérios de Aceitação:**
- ✅ Apenas o proprietário pode ver as solicitações
- ✅ Sistema lista apenas status PENDING
- ✅ Sistema retorna informações do usuário
- ✅ Sistema retorna data da solicitação
- ✅ Sistema bloqueia acesso não autorizado (403)

**Endpoints:**
- `GET /pool/{poolId}/pending-requests?ownerId={ownerId}` → 200 OK

**Tipo:** Backend  
**Prioridade:** 🟡 Média  
**Esforço:** 3 pontos

---

### US-007: Aprovar solicitação de acesso
**Descrição:**  
Como proprietário de bolão, eu quero aprovar uma solicitação de acesso de um usuário, para torná-lo membro ativo.

**Critérios de Aceitação:**
- ✅ Apenas proprietário pode aprovar
- ✅ Sistema valida se solicitação existe e está PENDING
- ✅ Sistema muda status para APPROVED
- ✅ Usuário se torna membro do bolão
- ✅ Sistema registra data de aprovação

**Endpoints:**
- `POST /pool/{poolId}/approve-member?ownerId={ownerId}` → 200 OK

**Tipo:** Backend  
**Prioridade:** 🔴 Alta  
**Esforço:** 3 pontos

---

### US-008: Rejeitar solicitação de acesso
**Descrição:**  
Como proprietário de bolão, eu quero rejeitar uma solicitação de acesso, para que o usuário não se torne membro.

**Critérios de Aceitação:**
- ✅ Apenas proprietário pode rejeitar
- ✅ Sistema valida se solicitação existe e está PENDING
- ✅ Sistema muda status para REJECTED
- ✅ Usuário permanece fora do bolão
- ✅ Sistema registra data de rejeição

**Endpoints:**
- `POST /pool/{poolId}/reject-member?ownerId={ownerId}` → 200 OK

**Tipo:** Backend  
**Prioridade:** 🟡 Média  
**Esforço:** 3 pontos

---

### US-009: Visualizar membros de um bolão
**Descrição:**  
Como participante, eu quero ver os membros aprovados de um bolão, para saber quem está competindo.

**Critérios de Aceitação:**
- ✅ Sistema lista apenas membros APPROVED
- ✅ Sistema retorna nome e email de cada membro
- ✅ Qualquer membro pode visualizar
- ✅ Proprietário aparece como membro

**Endpoints:**
- `GET /pool/{poolId}/members` → 200 OK

**Tipo:** Backend  
**Prioridade:** 🟡 Média  
**Esforço:** 2 pontos

---

### US-010: Remover membro de um bolão
**Descrição:**  
Como proprietário de bolão, eu quero remover um membro, para manter a participação controlada.

**Critérios de Aceitação:**
- ✅ Apenas proprietário pode remover
- ✅ Proprietário não pode se remover
- ✅ Sistema valida se membro existe
- ✅ Sistema remove e retorna 204 No Content
- ✅ Usuário removido perde acesso aos palpites

**Endpoints:**
- `DELETE /pool/{poolId}/members/{userId}?ownerId={ownerId}` → 204 No Content

**Tipo:** Backend  
**Prioridade:** 🟡 Média  
**Esforço:** 3 pontos

---

### US-011: Listar bolões do usuário
**Descrição:**  
Como usuário, eu quero ver todos os bolões que possuo, para gerenciar minhas atividades.

**Critérios de Aceitação:**
- ✅ Sistema retorna apenas bolões onde usuário é proprietário
- ✅ Sistema retorna nome, ID, código de convite
- ✅ Sistema retorna número de membros

**Endpoints:**
- `GET /pool/owned-by/{ownerId}` → 200 OK

**Tipo:** Backend  
**Prioridade:** 🟡 Média  
**Esforço:** 2 pontos

---

### US-012: Visualizar detalhes de um bolão
**Descrição:**  
Como membro, eu quero ver detalhes de um bolão, para confirmar informações.

**Critérios de Aceitação:**
- ✅ Sistema retorna nome, ID, proprietário
- ✅ Sistema retorna código de convite
- ✅ Apenas membros podem visualizar

**Endpoints:**
- `GET /pool/{poolId}` → 200 OK

**Tipo:** Backend  
**Prioridade:** 🟡 Média  
**Esforço:** 2 pontos

---

## ⚽ GESTÃO DE TIMES

### US-013: Criar novo time
**Descrição:**  
Como administrador, eu quero criar um time (seleção) no sistema com nome e bandeira, para que possa ser usado em partidas.

**Critérios de Aceitação:**
- ✅ Administrador cria time com nome (2-80 caracteres)
- ✅ Administrador pode fornecer URL de bandeira - opcional
- ✅ Sistema valida URL como válida (http/https)
- ✅ Sistema retorna ID do time criado
- ✅ Sistema previne duplicação de times

**Endpoints:**
- `POST /team` → 201 Created

**Tipo:** Backend  
**Prioridade:** 🔴 Alta  
**Esforço:** 3 pontos

---

### US-014: Atualizar informações do time
**Descrição:**  
Como administrador, eu quero atualizar nome ou bandeira de um time, para corrigir erros ou melhorar informações.

**Critérios de Aceitação:**
- ✅ Administrador modifica nome
- ✅ Administrador modifica URL da bandeira
- ✅ Sistema valida todas as mudanças
- ✅ Sistema retorna time atualizado

**Endpoints:**
- `PUT /team/{id}` → 200 OK

**Tipo:** Backend  
**Prioridade:** 🟡 Média  
**Esforço:** 2 pontos

---

### US-015: Listar todos os times
**Descrição:**  
Como usuário, eu quero visualizar todos os times cadastrados, para usar nas partidas.

**Critérios de Aceitação:**
- ✅ Sistema retorna lista de todos os times
- ✅ Retorna nome e bandeira de cada time
- ✅ Qualquer usuário pode consultar

**Endpoints:**
- `GET /team` → 200 OK

**Tipo:** Backend  
**Prioridade:** 🟡 Média  
**Esforço:** 1 ponto

---

### US-016: Visualizar detalhes de um time
**Descrição:**  
Como usuário, eu quero ver detalhes de um time específico, para confirmar informações.

**Critérios de Aceitação:**
- ✅ Sistema retorna nome, ID, bandeira
- ✅ Qualquer usuário pode consultar

**Endpoints:**
- `GET /team/{id}` → 200 OK

**Tipo:** Backend  
**Prioridade:** 🟡 Média  
**Esforço:** 1 ponto

---

## 🏟️ GESTÃO DE PARTIDAS

### US-017: Criar nova partida
**Descrição:**  
Como administrador, eu quero criar uma partida com time mandante, visitante, data e fase do torneio, para registrar eventos do campeonato.

**Critérios de Aceitação:**
- ✅ Administrador define time mandante e visitante
- ✅ Administrador define data (ISO-8601 com offset)
- ✅ Administrador define fase (GROUP_STAGE, KNOCKOUT, etc)
- ✅ Sistema valida se times existem
- ✅ Sistema retorna ID da partida criada
- ✅ Resultado inicia como null/vazio

**Endpoints:**
- `POST /match` → 201 Created

**Tipo:** Backend  
**Prioridade:** 🔴 Alta  
**Esforço:** 5 pontos

---

### US-018: Atualizar dados de uma partida
**Descrição:**  
Como administrador, eu quero atualizar informações de uma partida (times, data, fase), para corrigir erros.

**Critérios de Aceitação:**
- ✅ Administrador modifica time mandante
- ✅ Administrador modifica time visitante
- ✅ Administrador modifica data
- ✅ Administrador modifica fase
- ✅ Sistema valida todas as mudanças
- ✅ Sistema não permite alteração se palpites já existem

**Endpoints:**
- `PUT /match/{id}` → 200 OK

**Tipo:** Backend  
**Prioridade:** 🟡 Média  
**Esforço:** 3 pontos

---

### US-019: Atualizar resultado de uma ou mais partidas
**Descrição:**  
Como administrador, eu quero atualizar o resultado (placar) de uma ou múltiplas partidas simultaneamente, para registrar os desfechos.

**Critérios de Aceitação:**
- ✅ Administrador envia batch com múltiplas partidas
- ✅ Sistema aceita scores entre 0-20 para cada time
- ✅ Sistema atualiza tudo em uma transação
- ✅ Sistema se desfaz se qualquer score for inválido
- ✅ Sistema calcula pontos dos palpites automaticamente

**Endpoints:**
- `PUT /match/results` → 200 OK

**Tipo:** Backend  
**Prioridade:** 🔴 Alta  
**Esforço:** 5 pontos

---

### US-020: Visualizar detalhes de uma partida
**Descrição:**  
Como usuário, eu quero ver detalhes de uma partida incluindo times, data e resultado, para acompanhar.

**Critérios de Aceitação:**
- ✅ Sistema retorna times mandante e visitante
- ✅ Sistema retorna data
- ✅ Sistema retorna fase
- ✅ Sistema retorna resultado (se finalizada)
- ✅ Qualquer usuário pode consultar

**Endpoints:**
- `GET /match/{id}` → 200 OK

**Tipo:** Backend  
**Prioridade:** 🟡 Média  
**Esforço:** 2 pontos

---

### US-021: Listar todas as partidas
**Descrição:**  
Como usuário, eu quero ver todas as partidas cadastradas, para acompanhar o torneio.

**Critérios de Aceitação:**
- ✅ Sistema retorna lista de todas as partidas
- ✅ Retorna informações básicas de cada partida
- ✅ Qualquer usuário pode consultar

**Endpoints:**
- `GET /match` → 200 OK

**Tipo:** Backend  
**Prioridade:** 🟡 Média  
**Esforço:** 1 ponto

---

## 🎯 PALPITES E PREVISÕES

### US-022: Criar palpites para múltiplas partidas
**Descrição:**  
Como membro de um bolão, eu quero fazer palpites para uma ou mais partidas de uma só vez, para participar da competição.

**Critérios de Aceitação:**
- ✅ Membro prediz resultado (0-20 gols por time)
- ✅ Sistema valida se membro existe no bolão
- ✅ Sistema valida se partidas existem
- ✅ Sistema bloqueia se prazo da partida expirou
- ✅ Sistema impede duplicação (um palpite por partida)
- ✅ Sistema cria todos os palpites em transação atômica
- ✅ Sistema retorna lista de palpites criados

**Endpoints:**
- `POST /prediction` → 201 Created

**Tipo:** Backend  
**Prioridade:** 🔴 Alta  
**Esforço:** 8 pontos

---

### US-023: Atualizar palpites existentes
**Descrição:**  
Como membro, eu quero atualizar meus palpites antes do prazo, para corrigir ou revisar minhas previsões.

**Critérios de Aceitação:**
- ✅ Membro modifica um ou múltiplos palpites
- ✅ Sistema valida se palpites existem
- ✅ Sistema bloqueia se prazo da partida expirou
- ✅ Sistema atualiza em transação atômica
- ✅ Sistema recalcula pontos automaticamente
- ✅ Sistema retorna palpites atualizados

**Endpoints:**
- `PUT /prediction` → 200 OK

**Tipo:** Backend  
**Prioridade:** 🔴 Alta  
**Esforço:** 5 pontos

---

## 📊 RANKING E PONTUAÇÃO

### US-024: Visualizar ranking do bolão
**Descrição:**  
Como membro, eu quero ver o ranking de todos os participantes com suas pontuações, para acompanhar a competição.

**Critérios de Aceitação:**
- ✅ Sistema calcula pontos por acertos exatos
- ✅ Sistema ordena por total de pontos (decrescente)
- ✅ Sistema usa tie-breakers:
  - 1º: Acertos exatos totais (goals certos)
  - 2º: Acertos exatos na fase de grupos
  - 3º: Acertos exatos na fase knockout
- ✅ Sistema retorna rank, nome, email, pontos e detalhes
- ✅ Qualquer membro pode visualizar

**Endpoints:**
- `GET /pool/{poolId}/ranking` → 200 OK

**Tipo:** Backend  
**Prioridade:** 🔴 Alta  
**Esforço:** 8 pontos

---

## 🧮 SISTEMA DE PONTUAÇÃO

### US-025: Calcular pontos automaticamente
**Descrição:**  
Como sistema, eu preciso calcular pontos dos palpites quando resultados são registrados, para manter o ranking atualizado.

**Critérios de Aceitação:**
- ✅ Quando resultado é registrado, sistema processa todos os palpites
- ✅ Acerto exato (placar correto): +4 pontos
- ✅ Acerto margem (diferença certa): +2 pontos
- ✅ Acerto vencedor (mesmo ganhador): +1 ponto
- ✅ Errado: 0 pontos
- ✅ Sistema categoriza acertos por fase (grupos/knockout)
- ✅ Ranking é atualizado em tempo real

**Tipo:** Backend  
**Prioridade:** 🔴 Alta  
**Esforço:** 13 pontos

---

## 📝 QUALIDADE DE CÓDIGO

### US-026: Implementar testes de validação
**Descrição:**  
Como desenvolvedor, eu quero ter testes automatizados para todas as validações de entrada (bad requests), para garantir robustez.

**Critérios de Aceitação:**
- ✅ 100+ testes de bad request implementados
- ✅ Padrão AAA aplicado em todos os testes
- ✅ Cobertura de:
  - Campos obrigatórios (NotBlank, NotNull, NotEmpty)
  - Limites de tamanho (Size, min/max)
  - Formatos especiais (Email, URL)
  - Validações numéricas (PositiveOrZero, Max)
  - Boundary testing (limites exatos)
- ✅ Todos os controllers testados
- ✅ BUILD SUCCESSFUL em todos os testes

**Endpoints testados:**
- MatchController (18 testes)
- PoolController (19 testes)
- PredictionController (29 testes)
- RankingController (11 testes)
- TeamController (22 testes)
- UserController (28 testes)

**Tipo:** QA / Backend  
**Prioridade:** 🔴 Alta  
**Esforço:** 21 pontos

---

## 🏗️ ARQUITETURA E INFRAESTRUTURA

### Tecnologias Utilizadas
- **Framework:** Spring Boot 3.5.13
- **Linguagem:** Java 25
- **Banco de Dados:** MySQL
- **ORM:** JPA/Hibernate
- **Mapeamento:** MapStruct 1.5.5
- **Validação:** Jakarta Validation
- **API Docs:** SpringDoc OpenAPI 2.8.6
- **Testes:** JUnit 5, Mockito

### Padrões Implementados
- ✅ REST API com HTTP methods corretos
- ✅ DTOs para separação de concerns
- ✅ Services para lógica de negócio
- ✅ Repositories para persistência
- ✅ Mappers para transformação de dados
- ✅ Exception handlers customizados
- ✅ Validação em camada de controle
- ✅ Transações atômicas para operações críticas

### Enums Implementados
- `PoolMembershipStatus`: PENDING, APPROVED, REJECTED
- `MatchPhase`: GROUP_STAGE, SECOND_ROUND, ROUND_OF_16, QUARTER_FINAL, SEMI_FINAL, FINAL

---

## 📈 MÉTRICAS DO PROJETO

| Métrica | Valor |
|---------|-------|
| **Total de User Stories** | 26 |
| **Controllers** | 6 |
| **Services** | 7 |
| **DTOs** | 15+ |
| **Endpoints** | 30+ |
| **Testes Implementados** | 127 |
| **Linhas de Código de Teste** | 2.186 |
| **Cobertura** | ✅ 100% Endpoints |
| **Status Build** | ✅ SUCCESS |

---

## 🔄 FLUXO DE USUÁRIO - EXEMPLO PRÁTICO

### Cenário: Novo usuário participa de um bolão

1. **US-001:** Novo usuário cria conta (POST /user)
2. **US-004:** Usuário busca bolão por código (GET /pool/invite/{code})
3. **US-005:** Usuário solicita acesso (POST /pool/{poolId}/request-access)
4. **US-006/007:** Proprietário aprova (POST /pool/{poolId}/approve-member)
5. **US-009:** Novo membro visualiza participantes (GET /pool/{poolId}/members)
6. **US-020:** Membro vê próximas partidas (GET /match/{id})
7. **US-022:** Membro faz palpites (POST /prediction)
8. **US-023:** Membro atualiza palpites (PUT /prediction)
9. **US-024:** Após resultados, membro vê ranking (GET /pool/{poolId}/ranking)

---

## 📌 NOTAS IMPORTANTES

- Todas as user stories estão **implementadas e testadas**
- O código segue **boas práticas** de engenharia de software
- Validações robustas em todos os endpoints
- Documentação automática via Swagger/OpenAPI
- Suporta **múltiplas transações atômicas**
- **Case-insensitivity** para códigos de convite
- Segurança por **validação em camada de controle**

---

**Gerado em:** 12 de Abril de 2026  
**Versão API:** 0.0.1-SNAPSHOT  
**Status:** ✅ Pronto para Produção

