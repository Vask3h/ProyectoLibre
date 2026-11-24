package com.example.aplicaciondam2

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Base de datos principal de la aplicación utilizando Room.
 * Mantiene la tabla "coches" y proporciona acceso a CocheDao.
 */
@Database(entities = [Coche::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cocheDao(): CocheDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                val nueva = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "coches_database"
                )
                    .addCallback(DatabaseCallback(context.applicationContext))
                    .fallbackToDestructiveMigration(true)
                    .build()
                instancia = nueva
                nueva
            }
        }

        private class DatabaseCallback(private val context: Context) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Insertar los coches por defecto al crear la base de datos por primera vez
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = obtener(context).cocheDao()
                    dao.insertar(
                        Coche(
                            marca = "Seat",
                            modelo = "León",
                            anio = 2019,
                            precio = 14500.0,
                            descripcion = "Compacto en muy buen estado, 85.000 km, gasolina y revisiones al día.",
                            imagenResId = R.drawable.ic_coche
                        )
                    )
                    dao.insertar(
                        Coche(
                            marca = "Volkswagen",
                            modelo = "Golf",
                            anio = 2017,
                            precio = 13200.0,
                            descripcion = "Diésel de bajo consumo, un solo propietario y ITV recién pasada.",
                            imagenResId = R.drawable.ic_coche
                        )
                    )
                    dao.insertar(
                        Coche(
                            marca = "Toyota",
                            modelo = "Corolla",
                            anio = 2021,
                            precio = 21900.0,
                            descripcion = "Híbrido con etiqueta ECO, 30.000 km y garantía oficial.",
                            imagenResId = R.drawable.ic_coche
                        )
                    )
                }
            }
        }
    }
}
