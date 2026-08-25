package mx.utselva.alertacivil.tv

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaTv = darkColorScheme(
    primary = Color(0xFF4FA3E3),
    secondary = Color(0xFFFF8C42),
    background = Color(0xFF0B0E12),
    surface = Color(0xFF161A20),
    onBackground = Color(0xFFECEFF3),
    onSurface = Color(0xFFECEFF3),
    onSurfaceVariant = Color(0xFF9AA5B1)
)

@Composable
fun AlertaCivilTvTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = EsquemaTv, content = content)
}

/** Mismo criterio de color que la app movil. */
fun colorPorNivel(nivel: String): Color = when (nivel) {
    "Alto" -> Color(0xFFE74C3C)
    "Medio" -> Color(0xFFF39C12)
    else -> Color(0xFF2ECC71)
}