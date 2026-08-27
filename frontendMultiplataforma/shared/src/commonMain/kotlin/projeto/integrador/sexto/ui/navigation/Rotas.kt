package projeto.integrador.sexto.ui.navigation

// Rotas do app representadas como uma hierarquia de sealed class.
// Vantagens sobre strings: compilador valida todas as rotas, nao ha typo.
// Cada subclasse representa uma tela, com seus argumentos tipados no construtor.
sealed class RotaApp {

    // Tela de Login (sem argumentos).
    data object Login : RotaApp()

    // Tela principal apos o login, recebe o nome do usuario como argumento.
    data class Home(val nomeUsuario: String = VALOR_PADRAO_NOME_USUARIO) : RotaApp()

    // --- Como adicionar uma nova tela no futuro ---
    // Basta criar uma nova subclasse aqui. Exemplos:
    //   data object Perfil : RotaApp()
    //   data class DetalhePedido(val idPedido: String) : RotaApp()

    companion object {
        // Valor padrao para Home quando o nome do usuario nao eh informado.
        const val VALOR_PADRAO_NOME_USUARIO = "Usuario"
    }
}
