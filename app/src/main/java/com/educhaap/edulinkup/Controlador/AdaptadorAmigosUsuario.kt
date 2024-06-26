package com.educhaap.edulinkup.Controlador

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.educhaap.edulinkup.ChatActivity
import com.educhaap.edulinkup.Modelo.Amigos
import com.educhaap.edulinkup.Modelo.Usuario
import com.educhaap.edulinkup.NuevoAmigo
import com.educhaap.edulinkup.R

class AdaptadorAmigosUsuario(private val context: Context, private var amigosUsuario: MutableList<Amigos>): RecyclerView.Adapter<AdaptadorAmigosUsuario.AmigoUsuarioViewHolder>() {

    // ViewHolder para el RecyclerView
    class AmigoUsuarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewNombre: TextView = itemView.findViewById(R.id.tvNombreAmigo)
        val textViewEmail: TextView = itemView.findViewById(R.id.tvEmailAmigo)
        val textViewInstitucion: TextView = itemView.findViewById(R.id.tvInstitucionAmigo)
        val textViewCarrera: TextView = itemView.findViewById(R.id.tvCarreraAmigo)
        val buttomViewVerChat: TextView = itemView.findViewById(R.id.btnVerchat)
        var txtUidAmigo: String = ""
    }

    // Método que infla el diseño de los ítems y crea un ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdaptadorAmigosUsuario.AmigoUsuarioViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_usuario_amigo, parent, false)
        return AdaptadorAmigosUsuario.AmigoUsuarioViewHolder(itemView)
    }

    // Método que enlaza los datos de una persona con el ViewHolder
    override fun onBindViewHolder(holder: AdaptadorAmigosUsuario.AmigoUsuarioViewHolder, position: Int) {
        val amigo = amigosUsuario[position]
        holder.textViewNombre.text = amigo.nameAmigo
        holder.textViewEmail.text = amigo.correoAmigo
        holder.txtUidAmigo = amigo.uidAmigo

        //Creamos un evento al boton del recyclerView
        holder.buttomViewVerChat.setOnClickListener {

            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("name", amigo.nameAmigo)
            intent.putExtra("uid", amigo.uidAmigo)
            context.startActivity(intent)
            if (context is Activity) {
                context.finish()
            }
            //Limpiamos el recycler
            clear()
        }
    }

    // Método que devuelve la cantidad de ítems en la lista
    override fun getItemCount() = amigosUsuario.size

    // Método para limpiar el RecyclerView
    fun clear() {
        amigosUsuario.clear()
        notifyDataSetChanged()
    }
}
