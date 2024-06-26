package com.educhaap.edulinkup.Controlador

import android.content.ComponentCallbacks
import android.content.Context
import android.provider.CalendarContract.Instances
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.educhaap.edulinkup.Modelo.Conversacion
import com.educhaap.edulinkup.Modelo.Mensaje
import com.educhaap.edulinkup.Modelo.Usuario
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.sql.Time

class ServicesChat(private val context: Context) {

    private lateinit var db: FirebaseFirestore
    private lateinit var listaMensaje: MutableList<Mensaje>
    private lateinit var adapter: AdaptadorMensajes
    val auth = FirebaseAuth.getInstance()
    val senderUid = auth.currentUser?.uid

    //configuracion del recyclerView para envio de mensajes
    fun setupRecyclerViewChat(recyclerView: RecyclerView) {
        listaMensaje = ArrayList()
        adapter = AdaptadorMensajes(listaMensaje)
        db = FirebaseFirestore.getInstance()

        //Mostrar mensajes en el recycler view
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    //Agregando mensajes a la base de datos
    fun enviarMensaje(mensaje: String, recibeUid: String,currentTime: Long) {
        try {
            val mensajeObj = Mensaje(senderUid!!,recibeUid,mensaje,currentTime)
            db.collection("mensaje").add(mensajeObj).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    listarMensaje(recibeUid)
                    Toast.makeText(context, "Mensaje enviado", Toast.LENGTH_SHORT).show()
                } else {
                    handleError("Error al enviar mensaje al usuario solicitado: ${task.exception?.message}")
                }
            }
        } catch (e: Exception) {
            handleError("Error al momento de enviar mensajes: ${e.message}")
        }
    }

    //listar mensajes en pantalla
    fun listarMensaje(recibeUid: String) {
        try {
            db.collection("mensaje")
                .whereIn("enviaUid", listOf(senderUid, recibeUid))
                .whereIn("recibeUid", listOf(senderUid, recibeUid))
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error == null) {
                        listaMensaje.clear()
                        if (snapshot != null && !snapshot.isEmpty) {
                            val mensajeList = snapshot.toObjects(Mensaje::class.java)
                            listaMensaje.addAll(mensajeList)
                        } else {
                            Log.d("ServicesChat", "No se encontraron mensajes entre los usuarios")
                        }
                        adapter.notifyDataSetChanged()
                    } else {
                        handleError("Error al listar mensajes de la base de datos")
                    }
                }
        } catch (e: Exception) {
            handleError("Error al momento de listar mensajes: ${e.message}")
        }
    }

    private fun handleError(message: String) {
        Log.e("ServicesChat", message)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // nuevo codigo para el manejo del ultimo mensaje
    fun obtenerUltimoMensaje(recibeUid: String, callback: (String,Int)-> Unit){
        db.collection("mensaje")
            .whereEqualTo("enviaUid", senderUid)
            .whereEqualTo("recibeUid", recibeUid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val ultimoMensaje = snapshot.documents[0].getString("mensaje") ?: ""
                    val mensajesNoLeidos = snapshot.documents.count { !it.getBoolean("leido")!! }
                    callback(ultimoMensaje, mensajesNoLeidos)
                } else {
                    callback("No hay mensajes", 0)
                }
            }
            .addOnFailureListener { exception ->
                handleError("Error al obtener el último mensaje: ${exception.message}")
                callback("Error", 0)
            }
    }
}