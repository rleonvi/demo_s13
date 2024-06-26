package com.educhaap.edulinkup.Controlador

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.educhaap.edulinkup.Modelo.Usuario
import com.educhaap.edulinkup.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdaptadorUsuarios(
    private val context: Context,
    private val userList: List<Usuario>,
    private val onUserClick: (Usuario) -> Unit
):RecyclerView.Adapter<AdaptadorUsuarios.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_chat,parent,false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int =  userList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user,onUserClick)

    }

    class UserViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        private val chatName = itemView.findViewById<TextView>(R.id.chatNombre)
        //private val chatUltimoMensaje = itemView.findViewById<TextView>(R.id.chatUltimoMensaje)
        //private val  mensajeNoLeido = itemView.findViewById<TextView>(R.id.tvMensajeNoLeido)

        fun bind(user: Usuario, onUserClick: (Usuario) -> Unit) {
            chatName.text = user.name
            //chatUltimoMensaje.text = user.ultimoMensaje ?: "No hay mensajes"

            //mensajeNoLeido.visibility = if (user.mensajeNoLeido > 0) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                onUserClick(user)
            }
        }
    }
}