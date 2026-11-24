package com.example.aplicaciondam2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.util.Locale

/**
 * Adaptador del RecyclerView con soporte para Glide e importación de modelos API.
 */
class CocheAdapter(
    private var lista: List<Coche>,
    private val onClick: (Coche) -> Unit,
    private val onDeleteClick: ((Coche) -> Unit)? = null,
    private val onImportClick: ((Coche) -> Unit)? = null
) : RecyclerView.Adapter<CocheAdapter.CocheViewHolder>() {

    class CocheViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivCoche: ImageView = itemView.findViewById(R.id.ivCoche)
        val tvMarcaModelo: TextView = itemView.findViewById(R.id.tvMarcaModelo)
        val tvAnio: TextView = itemView.findViewById(R.id.tvAnio)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        val btnBorrar: ImageButton = itemView.findViewById(R.id.btnBorrar)
        val btnImportar: ImageButton = itemView.findViewById(R.id.btnImportar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocheViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_coche, parent, false)
        return CocheViewHolder(vista)
    }

    override fun onBindViewHolder(holder: CocheViewHolder, position: Int) {
        val coche = lista[position]

        // Carga de imágenes: 1) Glide desde URL, 2) Archivo local, 3) Recurso drawable
        if (!coche.imageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(coche.imageUrl)
                .placeholder(R.drawable.ic_coche)
                .error(R.drawable.ic_coche)
                .into(holder.ivCoche)
        } else {
            val bitmap = Fotos.cargar(coche.fotoPath)
            if (bitmap != null) {
                holder.ivCoche.setImageBitmap(bitmap)
            } else {
                holder.ivCoche.setImageResource(coche.imagenResId)
            }
        }

        holder.tvMarcaModelo.text = "${coche.marca} ${coche.modelo}"
        holder.tvAnio.text = if (coche.esOnline) "Catálogo Oficial API" else "Año: ${coche.anio}"
        holder.tvPrecio.text = if (coche.esOnline) "Catálogo Online" else String.format(Locale("es", "ES"), "%,.2f €", coche.precio)

        // Clic en la fila
        holder.itemView.setOnClickListener { onClick(coche) }

        // Botón Borrar
        holder.btnBorrar.setOnClickListener {
            onDeleteClick?.invoke(coche)
        }

        // Botón Importar si es un elemento online
        if (coche.esOnline && onImportClick != null) {
            holder.btnImportar.visibility = View.VISIBLE
            holder.btnImportar.setOnClickListener { onImportClick.invoke(coche) }
        } else {
            holder.btnImportar.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevosCoches: List<Coche>) {
        lista = nuevosCoches
        notifyDataSetChanged()
    }
}
