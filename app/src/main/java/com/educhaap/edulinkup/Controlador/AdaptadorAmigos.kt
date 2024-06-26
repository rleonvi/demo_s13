package com.educhaap.edulinkup.Controlador

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.educhaap.edulinkup.Modelo.Usuario
import com.educhaap.edulinkup.NuevoAmigo
import com.educhaap.edulinkup.R

class AdaptadorAmigos(val uidUsuario: String, val correoUsuario:String, private val context: Context, private var usuarios: MutableList<Usuario>): RecyclerView.Adapter<AdaptadorAmigos.AmigoViewHolder>(){

    // ViewHolder para el RecyclerView
    class AmigoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewNombre: TextView = itemView.findViewById(R.id.edtNombre)
        val textViewEmail: TextView = itemView.findViewById(R.id.edtEmail)
        val textViewInstitucion: TextView = itemView.findViewById(R.id.tvInstitucion)
        val textViewCarrera: TextView = itemView.findViewById(R.id.tvCarrera)
        val buttomViewAgregarAmigo: TextView = itemView.findViewById(R.id.btnAgregarAmigo)
        var uidUsuarioSeleccionado : String? = ""
    }
    // Método que infla el diseño de los ítems y crea un ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmigoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_usuario, parent, false)
        return AmigoViewHolder(itemView)
    }

    // Método que enlaza los datos de una persona con el ViewHolder
    override fun onBindViewHolder(holder: AmigoViewHolder, position: Int) {
        val amigo = usuarios[position]
        holder.textViewNombre.text = amigo.name
        holder.textViewEmail.text = amigo.email
        holder.uidUsuarioSeleccionado = amigo.uid

        //Creamos un evento al boton del recyclerView
        holder.buttomViewAgregarAmigo.setOnClickListener {
            val nombreAmigo = holder.textViewNombre.text.toString()
            val correoAmigo = holder.textViewEmail.text.toString()
            var NuevoAmigo = NuevoAmigo()
            NuevoAmigo.insertAmigo(uidUsuario, correoUsuario, context,nombreAmigo, correoAmigo, amigo.uid)

            //Limpiamos el recycler
            clear()
        }
    }

    // Método que devuelve la cantidad de ítems en la lista
    override fun getItemCount() = usuarios.size

    // Método para limpiar el RecyclerView
    fun clear() {
        usuarios.clear()
        notifyDataSetChanged()
    }
}

