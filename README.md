<div align="center">

# DDD Shield

**Bloqueio inteligente de chamadas por DDD**

[**Baixar APK (v1.1.1)**](https://github.com/eduardokryon/DDD-shield/releases/latest)

</div>

---

## Sobre

DDD Shield e um aplicativo Android que bloqueia automaticamente chamadas telefonicas de DDDs brasileiros selecionados pelo usuario. Construido exclusivamente com a API oficial `CallScreeningService` do Android — sem servicos em primeiro plano, sem hacks de acessibilidade, sem drenagem de bateria.

### Por que?

O Brasil recebe milhoes de chamadas indesejadas diariamente — telemarketing, golpes, cobranca. O DDD Shield devolve ao usuario o controle de quais regiões podem alcancar seu telefone.

### Funcionalidades

- Bloqueio de chamadas de qualquer combinacao dos 67 DDDs brasileiros
- Bloqueio de numeros especificos (mesmo que estejam na agenda)
- Lista de exceções — permita numeros de DDDs bloqueados
- Protecao de contatos (por padrao)
- Design Material You com cores dinamicas e modo escuro
- Zero uso de bateria em segundo plano

---

## Hierarquia de Bloqueio

O DDD Shield utiliza um sistema de prioridades para determinar se uma chamada deve ser bloqueada:

```
┌─────────────────────────────────────────────────────┐
│  1. EXCEÇÕES (whitelist)                            │
│     → SEMPRE permitidos                             │
│     → Mesmo com DDD bloqueado                       │
├─────────────────────────────────────────────────────┤
│  2. NÚMEROS BLOQUEADOS                              │
│     → SEMPRE bloqueados                             │
│     → Mesmo que estejam na agenda de contatos       │
├─────────────────────────────────────────────────────┤
│  3. CONTATOS DA AGENDA                              │
│     → Permitidos por padrao                         │
│     → A menos que bloqueados explicitamente         │
├─────────────────────────────────────────────────────┤
│  4. DDDs BLOQUEADOS                                 │
│     → Bloqueados                                    │
│     → Todos os numeros da regiao                    │
└─────────────────────────────────────────────────────┘
```

### Exemplos Praticos

| Cenario | Resultado |
|---------|-----------|
| DDD 88 bloqueado | Todos de 88 bloqueados |
| DDD 88 bloqueado + 088 99999-9999 na exceção | 088 99999-9999 pode ligar |
| Contato "Mãe" com 088 99999-9999 + DDD 88 bloqueado | Mãe não liga |
| Contato "Mãe" + 088 99999-9999 na exceção + DDD 88 bloqueado | Mãe pode ligar |
| Contato "Mãe" + 088 99999-9999 na lista de bloqueados | Mãe não liga (mesmo na agenda) |

### Resumo

- **Quer bloquear uma regiao inteira?** → Bloqueie o DDD
- **Quer bloquear uma pessoa especifica?** → Bloqueie o numero (mesmo na agenda)
- **Quer permitir um numero de uma regiao bloqueada?** → Adicione na exceção
- **Contatos sao protegidos?** → Sim, por padrao (mas podem ser bloqueados)

---

## Como Funciona

O DDD Shield utiliza o `CallScreeningService` do Android, uma API de nivel do sistema que intercepta chamadas recebidas antes que toquem. Quando uma chamada chega:

1. Verifica se o numero esta na lista de exceções (se sim, libera)
2. Verifica se o numero esta bloqueado explicitamente (se sim, bloqueia)
3. Verifica se o numero esta nos contatos (se sim, libera)
4. Verifica o DDD (se bloqueado, rejeita)

Essa abordagem e:
- **Eficiente em bateria** — sem servicos persistentes
- **Confiavel** — funciona apos reinicializacoes sem reconfiguracao
- **Nao invasiva** — usa apenas APIs oficiais do Android

---

## Requisitos

- Android 8.0 (API 26) ou superior
- Permissao de Call Screening (concedida na primeira abertura)
- Permissao de contatos (opcional, para protecao de contatos)

---

## Instalacao

### Download

Baixe o APK mais recente em [Releases](https://github.com/eduardokryon/DDD-shield/releases/latest).

### Configuracao

1. Ative "Fontes desconhecidas" para o seu gerenciador de arquivos/navegador
2. Instale o APK
3. Abra o DDD Shield — ele ira solicitar as permissoes necessarias
4. Defina o DDD Shield como seu app padrao de Call Screening quando solicitado
5. Leia e aceite as instrucoes no popup de boas-vindas

Se o aviso nao aparecer:

```
Configuracoes → Apps → Apps padrao → App de bloqueio de chamadas → DDD Shield
```

### Uso

1. Abra o DDD Shield
2. Leia o popup de instrucoes e marque que leu
3. Na aba **Início**, selecione os DDDs que deseja bloquear
4. Ative o bloqueador
5. Na aba **Números**, bloqueie numeros especificos ou adicione exceções
6. Pronto — chamadas serao processadas conforme a hierarquia

---

## Arquitetura

Clean Architecture com padrao MVVM.

```
┌─────────────────────────────────────────────┐
│              Camada de UI                    │
│   Screens ─ Components ─ ViewModels (Flow)   │
├─────────────────────────────────────────────┤
│            Camada de Dominio                 │
│        Use Cases ─ Repository Interface       │
├─────────────────────────────────────────────┤
│              Camada de Dados                 │
│      Repository Impl ─ DataStore             │
├─────────────────────────────────────────────┤
│            Camada de Servico                 │
│           CallScreeningService               │
└─────────────────────────────────────────────┘
```

### Principios

- **SOLID** — responsabilidade unica, inversao de dependencia
- **DRY** — componentes reutilizaveis, repositorio de DDDs centralizado
- **KISS** — abstracoes minimas, logica direta

---

## Stack Tecnica

| Componente | Tecnologia |
|------------|------------|
| Linguagem | Kotlin 1.9.22 |
| UI | Jetpack Compose + Material 3 |
| Navegacao | Navigation Compose |
| Persistencia | DataStore Preferences |
| Assincronia | Coroutines + StateFlow |
| Arquitetura | MVVM + Clean Architecture |
| Build | Gradle 8.4 + Version Catalog |

---

## Estrutura do Projeto

```
app/src/main/java/com/dddlock/
├── AppContainer.kt            # Injecao de dependencias
├── DDD ShieldApplication.kt      # Ponto de entrada da Application
├── MainActivity.kt            # Activity principal + navegacao
├── model/                     # Modelos de dados
├── data/                      # Repositorio + DataStore
├── domain/                    # Use cases + interfaces
├── service/                   # CallScreeningService
├── navigation/                # Grafo de navegacao
└── ui/                        # Screens, components, theme
```

---

## Compilando a Partir do Codigo

### Pre-requisitos

- Android Studio Hedgehog (2023.1.1) ou mais recente
- JDK 17
- Android SDK 34

### Build

```bash
git clone https://github.com/eduardokryon/DDD-shield.git
cd DDD-shield
./gradlew assembleDebug
```

APK gerado em: `app/build/outputs/apk/debug/app-debug.apk`

### Build Release

```bash
./gradlew assembleRelease
```

> Builds release exigem uma chave de assinatura. Configure no arquivo `app/build.gradle.kts`.

---

## Permissoes

| Permissao | Finalidade |
|-----------|------------|
| `CALL_PHONE` | Necessaria para o CallScreeningService |
| `READ_CONTACTS` | Protege contatos de serem bloqueados |
| `POST_NOTIFICATIONS` | Notificacoes (Android 13+) |

---

## Limitacoes Conhecidas

- **Chamadas VoIP** (WhatsApp, Telegram) nao sao afetadas — apenas chamadas cellulares
- **Android 8.0+** obrigatorio — CallScreeningService indisponivel em versoes anteriores
- **Variacoes de fabricantes** — alguns OEMs (Xiaomi, Samsung) podem modificar o comportamento do call screening
- **Primeira campainha** — a chamada pode tocar uma vez antes de ser rejeitada, dependendo do dispositivo

---

## Roadmap

- [ ] Estatisticas e historico de bloqueios
- [ ] Exportar/importar configuracoes
- [ ] Backup em nuvem
- [ ] Suporte a multiplos idiomas (PT-BR, EN)

---

## Contribuindo

Contribuicoes sao bem-vindas. Abra uma issue primeiro para discutir o que voce gostaria de alterar.

1. Faca fork do repositorio
2. Crie uma branch de feature (`git checkout -b feature/amazing-feature`)
3. Faca commit das suas alteracoes (`git commit -m 'Add amazing feature'`)
4. Push para a branch (`git push origin feature/amazing-feature`)
5. Abra um Pull Request

---

## Licenca

Este projeto esta licenciado sob a MIT License — veja o arquivo [LICENSE](LICENSE) para detalhes.

---

## Autor

**Eduardo Brito** — [@eduardokryon](https://github.com/eduardokryon)

---

<div align="center">
  <sub>Feito com Kotlin e Jetpack Compose</sub>
</div>
