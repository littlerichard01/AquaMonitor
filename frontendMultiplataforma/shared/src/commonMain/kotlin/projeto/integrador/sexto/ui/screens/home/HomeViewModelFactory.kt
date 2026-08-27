package projeto.integrador.sexto.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

// Helper para criar o HomeViewModel passando o nome do usuario recebido da navegacao.
// remember guarda a mesma instancia enquanto a tela estiver ativa.
// Se nomeUsuario mudar, uma nova instancia do ViewModel sera criada.
@Composable
fun lembrarHomeViewModel(nomeUsuario: String): HomeViewModel {
    return remember(nomeUsuario) { HomeViewModel(nomeUsuario = nomeUsuario) }
}
