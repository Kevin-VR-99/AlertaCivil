package mx.utselva.alertacivil.mobile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.utselva.alertacivil.data.ImagenUtils
import mx.utselva.alertacivil.model.NivelRiesgo
import mx.utselva.alertacivil.model.TipoIncidente
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaReporte(vm: ReporteViewModel = viewModel()) {

    val contexto = LocalContext.current
    val snackbar = remember { SnackbarHostState() }
    val incidentes by vm.incidentes.collectAsState()

    val camara = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap -> if (bitmap != null) vm.fotografia = bitmap }

    val galeria = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) vm.fotografia = ImagenUtils.desdeUri(contexto, uri) }

    LaunchedEffect(vm.mensaje) {
        vm.mensaje?.let {
            snackbar.showSnackbar(it)
            vm.limpiarMensaje()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("AlertaCivil", fontWeight = FontWeight.Bold)
                        Text(
                            "Reporte de incidentes en campo",
                            fontSize = 12.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { relleno ->
        Column(
            modifier = Modifier
                .padding(relleno)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = vm.titulo,
                onValueChange = { vm.titulo = it },
                label = { Text("Titulo del incidente") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = vm.descripcion,
                onValueChange = { vm.descripcion = it },
                label = { Text("Descripcion") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = vm.ubicacion,
                onValueChange = { vm.ubicacion = it },
                label = { Text("Ubicacion (colonia, calle o referencia)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = vm.reportadoPor,
                onValueChange = { vm.reportadoPor = it },
                label = { Text("Reportado por") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            Text("Tipo de incidente", fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TipoIncidente.TODOS.forEach { opcion ->
                    FilterChip(
                        selected = vm.tipo == opcion,
                        onClick = { vm.tipo = opcion },
                        label = { Text(opcion) }
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            Text("Nivel de riesgo", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NivelRiesgo.TODOS.forEach { opcion ->
                    FilterChip(
                        selected = vm.nivelRiesgo == opcion,
                        onClick = { vm.nivelRiesgo = opcion },
                        label = { Text(opcion) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = colorPorNivel(opcion),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            Text("Evidencia fotografica", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { camara.launch(null) }) {
                    Icon(Icons.Filled.PhotoCamera, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Camara")
                }
                OutlinedButton(
                    onClick = {
                        galeria.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Icon(Icons.Filled.PhotoLibrary, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Galeria")
                }
            }

            vm.fotografia?.let { bitmap ->
                Spacer(Modifier.height(12.dp))
                androidx.compose.foundation.Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Evidencia seleccionada",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { vm.publicar() },
                enabled = !vm.enviando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (vm.enviando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(12.dp))
                    Text("Publicando...")
                } else {
                    Icon(Icons.Filled.Send, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Publicar al centro de mando")
                }
            }

            Spacer(Modifier.height(28.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))
            Text(
                "Incidentes activos en el servidor (${incidentes.size})",
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))

            if (incidentes.isEmpty()) {
                Text(
                    "Aun no hay incidentes publicados.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                incidentes.forEach { incidente ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Box(
                            Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(colorPorNivel(incidente.nivelRiesgo))
                        )
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(incidente.titulo, fontWeight = FontWeight.SemiBold)
                            Text(
                                "${incidente.tipo} | ${incidente.ubicacion} | ${
                                    SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
                                        .format(Date(incidente.timestamp))
                                }",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}