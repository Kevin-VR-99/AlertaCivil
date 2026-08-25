package mx.utselva.alertacivil.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import java.io.ByteArrayOutputStream

/**
 * Utilidades compartidas para el manejo de la informacion multimedia.
 *
 * La fotografia se comprime y se codifica en Base64 para viajar dentro del
 * documento de Firestore. Asi el intercambio movil <-> TV funciona con el
 * plan gratuito de Firebase, sin necesidad de contratar almacenamiento.
 */
object ImagenUtils {

    /** Limite conservador: un documento de Firestore admite hasta 1 MB. */
    private const val LIMITE_BYTES = 700_000

    /** Comprime el mapa de bits y lo devuelve codificado en Base64. */
    fun comprimirYCodificar(bitmap: Bitmap, anchoMaximo: Int = 720): String {
        val escalado = escalar(bitmap, anchoMaximo)
        var calidad = 65
        var bytes = comprimir(escalado, calidad)

        // Si la imagen sigue siendo pesada, se baja la calidad progresivamente.
        while (bytes.size > LIMITE_BYTES && calidad > 20) {
            calidad -= 15
            bytes = comprimir(escalado, calidad)
        }
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    /** Reconstruye el mapa de bits a partir de la cadena Base64. Lo usa la app de TV. */
    fun decodificar(base64: String): Bitmap? = try {
        if (base64.isBlank()) {
            null
        } else {
            val bytes = Base64.decode(base64, Base64.NO_WRAP)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
    } catch (e: Exception) {
        null
    }

    /** Carga una imagen elegida desde la galeria del dispositivo. */
    fun desdeUri(context: Context, uri: Uri): Bitmap? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val fuente = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(fuente) { decoder, _, _ ->
                // Obligatorio: un bitmap de hardware no se puede comprimir.
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                decoder.isMutableRequired = true
            }
        } else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    } catch (e: Exception) {
        null
    }

    private fun escalar(bitmap: Bitmap, anchoMaximo: Int): Bitmap {
        if (bitmap.width <= anchoMaximo) return bitmap
        val proporcion = anchoMaximo.toFloat() / bitmap.width.toFloat()
        val alto = (bitmap.height * proporcion).toInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(bitmap, anchoMaximo, alto, true)
    }

    private fun comprimir(bitmap: Bitmap, calidad: Int): ByteArray {
        val salida = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, calidad, salida)
        return salida.toByteArray()
    }
}