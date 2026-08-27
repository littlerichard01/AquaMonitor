package projeto.integrador.sexto.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import projeto.integrador.sexto.ui.screens.home.HomeScreen
import projeto.integrador.sexto.ui.screens.home.lembrarHomeViewModel
import projeto.integrador.sexto.ui.screens.login.LoginScreen
import projeto.integrador.sexto.ui.screens.login.lembrarLoginViewModel

// Controlador de navegacao customizado, sem dependencias de bibliotecas externas.
// Funciona da mesma forma em Android, Desktop e Web.
//
// Possui uma pilha (stack) de telas. A ultima da lista e a tela visivel.
// - navegarPara(): empilha uma nova tela.
// - voltar(): remove a tela do topo da pilha.
// - navegarELimpar(): limpa a pilha inteira antes de abrir a nova tela (login -> home).
class AppNavController {

    // Pilha de telas. A ultima da lista e a atualmente exibida.
    private val pilha: SnapshotStateList<RotaApp> = mutableStateListOf(RotaApp.Login)

    // Rota atualmente visivel. Toda mudanca dispara recomposicao.
    var rotaAtual: RotaApp by mutableStateOf(pilha.last())
        private set

    // Empilha uma nova tela em cima da pilha.
    fun navegarPara(rota: RotaApp) {
        pilha.add(rota)
        rotaAtual = pilha.last()
    }

    // Remove a tela do topo e volta para a anterior.
    // Se so tiver 1 tela na pilha, nao faz nada para nao sair do app.
    fun voltar(): Boolean {
        return if (pilha.size > 1) {
            pilha.removeLast()
            rotaAtual = pilha.last()
            true
        } else {
            false
        }
    }

    // Limpa toda a pilha e deixa apenas a nova rota (usado no login -> home).
    fun navegarELimpar(rota: RotaApp) {
        pilha.clear()
        pilha.add(rota)
        rotaAtual = pilha.last()
    }

    // Remove ate uma determinada rota, deixando-a no topo.
    fun voltarAte(rotaAlvo: RotaApp, inclusive: Boolean = false) {
        val indiceAlvo = pilha.indexOfLast { it::class == rotaAlvo::class }
        if (indiceAlvo >= 0) {
            val removerAte = if (inclusive) indiceAlvo else indiceAlvo + 1
            while (pilha.size > removerAte) pilha.removeLast()
            if (pilha.isEmpty()) pilha.add(rotaAlvo)
            rotaAtual = pilha.last()
        }
    }
}

// Liga a Rota atual ao seu composable de tela.
// Ao adicionar uma nova Tela/Rota, basta adicionar um novo when aqui.
@Composable
fun AppNavHost(navController: AppNavController) {
    when (val rota = navController.rotaAtual) {

        // ----- Tela de Login -----
        is RotaApp.Login -> {
            val viewModel = lembrarLoginViewModel()
            LoginScreen(
                viewModel = viewModel,
                aoLogarComSucesso = { nomeUsuario ->
                    // Login com sucesso: limpa a pilha e vai para a Home.
                    navController.navegarELimpar(RotaApp.Home(nomeUsuario))
                }
            )
        }

        // ----- Tela Principal (Home) -----
        is RotaApp.Home -> {
            val viewModel = lembrarHomeViewModel(rota.nomeUsuario)
            HomeScreen(
                viewModel = viewModel,
                aoSair = {
                    // Logout: limpa a pilha e volta para o Login.
                    navController.navegarELimpar(RotaApp.Login)
                }
            )
        }

        // ---- NOVAS TELAS: adicionar abaixo seguindo o mesmo padrao ----
        //
        // Exemplo:
        // is RotaApp.Perfil -> {
        //     val vm = lembrarPerfilViewModel()
        //     PerfilScreen(
        //         viewModel = vm,
        //         aoVoltar = { navController.voltar() }
        //     )
        // }
        //
        // is RotaApp.DetalhePedido -> {
        //     val vm = lembrarDetalhePedidoViewModel(rota.idPedido)
        //     DetalhePedidoScreen(
        //         viewModel = vm,
        //         aoVoltar = { navController.voltar() }
        //     )
        // }
    }
}
