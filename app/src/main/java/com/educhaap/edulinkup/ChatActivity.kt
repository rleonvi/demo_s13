package com.educhaap.edulinkup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.educhaap.edulinkup.Controlador.AdaptadorMensajes
import com.educhaap.edulinkup.Controlador.ServicesChat
import com.educhaap.edulinkup.Modelo.Mensaje
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration

class ChatActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var tvName:TextView
    private lateinit var mensaje: EditText
    private lateinit var btnEnviar: ImageButton
    private lateinit var recyclerViewChat: RecyclerView
    private lateinit var servicesChat: ServicesChat
    private lateinit var btnRegresar: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        initComponets()
        initChatService()
    }

    //Inicializar componentes
    private fun initComponets(){
        mensaje = findViewById(R.id.etMensaje)
        btnEnviar = findViewById(R.id.btnEnviar)
        recyclerViewChat = findViewById(R.id.recyclerViewMessages)
        tvName = findViewById(R.id.tvChatName)
        btnRegresar = findViewById(R.id.btnRegresar)
        servicesChat = ServicesChat(this)
        auth  = FirebaseAuth.getInstance()
    }

    //Metodo para iniciar servicios de chat
    private fun initChatService() {
        servicesChat.setupRecyclerViewChat(recyclerViewChat)

        val name = intent.getStringExtra("name")
        val receiveUid = intent.getStringExtra("uid")

        servicesChat.listarMensaje(receiveUid!!)
        supportActionBar?.title = name
        tvName.text = name

        //Boton para regresar pantalla anterior
        btnRegresar.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        //Boton para enviar mensajes
        btnEnviar.setOnClickListener {
            val mensajeTexto = mensaje.text.toString()
            val currentTime = System.currentTimeMillis()
            servicesChat.enviarMensaje(mensajeTexto,receiveUid,currentTime)
            mensaje.setText("")
        }
    }
}