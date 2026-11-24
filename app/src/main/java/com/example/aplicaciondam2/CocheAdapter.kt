package com.example.aplicaciondam2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

/**
 * Adapter propio del RecyclerView.
 * - lista: coches a mostrar
 * - onClick: función que se ejecuta al pulsar un coche para ver detalles
 * - onDeleteClick: función que se ejecuta al pulsar el botón de papelera para eliminar
 */
class CocheAdapter(
    private var lista: List<Coche>,
    private val onClick: (Coche) -> Unit,
    private val onDeleteClick: ((Coche) -> Unit)? = null
) : RecyclerView.Adapter<CocheAdapter.CocheViewHolder>() {

    class CocheViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCoche: ImageView = itemView.findViewById(R.id.ivCoche)
        val tvMarcaModelo: TextView = itemView.findViewById(R.id.tvMarcaModelo)
        val tvAnio: TextView = itemView.findViewById(R.id.tvAnio)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        val btnBorrar: ImageButton = itemView.findViewById(R.id.btnBorrar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocheViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_coche, parent, false)
        return CocheViewHolder(vista)
    }

    override fun onBindViewHolder(holder: CocheViewHolder, position: Int) {
        val coche = lista[position]
        val bitmap = Fotos.cargar(coche.fotoPath)
        if (bitmap != null) {
            holder.ivCoche.setImageBitmap(bitmap)
        } else {
            holder.ivCoche.setImageResource(coche.imagenResId)
        }
        holder.tvMarcaModelo.text = "${coche.marca} ${coche.modelo}"
        holder.tvAnio.text = "Año: ${coche.anio}"
        holder.tvPrecio.text = String.format(Locale("es", "ES"), "%,.2f €", coche.precio)

        // Al pulsar la fila se abre el detalle del coche
        holder.itemView.setOnClickListener { onClick(coche) }

        // Al pulsar el icono de papelera se solicita la eliminación
        holder.btnBorrar.setOnClickListener {
            onDeleteClick?.invoke(coche)
        }

        // También se mantiene la opción de borrar mediante pulsación larga en la fila
        holder.itemView.setOnLongClickListener {
            onDeleteClick?.invoke(coche)
            true
        }
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevosCoches: List<Coche>) {
        lista = nuevosCoches
        notifyDataSetChanged()
    }
}
