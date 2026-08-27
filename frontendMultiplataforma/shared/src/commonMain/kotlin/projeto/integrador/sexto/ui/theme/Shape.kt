package projeto.integrador.sexto.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Define o arredondamento de cantos de todos os componentes do Material3.
// Quando voce cria um Button, Card, TextField etc sem passar shape,
// ele usa automaticamente um dos valores abaixo conforme o tamanho do componente.
val AppShapes = Shapes(
    // Usado por componentes pequenos: Chip, FAB pequeno, Badge.
    extraSmall = RoundedCornerShape(4.dp),
    // Usado por componentes medios-pequenos: TextField, Button, Card pequeno.
    small = RoundedCornerShape(8.dp),
    // Usado por componentes medios: Card, NavigationDrawer, Dialog pequeno.
    medium = RoundedCornerShape(12.dp),
    // Usado por componentes grandes: Dialog, NavigationBar, Card grande.
    large = RoundedCornerShape(16.dp),
    // Usado por componentes que ocupam quase a tela inteira: BottomSheet, Scaffold.
    extraLarge = RoundedCornerShape(28.dp)
)
