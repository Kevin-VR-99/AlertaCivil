package mx.utselva.alertacivil.tv

import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import mx.utselva.alertacivil.data.ImagenUtils
import mx.utselva.alertacivil.model.Incidente
import mx.utselva.alertacivil.model.NivelRiesgo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PantallaTablero(vm: TableroViewModel = viewModel()) {

    val incidentes by vm.incidentes.collectAsState()

    Column(Modifier.fillMaxSize()) {
        EncabezadoTablero(total = incidentes.size)

        if (incidentes.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "Sin incidentes activos. Esperando reportes de campo...",
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(32.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(incidentes, key = { it.id }) { incidente ->
                    TarjetaIncidente(incidente = incidente)
                }
            }
            BandaAvisos(incidentes)
        }
    }
}

@Composable
private fun EncabezadoTablero(total: Int) {
    var reloj by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        while (true) {
            reloj = SimpleDateFormat("EEEE dd 'de' MMMM  |  HH:mm:ss", Locale("es", "MX"))
                .format(Date())
            delay(1_000)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF14263D))
            .padding(horizontal = 40.dp, vertical = 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                "ALERTACIVIL  ·  CENTRO DE MANDO",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                "Direccion Municipal de Proteccion Civil",
                fontSize = 15.sp,
                color = Color(0xFF9AB6D4)
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(reloj, fontSize = 18.sp, color = Color.White)
            Text(
                "$total incidentes activos",
                fontSize = 15.sp,
                color = Color(0xFFFF8C42),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun TarjetaIncidente(incidente: Incidente) {

    var enfocada by remember { mutableStateOf(false) }
    val escala by animateFloatAsState(if (enfocada) 1.05f else 1f, label = "escala")
    val color = colorPorNivel(incidente.nivelRiesgo)

    Column(
        modifier = Modifier
            .scale(escala)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = if (enfocada) 3.dp else 1.dp,
                color = if (enfocada) color else Color(0xFF2A3038),
                shape = RoundedCornerShape(14.dp)
            )
            .onFocusChanged { enfocada = it.isFocused }
            .focusable()
    ) {
        ImagenIncidente(
            base64 = incidente.imagenBase64,
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
        )

        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "${incidente.tipo}  ·  Riesgo ${incidente.nivelRiesgo}",
                    fontSize = 13.sp,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                incidente.titulo,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2
            )
            Spacer(Modifier.height(6.dp))
            Text(
                incidente.descripcion,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3
            )
            Spacer(Modifier.height(10.dp))
            Text(
                incidente.ubicacion,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "${incidente.reportadoPor}  ·  ${
                    SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
                        .format(Date(incidente.timestamp))
                }",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Decodifica la imagen Base64 fuera del hilo principal para que el
 * tablero no se congele al recibir varios incidentes a la vez.
 */
@Composable
private fun ImagenIncidente(base64: String, modifier: Modifier = Modifier) {
    val bitmap by produceState<Bitmap?>(initialValue = null, base64) {
        value = withContext(Dispatchers.Default) { ImagenUtils.decodificar(base64) }
    }

    Box(
        modifier = modifier.background(Color(0xFF0E1319)),
        contentAlignment = Alignment.Center
    ) {
        val imagen = bitmap
        if (imagen != null) {
            Image(
                bitmap = imagen.asImageBitmap(),
                contentDescription = "Evidencia del incidente",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text("Cargando evidencia...", fontSize = 13.sp, color = Color(0xFF5B6672))
        }
    }
}

/** Banda inferior con el aviso del incidente de mayor riesgo. */
@Composable
private fun BandaAvisos(incidentes: List<Incidente>) {
    val criticos = incidentes.filter { it.nivelRiesgo == NivelRiesgo.ALTO }
    val aviso = criticos.firstOrNull() ?: incidentes.first()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorPorNivel(aviso.nivelRiesgo))
            .padding(horizontal = 40.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            if (criticos.isEmpty()) "AVISO A LA POBLACION:  " else "ALERTA DE RIESGO ALTO:  ",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            "${aviso.titulo} en ${aviso.ubicacion}. ${aviso.descripcion}",
            fontSize = 17.sp,
            color = Color.White,
            maxLines = 1
        )
    }
}