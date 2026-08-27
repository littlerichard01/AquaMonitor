package projeto.integrador.sexto.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import projeto.integrador.sexto.ui.components.BotaoPrimario
import projeto.integrador.sexto.ui.components.CardSimples

// Tela Home.
//
// Parametros:
//   viewModel -> estado e logica.
//   aoSair -> callback executado ao clicar em "Sair" (usado pelo NavGraph).
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    aoSair: () -> Unit
) {
    val state = viewModel.uiState

    Scaffold(
        topBar = {
            // Barra superior com titulo do app.
            TopAppBar(
                title = {
                    Text(
                        text = "Projeto Integrador",
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingInterno ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterno),
            contentAlignment = Alignment.Center
        ) {
            when {
                // Estado de carregamento.
                state.carregando -> {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                // Tela carregada: exibe a saudacao, lista e botao de sair.
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Saudacao ao usuario logado.
                        Text(
                            text = state.saudacao,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Confira o que ja esta pronto:",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Lista (rolavel) com os cards de exemplo.
                        // LazyColumn = RecyclerView = FlatList = map de divs.
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.itens) { item ->
                                CardSimples(titulo = item.titulo) {
                                    Text(
                                        text = item.descricao,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        // Botao para fazer logout e voltar para login.
                        BotaoPrimario(
                            texto = "Sair",
                            onClick = {
                                viewModel.pedirParaSair()
                                aoSair()
                            }
                        )
                    }
                }
            }
        }
    }
}
