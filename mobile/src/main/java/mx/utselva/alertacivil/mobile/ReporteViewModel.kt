package mx.utselva.alertacivil.mobile

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mx.utselva.alertacivil.data.ImagenUtils
import mx.utselva.alertacivil.data.IncidenteRepository
import mx.utselva.alertacivil.model.Incidente
import mx.utselva.alertacivil.model.NivelRiesgo
import mx.utselva.alertacivil.model.TipoIncidente

class ReporteViewModel : ViewModel() {

    private val repositorio = IncidenteRepository()

    var titulo by mutableStateOf("")
    var descripcion by mutableStateOf("")
    var ubicacion by mutableStateOf("")
    var reportadoPor by mutableStateOf("")
    var tipo by mutableStateOf(TipoIncidente.INUNDACION)
    var nivelRiesgo by mutableStateOf(NivelRiesgo.MEDIO)
    var fotografia by mutableStateOf<Bitmap?>(null)

    var enviando by mutableStateOf(false)
        private set
    var mensaje by mutableStateOf<String?>(null)

    /** Lista publicada, para confirmar en el celular que el dato llego al servidor. */
    val incidentes = repositorio.observarIncidentes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun publicar() {
        if (titulo.isBlank()) {
            mensaje = "Escribe un titulo para el incidente."
            return
        }
        if (ubicacion.isBlank()) {
            mensaje = "Indica la ubicacion del incidente."
            return
        }
        val imagen = fotografia
        if (imagen == null) {
            mensaje = "Agrega una fotografia como evidencia."
            return
        }

        viewModelScope.launch {
            enviando = true
            val codificada = withContext(Dispatchers.Default) {
                ImagenUtils.comprimirYCodificar(imagen)
            }
            val resultado = repositorio.publicarIncidente(
                Incidente(
                    titulo = titulo.trim(),
                    descripcion = descripcion.trim(),
                    tipo = tipo,
                    nivelRiesgo = nivelRiesgo,
                    ubicacion = ubicacion.trim(),
                    imagenBase64 = codificada,
                    reportadoPor = reportadoPor.ifBlank { "Brigadista sin identificar" }
                )
            )
            enviando = false
            if (resultado.isSuccess) {
                mensaje = "Incidente publicado. Ya es visible en el centro de mando."
                limpiarFormulario()
            } else {
                mensaje = "No se pudo publicar: ${resultado.exceptionOrNull()?.message}"
            }
        }
    }

    fun limpiarMensaje() {
        mensaje = null
    }

    private fun limpiarFormulario() {
        titulo = ""
        descripcion = ""
        ubicacion = ""
        fotografia = null
        tipo = TipoIncidente.INUNDACION
        nivelRiesgo = NivelRiesgo.MEDIO
    }
}