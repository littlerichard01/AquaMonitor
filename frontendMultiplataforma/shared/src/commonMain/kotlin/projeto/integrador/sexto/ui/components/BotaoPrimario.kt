package projeto.integrador.sexto.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Componente de botao principal reutilizavel.
// Use ele para acoes principais da tela, como "Entrar", "Salvar", "Confirmar".
//
// Parametros:
//   texto -> texto exibido dentro do botao.
//   onClick -> funcao chamada quando o usuario clicar no botao.
//   modifier -> modificador opcional para customizar tamanho/margens.
//   habilitado -> se false o botao fica cinza e nao recebe clique.
@Composable
fun BotaoPrimario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = habilitado,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        // Usa as cores do tema em vez de valores hardcoded.
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        // O texto ja ganha a tipografia e cor corretos do Button.
        // Apenas ajustamos o estilo para ser o labelLarge padrao.
        Text(
            text = texto,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
