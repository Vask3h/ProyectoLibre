package com.example.aplicaciondam2

/**
 * Almacén en memoria de los coches.
 * Al ser un "object", solo hay una instancia en toda la app,
 * por lo que todas las Activities ven la misma lista.
 */
object CocheRepository {

    // Lista modificable privada: solo se cambia desde este objeto
    private val coches = mutableListOf<Coche>()

    // Se ejecuta una sola vez, cuando se usa el objeto por primera vez
    init {
        coches.add(
            Coche("Seat", "León", 2019, 14500.0,
                "Compacto en muy buen estado, 85.000 km, gasolina y revisiones al día.",
                R.drawable.ic_coche)
        )
        coches.add(
            Coche("Volkswagen", "Golf", 2017, 13200.0,
                "Diésel de bajo consumo, un solo propietario y ITV recién pasada.",
                R.drawable.ic_coche)
        )
        coches.add(
            Coche("Toyota", "Corolla", 2021, 21900.0,
                "Híbrido con etiqueta ECO, 30.000 km y garantía oficial.",
                R.drawable.ic_coche)
        )
    }

    // Devuelve la lista (la misma referencia, para que el adaptador vea los cambios)
    fun getCoches(): List<Coche> = coches

    // Añade un coche nuevo al catálogo
    fun agregar(coche: Coche) {
        coches.add(coche)
    }

    // Número total de coches registrados
    fun total(): Int = coches.size
}
