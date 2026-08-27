package projeto.integrador.sexto.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

// Componente de campo de texto reutilizavel com estilo padronizado.
// Use para inputs de login, cadastro, formularios em geral.
//
// Parametros:
//   valor -> texto atual do campo (estado controlado pelo caller).
//   aoMudarValor -> funcao chamada sempre que o texto mudar.
//   rotulo -> texto acima do campo (label).
//   placeholder -> texto de dica dentro do campo quando estiver vazio.
//   modifier -> modificador opcional.
//   eSenha -> se true, aplica mascara de senha (bolinhas).
//   mensagemErro -> se nao-nula/vazia, mostra a mensagem em vermelho abaixo.
//   tecladoOpcoes -> configuracoes de teclado (tipo de entrada, action).
@Composable
fun CampoTexto(
    valor: String,
    aoMudarValor: (String) -> Unit,
    rotulo: String,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    eSenha: Boolean = false,
    mensagemErro: String? = null,
    tecladoOpcoes: KeyboardOptions = KeyboardOptions.Default,
    acoesTeclado: KeyboardActions = KeyboardActions.Default
) {
    val temErro = mensagemErro != null && mensagemErro.isNotBlank()

    OutlinedTextField(
        value = valor,
        onValueChange = aoMudarValor,
        label = {
            Text(
                text = rotulo,
                color = if (temErro) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        placeholder = {
            if (placeholder.isNotBlank()) {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        isError = temErro,
        // Para campo de senha, usa a transformacao visual de asteriscos/bolinhas.
        visualTransformation = if (eSenha) object : VisualTransformation {
            override fun filter(text: AnnotatedString): TransformedText {
                val mascara = "*".repeat(text.text.length)
                return TransformedText(AnnotatedString(mascara), OffsetMapping.Identity)
            }
        } else VisualTransformation.None,
        keyboardOptions = tecladoOpcoes,
        keyboardActions = acoesTeclado,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            errorBorderColor = MaterialTheme.colorScheme.error,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            errorLabelColor = MaterialTheme.colorScheme.error,
            cursorColor = MaterialTheme.colorScheme.primary,
            errorCursorColor = MaterialTheme.colorScheme.error,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent
        )
    )
    // Se houver mensagem de erro, exibe logo abaixo do campo.
    if (temErro) {
        Text(
            text = mensagemErro.orEmpty(),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 4.dp)
        )
    }
}
