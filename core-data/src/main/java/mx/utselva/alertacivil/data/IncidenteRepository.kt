package mx.utselva.alertacivil.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import mx.utselva.alertacivil.model.Incidente

/**
 * Servicio de comunicacion compartido por la app movil y la app de Smart TV.
 *
 * La app movil PUBLICA incidentes.
 * La app de TV los ESCUCHA en tiempo real mediante un listener de Firestore.
 *
 * Al vivir en el modulo :core-data, ninguna de las dos aplicaciones necesita
 * reimplementar la logica de acceso a datos.
 */
class IncidenteRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val coleccion = firestore.collection(COLECCION)

    /**
     * Devuelve un flujo que emite la lista de incidentes cada vez que
     * cambia algo en el servidor. Es lo que permite que la pantalla de TV
     * se actualice sola cuando el brigadista publica desde el celular.
     */
    fun observarIncidentes(): Flow<List<Incidente>> = callbackFlow {
        val registro = coleccion
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(30)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val lista = snapshot?.documents
                    ?.mapNotNull { documento ->
                        documento.toObject(Incidente::class.java)?.copy(id = documento.id)
                    }
                    ?.filter { it.activo }
                    ?: emptyList()
                trySend(lista)
            }

        awaitClose { registro.remove() }
    }

    /** Publica un incidente nuevo. Lo usa la aplicacion movil. */
    suspend fun publicarIncidente(incidente: Incidente): Result<String> = try {
        val documento = coleccion.document()
        val nuevo = incidente.copy(
            id = documento.id,
            timestamp = System.currentTimeMillis(),
            activo = true
        )
        documento.set(nuevo).await()
        Result.success(documento.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /** Marca un incidente como atendido para que deje de mostrarse en el tablero. */
    suspend fun marcarAtendido(id: String): Result<Unit> = try {
        coleccion.document(id).update("activo", false).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    companion object {
        const val COLECCION = "incidentes"
    }
}