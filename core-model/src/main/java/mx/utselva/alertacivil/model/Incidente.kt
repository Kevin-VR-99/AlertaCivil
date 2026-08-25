package mx.utselva.alertacivil.model

/**
 * Modelo de datos compartido entre la aplicacion movil y la aplicacion de Smart TV.
 * Vive en el modulo :core-model para evitar duplicidad de codigo.
 *
 * Todos los campos tienen valor por defecto porque Cloud Firestore requiere
 * un constructor sin argumentos para poder deserializar los documentos.
 */
data class Incidente(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val tipo: String = TipoIncidente.OTRO,
    val nivelRiesgo: String = NivelRiesgo.MEDIO,
    val ubicacion: String = "",
    val imagenBase64: String = "",
    val reportadoPor: String = "",
    val timestamp: Long = 0L,
    val activo: Boolean = true
)

/** Catalogo de tipos de incidente que puede reportar un brigadista. */
object TipoIncidente {
    const val INUNDACION = "Inundacion"
    const val DESLAVE = "Deslave"
    const val INCENDIO = "Incendio"
    const val SISMO = "Sismo"
    const val OTRO = "Otro"

    val TODOS = listOf(INUNDACION, DESLAVE, INCENDIO, SISMO, OTRO)
}

/** Niveles de riesgo manejados por Proteccion Civil. */
object NivelRiesgo {
    const val BAJO = "Bajo"
    const val MEDIO = "Medio"
    const val ALTO = "Alto"

    val TODOS = listOf(BAJO, MEDIO, ALTO)
}