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

class AdaptadorChats(private val context: Context, private var amigosUsuario: MutableList<Amigos>, private val onUserClick: (Amigos) -> Unit): RecyclerView.Adapter<AdaptadorChats.chatViewHolder>() {

    // ViewHolder para el RecyclerView
    class chatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val chatName = itemView.findViewById<TextView>(R.id.chatNombre)
        var txtUidAmigo: String = ""

        fun bind(amigo: Amigos, onUserClick: (Amigos) -> Unit) {
            chatName.text = amigo.nameAmigo
            //chatUltimoMensaje.text = user.ultimoMensaje ?: "No hay mensajes"

            //mensajeNoLeido.visibility = if (user.mensajeNoLeido > 0) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                onUserClick(amigo)
            }
        }
    }

    // Método que infla el diseño de los ítems y crea un ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdaptadorChats.chatViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return AdaptadorChats.chatViewHolder(itemView)
    }

    // Método que enlaza los datos de una persona con el ViewHolder
    override fun onBindViewHolder(holder: AdaptadorChats.chatViewHolder, position: Int) {
        val amigo = amigosUsuario[position]
        holder.bind(amigo,onUserClick)
        holder.txtUidAmigo = amigo.uidAmigo

    }

    // Método que devuelve la cantidad de ítems en la lista
    override fun getItemCount() = amigosUsuario.size

    // Método para limpiar el RecyclerView
    fun clear() {
        amigosUsuario.clear()
        notifyDataSetChanged()
    }
}
