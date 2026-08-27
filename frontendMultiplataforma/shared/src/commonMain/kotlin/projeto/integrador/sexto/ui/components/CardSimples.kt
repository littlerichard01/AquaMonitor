package projeto.integrador.sexto.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Card simples reutilizavel para exibir informacoes em blocos.
// Pense nele como uma "caixa" com cantos arredondados, sombra leve e
// espacamento interno padrao.
//
// Exemplo de uso:
//   CardSimples(titulo = "Usuario", conteudo = { Text(nome) })
//
// Parametros:
//   titulo -> texto do cabecalho do card.
//   modifier -> modificador opcional para ajustar tamanho/margens.
//   conteudo -> bloco composable livre dentro do card (abaixo do titulo).
@Composable
fun CardSimples(
    titulo: String,
    modifier: Modifier = Modifier,
    corFundo: Color? = null,
    conteudo: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = corFundo ?: MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Titulo do card em destaque.
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            // Conteudo customizado passado pelo caller.
            conteudo()
        }
    }
}
