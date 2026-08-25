package mx.utselva.alertacivil.tv

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import mx.utselva.alertacivil.data.IncidenteRepository

class TableroViewModel : ViewModel() {

    private val repositorio = IncidenteRepository()

    /**
     * Flujo en tiempo real. Cuando la app movil publica un incidente,
     * Firestore notifica al listener y esta lista se actualiza sola.
     */
    val incidentes = repositorio.observarIncidentes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun marcarAtendido(id: String) {
        viewModelScope.launch { repositorio.marcarAtendido(id) }
    }
}