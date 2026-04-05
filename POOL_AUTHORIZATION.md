# Sistema de Autorização de Entrada em Pools

## Visão Geral

Implementamos um sistema de autorização onde o criador (owner) da pool deve aprovar manualmente cada usuário que deseja entrar. Isso permite maior controle sobre quem participa da pool.

## Fluxo de Funcionamento

### 1. Criação da Pool
O owner cria uma nova pool:
```http
POST /pool
Content-Type: application/json

{
  "name": "Minha Pool",
  "ownerId": "550e8400-e29b-41d4-a716-446655440000"
}
```

Resposta:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440001",
  "name": "Minha Pool",
  "ownerId": "550e8400-e29b-41d4-a716-446655440000"
}
```

### 2. Usuário Solicita Entrada
Um usuário solicita acesso à pool:
```http
POST /pool/550e8400-e29b-41d4-a716-446655440001/request-access
Content-Type: application/json

{
  "userId": "550e8400-e29b-41d4-a716-446655440002"
}
```

Resposta (Status PENDING):
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440003",
  "poolId": "550e8400-e29b-41d4-a716-446655440001",
  "userId": "550e8400-e29b-41d4-a716-446655440002",
  "userName": "João Silva",
  "status": "PENDING",
  "requestedAt": "2026-04-05T10:30:00Z",
  "approvedAt": null
}
```

### 3. Owner Visualiza Requisições Pendentes
O owner visualiza todas as requisições pendentes:
```http
GET /pool/550e8400-e29b-41d4-a716-446655440001/pending-requests?ownerId=550e8400-e29b-41d4-a716-446655440000
```

Resposta:
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440003",
    "poolId": "550e8400-e29b-41d4-a716-446655440001",
    "userId": "550e8400-e29b-41d4-a716-446655440002",
    "userName": "João Silva",
    "status": "PENDING",
    "requestedAt": "2026-04-05T10:30:00Z",
    "approvedAt": null
  }
]
```

### 4. Owner Aprova ou Rejeita
Owner aprova:
```http
POST /pool/550e8400-e29b-41d4-a716-446655440001/approve-member?ownerId=550e8400-e29b-41d4-a716-446655440000
Content-Type: application/json

{
  "userId": "550e8400-e29b-41d4-a716-446655440002"
}
```

Resposta (Status APPROVED):
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440003",
  "poolId": "550e8400-e29b-41d4-a716-446655440001",
  "userId": "550e8400-e29b-41d4-a716-446655440002",
  "userName": "João Silva",
  "status": "APPROVED",
  "requestedAt": "2026-04-05T10:30:00Z",
  "approvedAt": "2026-04-05T10:35:00Z"
}
```

Ou rejeita:
```http
POST /pool/550e8400-e29b-41d4-a716-446655440001/reject-member?ownerId=550e8400-e29b-41d4-a716-446655440000
Content-Type: application/json

{
  "userId": "550e8400-e29b-41d4-a716-446655440002"
}
```

### 5. Visualizar Membros Aprovados
```http
GET /pool/550e8400-e29b-41d4-a716-446655440001/members
```

Resposta:
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440003",
    "poolId": "550e8400-e29b-41d4-a716-446655440001",
    "userId": "550e8400-e29b-41d4-a716-446655440002",
    "userName": "João Silva",
    "status": "APPROVED",
    "requestedAt": "2026-04-05T10:30:00Z",
    "approvedAt": "2026-04-05T10:35:00Z"
  }
]
```

## Validações e Regras

1. **Membresia Única**: Um usuário só pode ter uma requisição/aprovação por pool
2. **Only Owner Approves**: Apenas o owner da pool pode aprovar ou rejeitar requisições
3. **Status Flow**: PENDING → APPROVED ou REJECTED
4. **Predictions**: Usuários só podem fazer palpites se forem:
   - Owner da pool, OU
   - Membros aprovados (status APPROVED)
5. **Ranking**: Apenas owner e membros aprovados aparecem no ranking

## Entidades do Banco de Dados

### pool_memberships
| Coluna | Tipo | Descrição |
|--------|------|-----------|
| id | UUID | Identificador único |
| pool_id | UUID | ID da pool |
| user_id | UUID | ID do usuário |
| status | VARCHAR | PENDING, APPROVED, REJECTED |
| requested_at | TIMESTAMP | Quando foi solicitado |
| approved_at | TIMESTAMP | Quando foi aprovado (null se pending/rejected) |

Constraint: UNIQUE(pool_id, user_id) - garante um registro por user/pool

## Códigos de Erro HTTP

- **400 Bad Request**: Dados inválidos ou usuário já tem requisição
- **403 Forbidden**: Apenas owner pode aprovar/rejeitar/visualizar requisições
- **404 Not Found**: Pool ou usuário não encontrado
- **201 Created**: Requisição criada com sucesso
- **200 OK**: Operação bem-sucedida

## Fluxo de Validação de Palpites

Quando um usuário tenta fazer um palpite:

1. Verifica se usuário existe ✓
2. Verifica se pool existe ✓
3. Verifica se match existe ✓
4. **Verifica se usuário é owner OU membro aprovado** ← Nova validação
5. Verifica se deadline da fase foi respeitado ✓

Se alguma validação falhar, retorna erro apropriado.

