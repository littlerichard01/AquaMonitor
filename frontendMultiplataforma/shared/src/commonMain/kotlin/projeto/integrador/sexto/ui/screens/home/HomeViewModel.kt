package projeto.integrador.sexto.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

// ViewModel da tela Home (tela principal apos login).
// Simples por proposito: mostra como carregar dados e gerenciar lista.
//
// Nota: nao usamos androidx.lifecycle.ViewModel aqui por simplicidade.
// Basta manter uma instancia com remember{...} na tela (ver HomeViewModelFactory.kt).
class HomeViewModel(
    // Nome do usuario recebido da tela de login (parametro de navegacao).
    val nomeUsuario: String
) {

    // Estado da tela Home.
    data class UiState(
        val saudacao: String = "",
        val itens: List<ItemLista> = emptyList(),
        val carregando: Boolean = true
    )

    var uiState by mutableStateOf(UiState())
        private set

    // Bloco init: executado assim que o ViewModel e criado.
    // Simula um carregamento inicial (chamada de API, query em banco etc).
    init {
        carregarDados()
    }

    // Simula o carregamento assincrono dos dados da tela.
    // Em um app real, aqui chamariamos um repositorio/API via coroutines.
    private fun carregarDados() {
        uiState = uiState.copy(carregando = true)

        // Para manter o exemplo simples, usamos os dados mockados imediatamente.
        // Se quiser simular delay: use kotlinx.coroutines + viewModelScope.launch.
        val saudacao = "Ola, $nomeUsuario!"
        val itens = listOf(
            ItemLista(
                titulo = "Tela Login",
                descricao = "Autenticacao com validacao de email e senha."
            ),
            ItemLista(
                titulo = "Tema Compartilhado",
                descricao = "Cores, tipografia e shapes em um arquivo so."
            ),
            ItemLista(
                titulo = "Navegacao Multiplataforma",
                descricao = "Funciona em Android, Desktop e Web com o mesmo codigo."
            ),
            ItemLista(
                titulo = "Componentes Reutilizaveis",
                descricao = "Botao, campo de texto e card prontos para uso."
            )
        )

        uiState = uiState.copy(
            saudacao = saudacao,
            itens = itens,
            carregando = false
        )
    }

    // Evento disparado pela UI quando o usuario clica em "Sair".
    // Neste modelo simples, nao tem estado para mudar: quem desloga e o NavGraph.
    fun pedirParaSair() {
        // Aqui voce poderia limpar tokens, cache, chamar logout da API etc.
    }
}

// Modelo simples de item da lista exibida na Home.
data class ItemLista(
    val titulo: String,
    val descricao: String
)
