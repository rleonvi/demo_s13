package com.educhaap.edulinkup.Controlador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.educhaap.edulinkup.Modelo.Mensaje
import com.educhaap.edulinkup.R
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdaptadorMensajes(private val mensajeList:MutableList<Mensaje>): RecyclerView.Adapter<ViewHolder>() {

    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if(viewType == 1){
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_mensaje_recibir,parent,false)
            return ReceiveViewHolder(view)
        }else{
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_mensaje_enviar,parent,false)
            return SentViewHolder(view)
        }
    }

    override fun getItemCount(): Int  = mensajeList.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentMessage = mensajeList[position]

        if(holder is SentViewHolder){
            holder.enviarMensaje.text = currentMessage.contenido
            holder.timestamp.text = currentMessage.timestamp?.let { formatTimesTamp(it) }

        } else if (holder is ReceiveViewHolder) {
            holder.recibirMensaje.text = currentMessage.contenido
            holder.timestamp.text = currentMessage.timestamp?.let { formatTimesTamp(it) }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val user = FirebaseAuth.getInstance()
        val currentMensaje = mensajeList[position]
        if(user.currentUser?.uid.equals(currentMensaje.enviaUid)){
            return ITEM_SENT
        }else{
            return ITEM_RECEIVE
        }
    }

    class SentViewHolder(itemView: View) : ViewHolder(itemView){
        val enviarMensaje = itemView.findViewById<TextView>(R.id.tvMensajeEnviado)
        val timestamp = itemView.findViewById<TextView>(R.id.tvTimestampEnviado)
    }

    class ReceiveViewHolder(itemView: View) : ViewHolder(itemView){
        val recibirMensaje = itemView.findViewById<TextView>(R.id.tvMensajeRecibido)
        val timestamp = itemView.findViewById<TextView>(R.id.tvTimestampRecibido)
    }

    private fun formatTimesTamp(timestamp: Long): String{
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}