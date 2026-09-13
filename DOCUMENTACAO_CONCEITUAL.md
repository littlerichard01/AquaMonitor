# Documentação Conceitual do Projeto

ERP para lojas de aquarismo com monitoramento IoT via ESP32 + MQTT. AquaMonitor

## 1. Objetivo geral

Auxiliar lojas de aquarismo no gerenciamento e monitoramento de seus aquários, criaturas, compradores e vendas.
O sistema:

- Centraliza informações sobre aquários, espécies, espécimes, compradores e vendas;
- Registra (não executa) operações de venda — não funciona como plataforma de e-commerce;
- Monitora temperatura da água dos aquários em tempo real através de dispositivos ESP32 + MQTT;
- Mantém histórico de medições e exibe alertas configuráveis com severidade visual;
- Permite definir programações de alimentação;
- Exibe um dashboard principal com visão geral da loja (temperaturas atuais, itens disponíveis, alertas).

Cada loja possui **uma única conta de acesso**. O isolamento dos dados é resolvido pelo grafo de relacionamentos (Loja → Aquário → demais entidades), não por campos redundantes em todas as tabelas.

## 2. Stack tecnológica confirmada

| Camada | Tecnologia |
|---|---|
| Frontend (Mobile, Web, Desktop) | Kotlin Multiplatform + Compose Multiplatform + Material3 |
| Backend | Kotlin + Spring Boot 4.1.x |
| Persistência | Spring Data JPA + Hibernate |
| Banco de dados | PostgreSQL serverless (Neon.tech na nuvem, `sslmode=require`) |
| Comunicação API | REST com autenticação JWT |
| IoT | Dispositivos ESP32 enviam leituras via protocolo MQTT |
| Build | Gradle (Kotlin DSL), wrappers independentes para frontend e backend |
| Porta do backend | 8081 |

## 3. Modelagem — Metodologia Relacional Correta

> **Decisão arquitetural (06/09/2026):** Rejeitada a abordagem original do PDF que possuía `usuario_id` (denormalização/coluna redundante) em todas as entidades.
> O modelo agora segue a metodologia relacional correta, com relacionamentos através de chaves estrangeiras e cardinalidades apropriadas.
> A entidade "Usuário" do PDF foi renomeada semanticamente para **Loja**, pois cada loja tem exatamente uma conta de acesso.

### Convenção de nomenclatura

Nomes de **arquivos, classes, atributos e tabelas são em ASCII (sem acentos)**.
Acentos só aparecem em textos de UI, labels em DTOs de exibição e mensagens.

Exemplos: `Loja`, `Aquario`, `Especie`, `Especime`, `Comprador`, `Alerta`, `ConfiguracaoTemperatura`, `ProgramacaoAlimentacao`, `LeituraTemperatura`, `DispositivoIoT`.

---

## 4. Entidades, campos e significado

Todas as entidades possuem implicitamente:
- `id` (PK, Long, auto-incremento);
- `criado_em` (Instant / LocalDateTime, não nulo);
- `atualizado_em` (Instant / LocalDateTime, não nulo).

### 4.1 Loja

Conta de acesso de uma loja. 1 loja = 1 credencial.

| Atributo | Tipo | Regras |
|---|---|---|
| nome | String (não nulo) | Nome do responsável ou identificação da loja |
| email | String (UNIQUE, não nulo) | E-mail usado para autenticação |
| senha | String (não nulo) | Senha armazenada criptografada com BCrypt |
| ativo | Boolean (não nulo) | Indica se a conta está habilitada |

---

### 4.2 Aquario

Entidade central do sistema — representa um tanque da loja.

| Atributo | Tipo | Regras |
|---|---|---|
| nome | String (não nulo) | Ex.: "Aquário 01" |
| descricao | String | Campo opcional |
| volume_litros | Int / BigDecimal | Capacidade em litros |
| tipo | Enum / String | Ex.: AGUA_DOCE, AGUA_SALGADA |
| localizacao | String | Onde está na loja, ex.: "Corredor 3" |
| status | Enum | **ATIVO** (em uso / monitorado) · **INATIVO** (disponível p/ venda) · **VENDIDO** |
| preco | BigDecimal | Preço caso a loja queira vender o aquário |

---

### 4.3 Especie

Cadastro de "tipo de criatura".

| Atributo | Tipo | Regras |
|---|---|---|
| nome_popular | String (não nulo) | Ex.: "Peixe-palhaço" |
| nome_cientifico | String | Ex.: "Amphiprion ocellaris" |
| tipo | Enum / String | PEIXE, CRUSTACEO, MOLUSCO, CNIDARIO, OUTRO |
| descricao | String | Informações gerais |
| temperatura_minima | BigDecimal | Limite recomendado para a espécie |
| temperatura_maxima | BigDecimal | Limite recomendado para a espécie |

---

### 4.4 Especime

Uma **criatura individual** pertencente à loja.

| Atributo | Tipo | Regras |
|---|---|---|
| identificacao | String (UNIQUE, não nulo) | Ex.: "PEIXE-034" |
| preco | BigDecimal (não nulo) | Valor de venda |
| status | Enum | **DISPONIVEL** · **VENDIDO** · **INDISPONIVEL** |
| observacoes | String | Info específica do indivíduo |
| data_entrada | LocalDate (não nulo) | Quando entrou na loja |
| especie_id | FK → Especie (não nulo) | Obrigatório: todo espécime pertence a uma espécie |
| aquario_id | FK → Aquario | **Pode ser NULL** se ainda não alocado |

---

### 4.5 Comprador

Pessoas que compram espécimes ou aquários da loja.

| Atributo | Tipo | Regras |
|---|---|---|
| nome | String (não nulo) | Nome completo |
| cpf | String | Pode ser UNIQUE por contexto de loja |
| email | String | - |
| telefone | String | - |
| observacoes | String | Opcional |

---

### 4.6 Venda

Apenas o **registro** de uma venda já realizada. Não executa pagamento.

| Atributo | Tipo | Regras |
|---|---|---|
| valor | BigDecimal (não nulo) | Valor pelo qual o item foi vendido |
| data_venda | LocalDateTime (não nulo) | Data/hora do registro |
| observacoes | String | Opcional |
| comprador_id | FK → Comprador (não nulo) | Toda venda tem um comprador |
| especime_id | FK → Especime | **Pode ser NULL** se a venda for de aquário |
| aquario_id | FK → Aquario | **Pode ser NULL** se a venda for de espécime |

**Regra XOR rígida (não negociável):**
Uma venda tem **exatamente UM** entre `especime_id` e `aquario_id`.
Nunca os dois ao mesmo tempo, nunca nenhum dos dois.
Efeito colateral obrigatório ao registrar:
- Se `especime_id` → trocar status do espécime para **VENDIDO**;
- Se `aquario_id` → trocar status do aquário para **VENDIDO**.

---

### 4.7 Alerta

Configuração de monitoramento criada pelo usuário.
Representa a **condição que vai gerar avisos**, não o histórico de cada aviso disparado.

| Atributo | Tipo | Regras |
|---|---|---|
| tipo | Enum (não nulo) | **TEMPERATURA** · **ALIMENTACAO** |
| severidade | Enum (não nulo) | **BAIXA** · **MEDIA** · **ALTA** — define a cor/estilo visual no front |
| ativo | Boolean | Se a regra está ligada ou não |
| aquario_id | FK → Aquario (não nulo) | Todo alerta pertence a um aquário |

Regras:
- Tipo `TEMPERATURA` → compara a última `LeituraTemperatura` do aquário com os limites da `ConfiguracaoTemperatura` do mesmo aquário;
- Tipo `ALIMENTACAO` → verifica os horários de `ProgramacaoAlimentacao` do aquário.

---

### 4.8 DispositivoIoT

Um ESP32 físico associado a um aquário.

| Atributo | Tipo | Regras |
|---|---|---|
| nome | String (não nulo) | Ex.: "ESP32 Aquário 03" |
| identificacao | String (UNIQUE, não nulo) | MAC address ou código físico do dispositivo |
| mqtt_topic | String (não nulo) | Tópico MQTT onde o dispositivo publica leituras |
| status | Enum | **ONLINE** · **OFFLINE** · **INATIVO** |
| ultima_comunicacao | LocalDateTime | Atualizado automaticamente pelo backend ao receber mensagem MQTT |
| aquario_id | FK → Aquario | Aquário monitorado |

---

### 4.9 ConfiguracaoTemperatura

Limites aceitáveis de temperatura para um aquário.

| Atributo | Tipo | Regras |
|---|---|---|
| temperatura_minima | BigDecimal (não nulo) | Limite inferior |
| temperatura_maxima | BigDecimal (não nulo) | Limite superior |
| ativo | Boolean | Se a configuração está ativa |
| aquario_id | FK → Aquario (UNIQUE, não nulo) | 1:1 com Aquário |

---

### 4.10 ProgramacaoAlimentacao

Horários agendados de alimentação de um aquário. **Não** registra a alimentação feita — só o agendamento.

| Atributo | Tipo | Regras |
|---|---|---|
| horario | String / LocalTime (não nulo) | Ex.: "08:00" |
| descricao | String | Ex.: "Alimentação da manhã" |
| ativo | Boolean | - |
| aquario_id | FK → Aquario (não nulo) | 1 aquário → N programações |

---

### 4.11 LeituraTemperatura

Medição automática recebida do ESP32 via MQTT.
**Não é um CRUD de usuário.** O backend cria esses registros automaticamente ao receber mensagens no tópico MQTT.

| Atributo | Tipo | Regras |
|---|---|---|
| temperatura | BigDecimal (não nulo) | Valor medido pelo sensor |
| data_hora | LocalDateTime (não nulo) | Momento da leitura |
| aquario_id | FK → Aquario (não nulo) | De qual aquário é a leitura |
| dispositivo_iot_id | FK → DispositivoIoT (não nulo) | De qual dispositivo veio a leitura |

---

## 5. Diagrama de cardinalidades (resumo textual)

Notação: `Lado Esquerdo  (Cardinalidade)  :  (Cardinalidade)  Lado Direito`

```
Loja  1  :  1..N  Aquario
Loja  1  :  1..N  Especime
Loja  1  :  0..N  Venda
Loja  0..N  :  0..N  Comprador   (muitas lojas compartilham ou não compradores)

Aquario  1  :  1  LeituraTemperatura      (a "atual" / última do aquário; histórico é todas)
Aquario  1  :  1..N  Alerta                (um aquário tem múltiplas regras de alerta)
Aquario  1  :  1  ConfiguracaoTemperatura  (1:1 exata)
Aquario  0..N  :  0..1  Venda              (aquário pode ser vendido 0 ou 1 vez)
Aquario  1..N  :  1  Loja                  (vários aquários pertencem a uma loja)
Aquario  1  :  1..N  Especime              (vários espécimes num aquário)
Aquario  1  :  1..N  ProgramacaoAlimentacao

LeituraTemperatura  1  :  1  Aquario
LeituraTemperatura  1  :  1  DispositivoIoT

Alerta  1..N  :  1  Aquario

ConfiguracaoTemperatura  1  :  1  Aquario

Venda  0..1  :  0..N  Aquario
Venda  0..N  :  1  Comprador
Venda  1  :  0..N  Especime
Venda  0..N  :  1  Loja

Especime  1..N  :  1  Aquario
Especime  1..N  :  1  Especie
Especime  0..N  :  1  Venda
Especime  1..N  :  1  Loja

ProgramacaoAlimentacao  1..N  :  1  Aquario

Especie  1  :  1..N  Especime

Comprador  1  :  0..N  Venda
Comprador  0..N  :  1..N  Loja

DispositivoIoT  1  :  1  LeituraTemperatura
```

---

## 6. Regras de negócio relevantes (extraídas do PDF e mantidas)

1. **Isolamento por loja:** uma loja só enxerga os próprios aquários, espécies, espécimes, compradores, vendas e alertas.
2. **Status do aquário:**
   - `ATIVO` → aparece no monitoramento e no dashboard;
   - `INATIVO` → aparece como disponível para venda;
   - `VENDIDO` → não aparece mais nas listagens.
3. **Status do espécime:**
   - `DISPONIVEL` → pode ser escolhido em uma nova venda;
   - `VENDIDO` → não volta a ser editável por edição comum de tela;
   - `INDISPONIVEL` → reservado ou em tratamento.
4. **ProgramacaoAlimentacao e Alerta de ALIMENTACAO são entidades distintas:**
   - a Programacao só cadastra o horário;
   - o Alerta define se aquele horário vai gerar notificação visual e com qual severidade.
5. **Alertas de TEMPERATURA** não armazenam limites próprios — sempre reutilizam `ConfiguracaoTemperatura` do aquário dono.
6. **LeituraTemperatura é append-only:** não se edita nem se exclui medição recebida do ESP32; serve para gráfico de histórico e temperatura atual (último registro).
7. **A severidade do Alerta (BAIXA / MÉDIA / ALTA) influencia diretamente a UI do dashboard e da tela Detalhes do Aquário.**

---

## 7. Comunicação backend ↔ IoT

- Dispositivos ESP32 publicam em tópicos MQTT (um por dispositivo, campo `mqtt_topic` da entidade DispositivoIoT);
- Backend consome esses tópicos, identifica o dispositivo, cria um registro `LeituraTemperatura`, atualiza `ultima_comunicacao` e `status` do dispositivo;
- Após cada nova leitura, o backend pode opcionalmente verificar `ConfiguracaoTemperatura` + `Alerta` do aquário associado para disparar notificações.

---

## 8. Convenções de desenvolvimento

- Arquivos de entidades JPA em `backend/src/main/kotlin/com/example/backend/entity/`;
- Interfaces JpaRepository em `backend/src/main/kotlin/com/example/backend/repository/`;
- Regras de negócio em `backend/src/main/kotlin/com/example/backend/service/`;
- Endpoints REST em `backend/src/main/kotlin/com/example/backend/controller/`;
- Objetos Request/Response JSON em `backend/src/main/kotlin/com/example/backend/dto/`;
- Beans de infraestrutura (CORS, JWT, Swagger, config MQTT) em `backend/src/main/kotlin/com/example/backend/config/`;
- Exceptions customizadas e `@ControllerAdvice` global em `backend/src/main/kotlin/com/example/backend/exception/`;
- Nome de tabelas em plural no banco, ex.: `lojas`, `aquarios`, `especies`, `especimes`, `compradores`, `vendas`, `alertas`, `dispositivos_iot`, `configuracoes_temperatura`, `programacoes_alimentacao`, `leituras_temperatura`;
- Senhas sempre usando BCrypt antes de persistir;
- ddl-auto = update durante a fase de protótipo;
