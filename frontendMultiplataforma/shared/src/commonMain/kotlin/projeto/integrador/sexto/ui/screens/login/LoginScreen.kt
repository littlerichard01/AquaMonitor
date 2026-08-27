package projeto.integrador.sexto.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import projeto.integrador.sexto.ui.components.BotaoPrimario
import projeto.integrador.sexto.ui.components.CampoTexto

// Tela de login. E uma funcao @Composable que NAO guarda estado diretamente:
// todo estado fica no LoginViewModel, e a tela apenas le e envia eventos.
//
// Parametros:
//   viewModel -> estado e logica da tela.
//   aoLogarComSucesso -> callback que a camada acima (NavGraph) usa para navegar.
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    aoLogarComSucesso: (nomeUsuario: String) -> Unit
) {
    val state = viewModel.uiState

    // Quando o login e bem sucedido, avisamos ao NavGraph para navegar.
    // LaunchedEffect roda o bloco UMA VEZ sempre que a chave mudar.
    LaunchedEffect(state.loginRealizadoComSucesso) {
        state.loginRealizadoComSucesso?.let { nome ->
            aoLogarComSucesso(nome)
        }
    }

    // Layout principal. Box para poder centralizar conteudo.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Titulo da tela.
            Text(
                text = "Projeto Integrador",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Entre para continuar",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            // Campo de email.
            CampoTexto(
                valor = state.email,
                aoMudarValor = viewModel::aoMudarEmail,
                rotulo = "Email",
                placeholder = "seu@email.com",
                mensagemErro = state.erroEmail,
                tecladoOpcoes = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )

            // Campo de senha.
            CampoTexto(
                valor = state.senha,
                aoMudarValor = viewModel::aoMudarSenha,
                rotulo = "Senha",
                placeholder = "Sua senha",
                eSenha = true,
                mensagemErro = state.erroSenha,
                tecladoOpcoes = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )

            // Erro geral (autenticacao, rede etc).
            state.erroGeral?.let { msg ->
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(8.dp))

            // Botao de entrar. Desabilitado enquanto carrega.
            BotaoPrimario(
                texto = if (state.carregando) "Entrando..." else "Entrar",
                onClick = viewModel::tentarLogar,
                habilitado = !state.carregando
            )

            // Loader pequeno mostrado enquanto carrega.
            if (state.carregando) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
