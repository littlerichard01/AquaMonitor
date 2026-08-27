package projeto.integrador.sexto.ui.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

// ViewModel da tela de login.
// Ele guarda o estado da tela e a logica de negocio (validacao, autenticacao).
// Nunca coloque logica de UI dentro do ViewModel: ele so manipula estado.
// A UI observa o estado e se redesenha automaticamente quando ele muda.
//
// Nota: nao usamos androidx.lifecycle.ViewModel aqui por simplicidade.
// Basta manter uma instancia com remember{...} na tela (ver LoginViewModelFactory.kt).
class LoginViewModel {

    // Estado da tela. Tudo que a tela precisa saber fica encapsulado aqui.
    // Usamos um data class para facilitar a leitura: a UI simplesmente le os campos.
    data class UiState(
        val email: String = "",
        val senha: String = "",
        val carregando: Boolean = false,
        val erroEmail: String? = null,
        val erroSenha: String? = null,
        val erroGeral: String? = null,
        val loginRealizadoComSucesso: String? = null
    )

    // Estado observavel. Quando qualquer campo muda, a tela se recompõe.
    var uiState by mutableStateOf(UiState())
        private set

    // Chamado pela UI quando o usuario digita no campo de email.
    fun aoMudarEmail(novoEmail: String) {
        // Copia o estado atual substituindo apenas o email.
        // Tambem limpa o erro especifico do campo quando o usuario tenta editar.
        uiState = uiState.copy(
            email = novoEmail,
            erroEmail = null,
            erroGeral = null
        )
    }

    // Chamado pela UI quando o usuario digita no campo de senha.
    fun aoMudarSenha(novaSenha: String) {
        uiState = uiState.copy(
            senha = novaSenha,
            erroSenha = null,
            erroGeral = null
        )
    }

    // Chamado quando o usuario clica em "Entrar".
    // Retorna true se a validacao passou, e ai a tela pode navegar.
    // Retorna false se houve erro de validacao.
    fun tentarLogar(): Boolean {
        // 1. Validacao de campos.
        val erros = mutableMapOf<String, String>()

        if (uiState.email.isBlank()) {
            erros["email"] = "Informe seu email."
        } else if (!uiState.email.contains("@")) {
            erros["email"] = "Email invalido."
        }

        if (uiState.senha.isBlank()) {
            erros["senha"] = "Informe sua senha."
        } else if (uiState.senha.length < 3) {
            erros["senha"] = "Senha muito curta."
        }

        if (erros.isNotEmpty()) {
            uiState = uiState.copy(
                erroEmail = erros["email"],
                erroSenha = erros["senha"]
            )
            return false
        }

        // 2. Se passou na validacao, simulamos um login simples.
        // Nao tem backend ainda, entao consideramos qualquer email/senha >= 3
        // como valido e extraimos o "nome" do usuario (parte antes do @).
        val nomeUsuario = uiState.email.substringBefore("@").ifBlank { "Usuario" }
        uiState = uiState.copy(loginRealizadoComSucesso = nomeUsuario)
        return true
    }
}
