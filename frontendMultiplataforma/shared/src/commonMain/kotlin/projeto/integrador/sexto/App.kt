package projeto.integrador.sexto

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import projeto.integrador.sexto.ui.navigation.AppNavController
import projeto.integrador.sexto.ui.navigation.AppNavHost
import projeto.integrador.sexto.ui.theme.AppTheme

// Composable raiz de todo o aplicativo.
// Ele e chamado nos entry points de cada plataforma:
//   Android: MainActivity.setContent { App() }
//   Desktop: Window { App() }
//   Web:     ComposeViewport { App() }
//
// Responsabilidades:
//   1. Aplicar o tema global (AppTheme) para todas as telas.
//   2. Criar o controlador de navegacao (AppNavController) e NavHost.
//   3. Servir como surface base do layout (fundo da janela/tela).
@Composable
@Preview
fun App() {
    AppTheme {
        // Surface preenche todo o espaco e usa a cor background do tema.
        // Semelhante ao <body> no HTML ou ao decorView no Android.
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            // remember() mantem a mesma instancia do navController
            // mesmo que a UI se recomponha (rotacao, resize etc).
            val navController = remember { AppNavController() }
            AppNavHost(navController)
        }
    }
}
