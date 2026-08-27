# Projeto Integrador 6 Semestre

Projeto organizado em duas partes:

- `frontendMultiplataforma`: aplicacao Kotlin Multiplatform com Compose Multiplatform.
- `backend`: diretorio reservado para o backend. Atualmente nao ha arquivos ou configuracao de execucao nele.

O frontend possui os seguintes alvos:

- Android
- Desktop JVM
- Web com Kotlin/Wasm
- Web com Kotlin/JS

---

## Estrutura principal

```text
ProjetoIntegrador6Sem/
|-- README.md
|-- backend/
`-- frontendMultiplataforma/
    |-- androidApp/              Aplicacao Android (entry point)
    |-- desktopApp/              Aplicacao Desktop JVM (entry point)
    |-- shared/                  Codigo e UI compartilhados (aqui fica 90% do app)
    |   `-- src/
    |       |-- commonMain/      Codigo comum a TODAS as plataformas (UI, logica)
    |       |-- androidMain/     Implementacoes especificas de Android
    |       |-- jvmMain/         Implementacoes especificas de Desktop
    |       |-- jsMain/          Implementacoes especificas de Web/JS
    |       |-- wasmJsMain/      Implementacoes especificas de Web/Wasm
    |       `-- ...Test/         Testes por plataforma
    `-- webApp/                  Aplicacoes Web Wasm e JS (entry point)
```

O codigo compartilhado fica principalmente em `frontendMultiplataforma/shared/src/commonMain`.

---

## Estrutura interna do commonMain (onde voce vai codar 95% do front)

```text
shared/src/commonMain/kotlin/projeto/integrador/sexto/
|
|-- App.kt                             # Ponto de entrada UI: tema + NavHost
|
|-- ui/
|   |-- theme/                         # Tema global (cores, fontes, shapes)
|   |   |-- Color.kt                   # Paleta light/dark
|   |   |-- Type.kt                    # Tipografia
|   |   |-- Shape.kt                   # Arredondamentos
|   |   `-- Theme.kt                   # AppTheme() - use em volta de tudo
|   |
|   |-- components/                    # Componentes reutilizaveis
|   |   |-- BotaoPrimario.kt
|   |   |-- CampoTexto.kt
|   |   `-- CardSimples.kt
|   |
|   |-- navigation/                    # Navegacao entre telas (sem libs externas)
|   |   |-- Rotas.kt                   # sealed class com as rotas (tipadas, sem typo)
|   |   `-- NavGraph.kt                # AppNavController + when() que liga rota -> tela
|   |
|   `-- screens/                       # Uma pasta por tela do app
|       |-- login/
|       |   |-- LoginScreen.kt         # Layout da tela (sem logica)
|       |   |-- LoginViewModel.kt      # Estado e logica da tela
|       |   `-- LoginViewModelFactory.kt # Cria o VM (boilerplate)
|       `-- home/
|           |-- HomeScreen.kt
|           |-- HomeViewModel.kt
|           `-- HomeViewModelFactory.kt
|
`-- composeResources/                  # Imagens, strings, fontes compartilhadas
    `-- drawable/
        `-- compose-multiplatform.xml
```

---

## Como criar uma TELA NOVA (passo a passo)

Suponha que voce queira criar a tela de **Perfil** (ex: Perfil do usuario). Siga esses 6 passos e tudo funcionara automaticamente em Android, Desktop e Web.

### Passo 1 - Criar a pasta da tela dentro de `screens/`

```text
shared/src/commonMain/kotlin/projeto/integrador/sexto/ui/screens/perfil/
```

---

### Passo 2 - Criar o `PerfilViewModel.kt` (estado + logica)

Todo estado da tela (texto digitado, carregando, lista de dados etc) fica no ViewModel. **Nunca guarde estado dentro do Screen.**

Copie esse modelo:

```kotlin
package projeto.integrador.sexto.ui.screens.perfil

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

// Nota: por simplicidade, ViewModel aqui eh uma classe Kotlin comum (nao herda de androidx.lifecycle.ViewModel).
// Basta guardar a instancia com remember {} na tela (veja Factory).
class PerfilViewModel {

    // 1. Defina TODO o que a tela precisa saber dentro de UiState.
    data class UiState(
        val nome: String = "",
        val email: String = "",
        val carregando: Boolean = false
    )

    // 2. Variavel observavel: quando muda, a tela se redesenha sozinha.
    var uiState by mutableStateOf(UiState())
        private set

    // 3. Funcoes que a tela chama quando ocorre um evento (botao clicado etc).
    init {
        // Use o bloco init para carregar dados assim que a tela abre.
        carregarPerfil()
    }

    private fun carregarPerfil() {
        uiState = uiState.copy(carregando = true)
        // ... chame repositorio/API aqui ...
        uiState = uiState.copy(
            carregando = false,
            nome = "Maria",
            email = "maria@email.com"
        )
    }
}
```

---

### Passo 3 - Criar o `PerfilViewModelFactory.kt` (boilerplate minimo)

Usamos `remember{}` para guardar a mesma instancia do ViewModel enquanto a tela estiver ativa. Se a tela receber argumentos, passe-os por parametro.

```kotlin
package projeto.integrador.sexto.ui.screens.perfil

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun lembrarPerfilViewModel(): PerfilViewModel {
    // remember {} = a instancia de PerfilViewModel so e criada UMA VEZ,
    // e sobrevive a mudancas de estado da UI (recomposicoes).
    return remember { PerfilViewModel() }
}
```

Se sua tela receber **parametro via navegacao** (ex: id do usuario), faca assim:

```kotlin
@Composable
fun lembrarPerfilViewModel(idUsuario: String): PerfilViewModel {
    // note que o idUsuario foi passado para a chave de remember,
    // assim sempre que o id mudar uma nova instancia do VM eh criada.
    return remember(idUsuario) { PerfilViewModel(idUsuario) }
}
```

---

### Passo 4 - Criar o `PerfilScreen.kt` (só UI, sem logica)

A tela recebe o ViewModel e os callbacks via parametro. Ela NAO toma decisoes; so le `uiState` e envia eventos para o ViewModel.

```kotlin
package projeto.integrador.sexto.ui.screens.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import projeto.integrador.sexto.ui.components.BotaoPrimario
import projeto.integrador.sexto.ui.components.CardSimples

@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel,
    aoVoltar: () -> Unit   // Callback para navegacao (definido no NavGraph)
) {
    val state = viewModel.uiState

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (state.carregando) {
            CircularProgressIndicator()
        } else {
            CardSimples(titulo = "Meu Perfil") {
                Text(text = state.nome, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text(text = state.email, style = MaterialTheme.typography.bodyMedium)
            }

            BotaoPrimario(texto = "Voltar", onClick = aoVoltar)
        }
    }
}
```

---

### Passo 5 - Registrar a rota em `Rotas.kt`

Abra `shared/src/commonMain/kotlin/projeto/integrador/sexto/ui/navigation/Rotas.kt` e adicione uma nova subclasse ao `sealed class RotaApp`:

```kotlin
sealed class RotaApp {
    data object Login : RotaApp()
    data class Home(val nomeUsuario: String = VALOR_PADRAO_NOME_USUARIO) : RotaApp()

    // 👇 ADICIONE SUA NOVA ROTA AQUI
    // Sem argumento: crie como data object
    data object Perfil : RotaApp()

    // Com argumento: crie como data class (o valor jah fica tipado no construtor)
    // data class DetalhePedido(val idPedido: String) : RotaApp()

    companion object {
        const val VALOR_PADRAO_NOME_USUARIO = "Usuario"
    }
}
```

---

### Passo 6 - Ligar tudo no `NavGraph.kt`

Abra `shared/src/commonMain/kotlin/projeto/integrador/sexto/ui/navigation/NavGraph.kt` e adicione um novo `is` dentro do `when` da funcao `AppNavHost`:

```kotlin
@Composable
fun AppNavHost(navController: AppNavController) {
    when (val rota = navController.rotaAtual) {

        // ... (Login e Home ja existentes) ...

        // 👇 ADICIONE UM NOVO CASE AQUI
        is RotaApp.Perfil -> {
            val vm = lembrarPerfilViewModel()
            PerfilScreen(
                viewModel = vm,
                aoVoltar = {
                    // Volta uma tela (desempilha).
                    navController.voltar()
                }
            )
        }

        // Exemplo com argumento:
        // is RotaApp.DetalhePedido -> {
        //     val vm = lembrarDetalhePedidoViewModel(rota.idPedido)
        //     DetalhePedidoScreen(
        //         viewModel = vm,
        //         aoVoltar = { navController.voltar() }
        //     )
        // }
    }
}
```

---

## Como navegar DE UMA TELA PARA OUTRA

A navegacao sempre acontece atraves de callbacks que a tela recebe por parametro. **Nunca importe AppNavController dentro de uma Screen.**

### Exemplo: navegar da Home para o Perfil

**Passo A)** Adicione um callback no HomeScreen:

```kotlin
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    aoSair: () -> Unit,
    aoAbrirPerfil: () -> Unit   // 👈 adicione este callback
) {
    // Dentro do layout, use num BotaoPrimario:
    BotaoPrimario(texto = "Abrir Perfil", onClick = aoAbrirPerfil)
}
```

**Passo B)** Implemente o callback dentro do bloco `is RotaApp.Home` do `AppNavHost` (em NavGraph.kt):

```kotlin
is RotaApp.Home -> {
    val viewModel = lembrarHomeViewModel(rota.nomeUsuario)
    HomeScreen(
        viewModel = viewModel,
        aoSair = { navController.navegarELimpar(RotaApp.Login) },
        aoAbrirPerfil = {
            // Empilha uma nova tela em cima.
            navController.navegarPara(RotaApp.Perfil)
            // Com argumento: navController.navegarPara(RotaApp.DetalhePedido(id = "123"))
        }
    )
}
```

### Comandos uteis do AppNavController

| Acao | Codigo |
|---|---|
| Ir para outra tela (empilha) | `navController.navegarPara(RotaApp.Perfil)` |
| Ir e apagar TODA a pilha (ex: login -> home) | `navController.navegarELimpar(RotaApp.Home("Maria"))` |
| Voltar uma tela (desempilha) | `navController.voltar()` |
| Voltar ate uma tela especifica | `navController.voltarAte(RotaApp.Home(), inclusive = false)` |

---

## Como usar os componentes prontos

Foram criados 3 componentes reutilizaveis em `ui/components/`:

### BotaoPrimario
```kotlin
BotaoPrimario(
    texto = "Salvar",
    onClick = { viewModel.salvar() },
    habilitado = !state.carregando
)
```

### CampoTexto
```kotlin
CampoTexto(
    valor = state.nome,
    aoMudarValor = viewModel::aoMudarNome,
    rotulo = "Nome completo",
    placeholder = "Digite seu nome",
    eSenha = false,       // true = mostra bolinhas
    mensagemErro = state.erroNome  // null ou "" = sem erro
)
```

### CardSimples
```kotlin
CardSimples(titulo = "Dados do pedido") {
    Text("Numero: 123")
    Text("Total: R$ 99,90")
}
```

Para criar componentes novos: basta criar um arquivo `.kt` em `ui/components/` seguindo o mesmo modelo.

---

## Como usar o tema (cores, fontes, shapes)

**Nunca coloque cores hardcoded.** Sempre use as cores do tema via `MaterialTheme.colorScheme`:

```kotlin
import androidx.compose.material3.MaterialTheme

// Dentro de um @Composable:
Text(
    text = "Ola mundo",
    color = MaterialTheme.colorScheme.onBackground,   // 👈 use sempre isso
    style = MaterialTheme.typography.titleMedium      // 👈 tipografia do tema
)

// Para fundo:
Box(modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer))
```

Para mudar as cores do app (tema claro/escuro), edite apenas o arquivo
`shared/src/commonMain/kotlin/projeto/integrador/sexto/ui/theme/Color.kt`.
Todas as telas se atualizam automaticamente.

---

## Quando usar androidMain / jvmMain / jsMain / wasmJsMain

Use somente quando precisar de uma API especifica de uma plataforma.
Padrao: `expect` no commonMain, `actual` em cada source set.

Exemplo ja existente no projeto:
- `shared/src/commonMain/.../Platform.kt` → `expect fun getPlatform()`
- `shared/src/androidMain/.../Platform.android.kt` → `actual fun getPlatform()`
- `shared/src/jvmMain/.../Platform.jvm.kt` → `actual fun getPlatform()`
- etc.

Se for algo que funciona em todas (ex: uma tela, uma logica matematica,
validacao, formatacao de texto), **sempre coloque no commonMain**.

---

## Execucao no Windows

Abra o PowerShell na raiz do repositorio e entre na pasta do frontend:

```powershell
cd .\frontendMultiplataforma
```

### Desktop

Execucao normal:

```powershell
.\gradlew.bat :desktopApp:run
```

O aplicativo Desktop sera aberto em uma janela.

### Web com Kotlin/JS

Use esta opcao para maior compatibilidade com navegadores antigos:

```powershell
.\gradlew.bat :webApp:jsBrowserDevelopmentRun
```

Se quiser maior desempenho em navegadores mais modernos, use:

```powershell
.\gradlew.bat :webApp:wasmJsBrowserDevelopmentRun
```

O Gradle iniciara um servidor local. Abra no navegador o endereco informado no terminal, normalmente `http://localhost:8080`.

### Android

Para gerar o APK de debug:

```powershell
.\gradlew.bat :androidApp:assembleDebug
```

O APK sera gerado em:

```text
frontendMultiplataforma/androidApp/build/outputs/apk/debug/androidApp-debug.apk
```

Com um emulador ou dispositivo conectado, instale e execute com:

```powershell
.\gradlew.bat :androidApp:installDebug
```

Outra opcao e abrir `frontendMultiplataforma` no Android Studio, aguardar a sincronizacao do Gradle, selecionar a configuracao `androidApp` e clicar em Run.

---

## Comandos uteis do Gradle

Listar todas as tarefas disponiveis:

```powershell
.\gradlew.bat tasks
```

Limpar os artefatos gerados:

```powershell
.\gradlew.bat clean
```

Recompilar os modulos:

```powershell
.\gradlew.bat build
```

---

## Cheat sheet rapido para quem vem de React / Android XML

| O que voce quer fazer | Compose Multiplatform |
|---|---|
| Componente reutilizavel | Funcao com `@Composable` |
| Estado local dentro do componente | `var valor by remember { mutableStateOf("") }` |
| Estado global da tela | Classe ViewModel com UiState + mutableStateOf, instancia guardada com remember{} |
| Efeito ao montar a tela | `LaunchedEffect(Unit) { ... }` |
| Passa props pra filho | Parametros normais da funcao composable |
| Botao clicavel | `Button(onClick = { ... }) { Text("Ok") }` |
| Lista de itens | `LazyColumn { items(lista) { item -> ... } }` |
| Espacamento entre itens | `Arrangement.spacedBy(8.dp)` ou `Spacer(Modifier.height(8.dp))` |
| Espacamento interno | `Modifier.padding(16.dp)` |
| Largura total | `Modifier.fillMaxWidth()` |
| Centralizar | `Box(contentAlignment = Alignment.Center)` ou `Column(horizontalAlignment = Alignment.CenterHorizontally)` |
| Condicional | `if (estado) { Componente() }` |
| Navegar para tela | callback passado pela NavGraph (ex: `aoAbrirPerfil = { navController.navegarPara(RotaApp.Perfil) }`) |
