package projeto.integrador.sexto.ui.screens.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

// Helper que cria o ViewModel de Login e lembra a instancia entre recomposicoes.
// Usamos remember{} para manter a mesma instancia enquanto a tela estiver ativa.
@Composable
fun lembrarLoginViewModel(): LoginViewModel {
    return remember { LoginViewModel() }
}
