package com.example.aplicaciondam2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

/**
 * Adapter propio del RecyclerView.
 * - lista: coches a mostrar
 * - onClick: función que se ejecuta al pulsar un coche
 */
class CocheAdapter(
    private val lista: List<Coche>,
    private val onClick: (Coche) -> Unit
) : RecyclerView.Adapter<CocheAdapter.CocheViewHolder>() {

    // ViewHolder: guarda las referencias a las vistas de una fila
    class CocheViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCoche: ImageView = itemView.findViewById(R.id.ivCoche)
        val tvMarcaModelo: TextView = itemView.findViewById(R.id.tvMarcaModelo)
        val tvAnio: TextView = itemView.findViewById(R.id.tvAnio)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
    }

    // Crea una fila nueva a partir de item_coche.xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocheViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_coche, parent, false)
        return CocheViewHolder(vista)
    }

    // Rellena la fila con los datos del coche de esa posición
    override fun onBindViewHolder(holder: CocheViewHolder, position: Int) {
        val coche = lista[position]
        holder.ivCoche.setImageResource(coche.imagenResId)
        holder.tvMarcaModelo.text = "${coche.marca} ${coche.modelo}"
        holder.tvAnio.text = "Año: ${coche.anio}"
        holder.tvPrecio.text = String.format(Locale("es", "ES"), "%,.2f €", coche.precio)

        // Al pulsar la fila avisamos a la Activity
        holder.itemView.setOnClickListener { onClick(coche) }
    }

    // Número de elementos de la lista
    override fun getItemCount(): Int = lista.size
}
