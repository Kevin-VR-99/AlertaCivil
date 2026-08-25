package mx.utselva.alertacivil.mobile

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NaranjaEmergencia = Color(0xFFD35400)
private val AzulInstitucional = Color(0xFF1B3A5C)

private val EsquemaClaro = lightColorScheme(
    primary = AzulInstitucional,
    secondary = NaranjaEmergencia,
    background = Color(0xFFF5F6F8),
    surface = Color.White
)

private val EsquemaOscuro = darkColorScheme(
    primary = Color(0xFF7FB2E5),
    secondary = Color(0xFFFF8C42),
    background = Color(0xFF121417),
    surface = Color(0xFF1C1F24)
)

@Composable
fun AlertaCivilMobileTheme(
    oscuro: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (oscuro) EsquemaOscuro else EsquemaClaro,
        content = content
    )
}

/** Color asociado a cada nivel de riesgo. Se reutiliza en la app de TV. */
fun colorPorNivel(nivel: String): Color = when (nivel) {
    "Alto" -> Color(0xFFC0392B)
    "Medio" -> Color(0xFFE67E22)
    else -> Color(0xFF27AE60)
}