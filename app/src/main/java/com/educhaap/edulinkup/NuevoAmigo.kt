package com.educhaap.edulinkup

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.educhaap.edulinkup.Controlador.AdaptadorAmigos
import com.educhaap.edulinkup.Modelo.Amigos
import com.educhaap.edulinkup.Modelo.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class NuevoAmigo : AppCompatActivity() {

    private lateinit var edtBucarAmigos: EditText
    private lateinit var recyclerViewUsuarios: RecyclerView
    private lateinit var adaptadorAmigosFireBase : AdaptadorAmigos

    private var usuariosList = mutableListOf<Usuario>()
    private var db = FirebaseFirestore.getInstance()

    //Instancia para obtener los datos del usuario autentificado
    private lateinit var auth: FirebaseAuth
    //Variables del usuario
    var correoUsuario : String = ""
    var uidUsuario : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_amigo)

        edtBucarAmigos = findViewById(R.id.edtBuscarAmigo);
        recyclerViewUsuarios = findViewById(R.id.rvAgregarAmigos)

        recyclerViewUsuarios.layoutManager = LinearLayoutManager(this)

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

        //Inicializamos el adaptador
        adaptadorAmigosFireBase = AdaptadorAmigos(uidUsuario,correoUsuario, this,usuariosList)
        recyclerViewUsuarios.adapter = adaptadorAmigosFireBase

    }

    fun startMisAmigosActivity(view : View)
    {
        val intent = Intent(this, MisAmigos::class.java)
        startActivity(intent)
        finish()
    }

    //Funcion que busca a los usuarios por nombre
    fun getUsuarios(view: View){
        val amigosRef = db.collection( "usuarios")
        val UsuarioBusqueda = edtBucarAmigos.text.toString();
        // Crear límites para la consulta de prefijo
        val endPrefijo = UsuarioBusqueda + "\uf8ff"

        amigosRef.orderBy("name")
            .startAt(UsuarioBusqueda)
            .endAt(endPrefijo)
            .get()
            .addOnSuccessListener { resultado ->
                usuariosList.clear()
                val usuariosList = mutableListOf<Usuario>()
                for( registro in resultado ){
                    var NombreUsuario = registro.getString("name")
                    var uid = registro.getString("uid")
                    var correo = registro.getString("email")

                    if (NombreUsuario != null && uid != null && correo != null) {
                        usuariosList.add(Usuario(uid, correo, NombreUsuario, "", "", "", uid, ""))
                    }

                }
                adaptadorAmigosFireBase = AdaptadorAmigos(uidUsuario,correoUsuario, this,usuariosList)
                recyclerViewUsuarios.adapter = adaptadorAmigosFireBase


            }
            .addOnFailureListener { e ->
                // Manejar el error
            }
    }

    //Funcion que guarda a los usuarios como amigos
    fun insertAmigo(uidUsuario:String, correoUsuario: String, contex: Context, nombreAmigo: String, correAmigo: String, uidUsuarioSeleccionado : String?) {

        try
        {
            if(correoUsuario == "" && uidUsuario == "")
            {
                Toast.makeText(contex, "Sesión Expirada vuelva a iniciar sesión", Toast.LENGTH_SHORT).show()
            }
            else
            {
                //Llenamos un objeto
                var uidAmigoSeleccionado : String = uidUsuarioSeleccionado ?: "UID DESCONOCIDO"
                var amigoAgregado = Amigos(correAmigo, nombreAmigo,uidAmigoSeleccionado,correoUsuario,"",uidUsuario,"","")

                //Insertamos el registro
                db.collection("amigos")
                    .add(amigoAgregado)
                    .addOnSuccessListener {
                        Toast.makeText(contex, "Amigo guardado", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(contex, "Error al guardar amigo "+e.message, Toast.LENGTH_SHORT).show()
                    }
            }

        }
        catch(ex : Exception)
        {
            Toast.makeText(contex, "No hay un usuario autenticado vuelva a iniciar sesión", Toast.LENGTH_SHORT).show()
        }




    }
}