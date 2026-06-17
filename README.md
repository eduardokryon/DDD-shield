# DDDLock

**Controle quais regiões podem ligar para você. Selecione os DDDs desejados e bloqueie chamadas automaticamente.**

DDDLock é um aplicativo Android que permite bloquear automaticamente chamadas telefônicas provenientes de DDDs brasileiros selecionados pelo usuário, utilizando exclusivamente a API oficial `CallScreeningService` do Android — sem necessidade de serviços em foreground, acessibilidade ou hacks.

---

<div align="center">

### [📥 Baixar APK (v1.0.4)](https://github.com/eduardokryon/DDD-shield/releases/latest)

</div>

---

## 📋 Índice

- [Visão Geral](#visão-geral)
- [Arquitetura](#arquitetura)
- [Tecnologias](#tecnologias)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Instalação](#instalação)
- [Como Gerar APK](#como-gerar-apk)
- [Como Atualizar APK](#como-atualizar-apk)
- [Como Ativar o Serviço](#como-ativar-o-serviço)
- [Como Usar](#como-usar)
- [Limitações do Android](#limitações-do-android)
- [Roadmap Futuro](#roadmap-futuro)
- [Créditos](#créditos)

---

## Visão Geral

O DDDLock resolve um problema comum no Brasil: o recebimento de chamadas indesejadas de DDDs específicos (telemarketing, golpes, cobranças). O aplicativo permite que o usuário:

- Escolha quais DDDs brasileiros deseja bloquear
- Bloqueie chamadas automaticamente sem intervenção manual
- Salve configurações localmente com persistência
- Continue funcionando após reinicialização do aparelho
- Funcione sem precisar abrir o aplicativo ou mantê-lo em segundo plano

Tudo isso utilizando **apenas** a API oficial `CallScreeningService`, sem depender de:
- ❌ Foreground Services
- ❌ WorkManager para manter bloqueio
- ❌ AlarmManager
- ❌ Acessibilidade
- ❌ Hacks ou APIs obsoletas

### Proteção de Contatos

Números salvos na sua lista de contatos **não são bloqueados**, mesmo que estejam em um DDD bloqueado. Isso garante que familiares, amigos e contatos importantes nunca sejam impedidos de ligar.

---

## Arquitetura

O projeto segue **Clean Architecture simplificada** com **MVVM**, garantindo separação de responsabilidades, testabilidade e facilidade de manutenção.

```
┌─────────────────────────────────────────────────────────────┐
│                        UI Layer                              │
│   ┌──────────┐  ┌──────────────┐  ┌──────────────────┐     │
│   │ Screens  │  │ Components   │  │    ViewModels    │     │
│   │ (Compose)│  │ (Reusable)   │  │   (StateFlow)    │     │
│   └────┬─────┘  └──────────────┘  └────────┬─────────┘     │
│        │                                    │               │
├────────┼────────────────────────────────────┼───────────────┤
│        │         Domain Layer               │               │
│   ┌────▼────────────────────────────────────▼──────────┐   │
│   │              Use Cases + Repository Interface       │   │
│   └─────────────────────────────────────────────────────┘   │
│                              │                               │
├──────────────────────────────┼───────────────────────────────┤
│         Data Layer           │                               │
│   ┌──────────────────────────▼──────────────────────────┐   │
│   │    Repository Impl + DataStore Preferences           │   │
│   └─────────────────────────────────────────────────────┘   │
│                              │                               │
├──────────────────────────────┼───────────────────────────────┤
│       Service Layer          │                               │
│   ┌──────────────────────────▼──────────────────────────┐   │
│   │           CallScreeningService                       │   │
│   └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### Princípios Aplicados

- **SOLID**: Interfaces segregadas, responsabilidade única, inversão de dependência
- **DRY**: Componentes reutilizáveis, centralização da lista de DDDs
- **KISS**: Simplicidade nas soluções, sem abstrações desnecessárias
- **Clean Code**: Nomes significativos, funções pequenas, código legível

---

## Tecnologias

| Tecnologia | Versão | Finalidade |
|---|---|---|
| **Kotlin** | 1.9.22 | Linguagem principal |
| **Jetpack Compose** | BOM 2024.02 | UI declarativa |
| **Material Design 3** | Compose MD3 | Design System Material You |
| **Navigation Compose** | 2.7.7 | Navegação entre telas |
| **DataStore Preferences** | 1.0.0 | Persistência de configurações |
| **Coroutines + Flow** | 1.8.0 | Assincronia e reatividade |
| **StateFlow** | — | Estado reativo nos ViewModels |
| **CallScreeningService** | Android API | Bloqueio de chamadas |
| **Gradle Version Catalog** | — | Gerenciamento de dependências |

---

## Estrutura do Projeto

```
app/
├── src/main/java/com/dddlock/
│   ├── AppContainer.kt              # Container de dependências (DI manual)
│   ├── DDDLockApplication.kt        # Application class
│   ├── MainActivity.kt              # Activity principal + Bottom Navigation
│   │
│   ├── model/
│   │   └── DDD.kt                   # Data class DDD + lista completa
│   │
│   ├── data/
│   │   ├── local/
│   │   │   └── DDDSettingsDataStore.kt  # DataStore Preferences
│   │   └── repository/
│   │       └── DDDRepositoryImpl.kt    # Implementação do repositório
│   │
│   ├── domain/
│   │   ├── model/
│   │   │   └── BlockerStatus.kt        # Modelo de status do bloqueio
│   │   ├── repository/
│   │   │   └── DDDRepository.kt        # Interface do repositório
│   │   └── usecase/
│   │       ├── GetAllDDDsUseCase.kt
│   │       ├── GetBlockedDDDsUseCase.kt
│   │       ├── GetBlockerStatusUseCase.kt
│   │       ├── SearchDDDsUseCase.kt
│   │       ├── SetBlockerEnabledUseCase.kt
│   │       └── ToggleDDDBlockUseCase.kt
│   │
│   ├── service/
│   │   └── DDDCallScreeningService.kt  # CallScreeningService
│   │
│   ├── navigation/
│   │   ├── Screen.kt                   # Sealed class de telas
│   │   └── DDDNavGraph.kt             # Grafo de navegação
│   │
│   └── ui/
│       ├── theme/
│       │   ├── Color.kt              # Paletas claro/escuro
│       │   ├── Type.kt               # Tipografia
│       │   ├── Shape.kt              # Formas
│       │   └── Theme.kt              # Tema Material You
│       ├── components/
│       │   ├── StatusCard.kt          # Card de status
│       │   ├── DDDSearchBar.kt       # Barra de busca
│       │   └── DDDListItem.kt        # Item de DDD na lista
│       └── screens/
│           ├── home/
│           │   ├── HomeScreen.kt      # Tela principal
│           │   └── HomeViewModel.kt   # ViewModel da Home
│           ├── diagnosis/
│           │   ├── DiagnosisScreen.kt # Tela de diagnóstico
│           │   └── DiagnosisViewModel.kt
│           └── about/
│               └── AboutScreen.kt     # Tela Sobre
│
└── src/main/res/
    ├── values/
    │   ├── strings.xml
    │   ├── colors.xml
    │   └── themes.xml
    ├── drawable/
    │   ├── ic_launcher_foreground.xml
    │   └── ic_launcher_background.xml
    └── mipmap-anydpi-v26/
        ├── ic_launcher.xml
        └── ic_launcher_round.xml
```

---

## Instalação no Dispositivo

### 1. Baixe o APK

Clique no botão acima ou acesse [Releases](https://github.com/eduardokryon/DDD-shield/releases/latest)

### 2. Ative "Fontes desconhecidas"

No seu celular Android:
```
Configurações → Segurança → Fontes desconhecidas → Ative para o navegador/arquivo que você baixou
```

### 3. Instale o APK

- Abra o arquivo `DDDLock-v1.0.0.apk` baixado
- Toque em **Instalar**
- Aguarde a instalação concluir

### 4. Conceda permissões

Abra o DDDLock e ative as permissões solicitadas:
- **Call Screening** (atender/chamar)

### 5. Configure como app padrão

**Opção A — Pelo app:**
1. Abra o DDDLock
2. O app irá solicitar automaticamente a permissão de Call Screening
3. Toque em **"Allow"** quando solicitado

**Opção B — Pelas configurações:**
```
Configurações → Apps → DDDLock → Permissões → Phone → Permitir
```

### 6. Defina como app padrão (se disponível)

```
Configurações → Apps → Apps padrão → Phone app → DDDLock
```

Ou:

```
Configurações → Apps → Apps padrão → Caller ID & spam app → DDDLock
```

> Se nenhuma opção aparecer, não se preocupe — o app funcionará apenas com a permissão de Phone concedida no passo 5.

---

## Compilação (para desenvolvedores)

### Pré-requisitos

- Android Studio Hedgehog (2023.1.1) ou superior
- JDK 17
- Gradle 8.4
- Android SDK 34

### Passos

```bash
# Clone o repositório
git clone https://github.com/eduardokryon/DDD-shield.git

# Abra no Android Studio
cd DDD-shield
./gradlew build
```

Ou abra a pasta diretamente no Android Studio — ele gerenciará as dependências automaticamente.

---

## Como Gerar APK

### Debug

```bash
./gradlew assembleDebug
```

O APK será gerado em: `app/build/outputs/apk/debug/app-debug.apk`

### Release

```bash
./gradlew assembleRelease
```

O APK será gerado em: `app/build/outputs/apk/release/app-release.apk`

> **Nota**: APKs release exigem chave de assinatura. Configure no arquivo `app/build.gradle.kts`:

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("sua-chave.jks")
            storePassword = "senha"
            keyAlias = "alias"
            keyPassword = "senha"
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

---

## Como Atualizar APK

Para publicar uma nova versão:

1. Altere as variáveis no `app/build.gradle.kts`:

```kotlin
defaultConfig {
    versionCode = 2          // Incrementar a cada build
    versionName = "1.0.1"    // Seguir SemVer (1.0.0 → 1.0.1 → 1.1.0 → 2.0.0)
}
```

2. Gere o novo APK: `./gradlew assembleRelease`
3. Distribua o APK atualizado

> **Importante**: Os dados do usuário (DDDs bloqueados, configurações) são preservados durante atualizações, pois são armazenados via DataStore Preferences, que persiste entre versões.

---

## Como Ativar o Serviço

1. Instale o APK no dispositivo
2. Abra o aplicativo
3. Vá em **Ajustes do Android → Apps → DDDLock → Permissões**
4. Conceda a permissão de **"Atender chamadas"** (Call Screening)
5. Volte para o aplicativo e ative o bloqueio

### Configuração Manual Alternativa

Acesse as configurações do telefone:
1. **Configurações → Apps → Aplicativos padrão → App de bloqueio de chamadas**
2. Selecione **DDDLock**

> ⚠️ O sistema pode solicitar a concessão da função de bloqueio de chamadas na primeira ativação.

---

## Como Usar

1. **Tela Inicial**: Veja o status do bloqueador e a lista de DDDs disponíveis
2. **Buscar DDD**: Use a barra de pesquisa para encontrar DDDs por número, cidade ou estado
3. **Selecionar DDDs**: Marque os DDDs que deseja bloquear
4. **Ativar bloqueio**: Toque no botão "Ativar bloqueio" para começar
5. **Diagnóstico**: Verifique se todos os serviços estão funcionando corretamente
6. **Sobre**: Veja informações do aplicativo e do desenvolvedor

---

## Limitações do Android

| Limitação | Descrição |
|---|---|
| **CallScreeningService** | O Android restringe o bloqueio a este serviço. Chamadas via VoIP (WhatsApp, Telegram) não são afetadas. |
| **API 26+** | O `CallScreeningService` está disponível apenas no Android 8.0 (Oreo) ou superior. |
| **Permissão** | O usuário precisa conceder manualmente a permissão de Call Screening. |
| **Callback** | O bloqueio ocorre após o telefone tocar no máximo 1 vez (a critério do fabricante). |
| **Fabricantes** | Algumas fabricantes (Xiaomi, Samsung) podem modificar o comportamento padrão do CallScreeningService. |

---

## Roadmap Futuro

### v1.1.0
- [ ] Lista branca de contatos (não bloquear contatos salvos)
- [ ] Estatísticas de bloqueio

### v1.2.0
- [ ] Exportar/Importar configurações
- [ ] Histórico de chamadas bloqueadas

### v2.0.0
- [ ] Backup local na nuvem
- [ ] Modo noturno automático
- [ ] Suporte a múltiplos idiomas (PT-BR, EN)

---

## Créditos

**Criador:** Eduardo Brito

**GitHub:** [@eduardokryon](https://github.com/eduardokryon)

Projeto idealizado e desenvolvido por Eduardo Brito.

---

<div align="center">
  <sub>DDDLock © 2024 Eduardo Brito. Todos os direitos reservados.</sub>
</div>
