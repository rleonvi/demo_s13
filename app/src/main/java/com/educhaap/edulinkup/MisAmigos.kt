package com.educhaap.edulinkup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.educhaap.edulinkup.Controlador.AdaptadorAmigos
import com.educhaap.edulinkup.Controlador.AdaptadorAmigosUsuario
import com.educhaap.edulinkup.Modelo.Amigos
import com.educhaap.edulinkup.Modelo.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class MisAmigos : AppCompatActivity() {

    private var db = FirebaseFirestore.getInstance()

    //Instancia para obtener los datos del usuario autentificado
    private lateinit var auth: FirebaseAuth
    //Variables del usuario
    var correoUsuario : String = ""
    var uidUsuario : String = ""

    private lateinit var recyclerViewAmigos: RecyclerView
    private lateinit var adaptadorAmigosUsuarioFireBase : AdaptadorAmigosUsuario
    private lateinit var contadorAmigos : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_amigos)

        recyclerViewAmigos = findViewById(R.id.rvMisAmigos)
        recyclerViewAmigos.layoutManager = LinearLayoutManager(this)
        contadorAmigos = findViewById(R.id.tvContadorAmigos)

        // Inicializar FirebaseAuth
        auth = FirebaseAuth.getInstance()
        // Obtener el usuario actualmente autenticado
        val currentUser: FirebaseUser? = auth.currentUser
        currentUser?.let { user ->
            uidUsuario = user.uid
            correoUsuario = user.email ?: "Correo no disponible"

            // Mostrar los datos del usuario en un Toast
            Toast.makeText(this, "UID: $uidUsuario, Email: $correoUsuario", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(this, "No hay un usuario autenticado vuelva a iniciar sesión", Toast.LENGTH_SHORT).show()
        }

        //LLenamos recylcer
        getAmigos()
    }

    fun startNuevoAmigosActivity(view : View)
    {
        val intent = Intent(this, NuevoAmigo::class.java)
        startActivity(intent)
        finish()
    }

    //Metodo que consulta a todos los amigos por correo del usuario logeado
    fun getAmigos()
    {
        if(correoUsuario != null && correoUsuario != "")
        {
            //Contador amigos
            var contadorAmigosEncontrados: Int = 0;

            val amigosRef = db.collection( "amigos")
            amigosRef.whereEqualTo("correoUsuario",correoUsuario)
                .get()
                .addOnSuccessListener { resultado ->
                    val amigosUsuarioList = mutableListOf<Amigos>()
                    for( registro in resultado ){
                        var NombreAmigo = registro.getString("nameAmigo")
                        var uidAmigo = registro.getString("uidAmigo")
                        var correoAmigo = registro.getString("correoAmigo")

                        if (NombreAmigo != null && uidAmigo != null && correoAmigo != null) {
                            amigosUsuarioList.add(Amigos(correoAmigo,NombreAmigo,uidAmigo,correoUsuario,"","","",""))

                            contadorAmigosEncontrados = amigosUsuarioList.count()

                            //Actualizamos el textview
                            contadorAmigos.text = contadorAmigosEncontrados.toString() + " Amigos"
                        }

                    }
                    adaptadorAmigosUsuarioFireBase = AdaptadorAmigosUsuario(this, amigosUsuarioList)
                    recyclerViewAmigos.adapter = adaptadorAmigosUsuarioFireBase
                }
                .addOnFailureListener { e ->
                    // Manejar el error
                    Toast.makeText(this, "Ocurrio un error al cargar lista amigos: "+e.message, Toast.LENGTH_SHORT).show()
                }
        }
        else
        {
            Toast.makeText(this, "No hay un usuario autenticado vuelva a iniciar sesión correo no se encuentra", Toast.LENGTH_SHORT).show()
        }
    }
}