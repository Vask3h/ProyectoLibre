package com.example.aplicaciondam2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object Fotos {

    fun crearArchivo(context: Context): File {
        val directorio = File(context.filesDir, "fotos")
        if (!directorio.exists()) {
            directorio.mkdirs()
        }
        return File(directorio, "foto_${System.currentTimeMillis()}.jpg")
    }

    fun uriDe(context: Context, archivo: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            archivo
        )
    }

    fun copiarDesdeUri(context: Context, uri: Uri): String? {
        return try {
            val archivoDestino = crearArchivo(context)
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(archivoDestino).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            archivoDestino.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun cargar(path: String?): Bitmap? {
        if (path == null) return null
        val archivo = File(path)
        if (!archivo.exists()) return null
        return BitmapFactory.decodeFile(archivo.absolutePath)
    }
}
