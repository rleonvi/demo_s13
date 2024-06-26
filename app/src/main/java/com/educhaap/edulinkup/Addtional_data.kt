package com.educhaap.edulinkup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.educhaap.edulinkup.Controlador.AdaptadorCarreras
import com.educhaap.edulinkup.Controlador.AdaptadorInstituciones
import com.educhaap.edulinkup.Controlador.AuthManager
import com.educhaap.edulinkup.Modelo.Carrera
import com.educhaap.edulinkup.Modelo.Institucion
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class Addtional_data : AppCompatActivity()
{
    //Creamos las referencias
    private lateinit var txtPrimerNombre : EditText
    private lateinit var txtSegundoNombre : EditText
    private lateinit var txtPrimerApellido : EditText
    private lateinit var txtSegundoApellido : EditText
    private lateinit var spInstituciones : Spinner
    private lateinit var spCarreras : Spinner
    private var selectedInstitution: String? = null

    //Datos del intent
    private lateinit var email : String
    private lateinit var name : String
    private lateinit var uid : String
    private lateinit var providerID : String


    //Instancia para firebase
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addtional_data)

        var tag : String = "AddtionalDataActivity"
        //Inicializamos las referencias con los objetos del XML
        txtPrimerNombre = findViewById(R.id.edtPrimerNombre);
        txtSegundoNombre = findViewById(R.id.edtSegundoNombre);
        txtPrimerApellido = findViewById(R.id.edtPrimerApellido);
        txtSegundoApellido = findViewById(R.id.edtSegundoApellido);

        //Inicializamos el spinner de instituciones
        spInstituciones = findViewById(R.id.spInstitucion)
        spCarreras = findViewById(R.id.spCarrera)

        try
        {
            // Obtener el Intent que inició esta actividad
            val intent = intent
            //Validamos que no sean nulos con el operador Elvis
            providerID = intent.getStringExtra("EXTRA_PROVIDER_ID") ?: "Desconocido"
            email = intent.getStringExtra("EXTRA_EMAIL") ?: "Desconocido"
            name = intent.getStringExtra("EXTRA_NAME") ?: "Desconocido"
            uid = intent.getStringExtra("EXTRA_UID") ?: "Desconocido"

            //Este metodo llena los spinneres
            getInstituciones()

            // Configurar el evento de selección de ítems del Spinner para instituciones
            spInstituciones.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    val selectedInstitucion = parent.getItemAtPosition(position) as Institucion
                    //Toast.makeText(this@Addtional_data, "Seleccionado: ${selectedInstitucion.nombreInstitucion}", Toast.LENGTH_SHORT).show()

                    //Llamamos dentro de institucion la funcion que llena mi spinner de carreras
                    getCarreras(selectedInstitucion.codigoInstitucion)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // No hacer nada si no se selecciona ningún ítem
                }
            }

        }
        catch (ex : Exception)
        {
            //Registramos un mensaje de depuracion
            Log.e(tag,"Error inesperado en "+ex.message);
        }
    }

    //Funcion que llena los spiner de instituciones
    fun getInstituciones()
    {
        //Creamos un objeto de la clase instituciones

        // Referencia a la colección de Firebase
        val institucionesRef = db.collection("instituciones")

        //Obtenemos los datos de la coleccion
        institucionesRef.get()
            .addOnSuccessListener { result ->
                val institucionesList = mutableListOf<Institucion>()

                //Recorremos los documentos y agregamos los nombres a la lista
                for(document in result)
                {
                    var codigo = document.getLong("codigoInstitucion")
                    val nombre = document.getString("nombreInstitucion")
                    val abrevitura = document.getString("abreviatura")

                    if(codigo != null && nombre != null && abrevitura != null)
                    {
                        //Agregamos la institucion a la lista
                        institucionesList.add(Institucion(codigo.toInt(),nombre,abrevitura))
                    }
                }

                // Crear un ArrayAdapter usando la lista de instituciones y un layout predeterminado de Spinner
                //val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, institucionesList)

                //Llamamos a nuestro adaptador personalizado para poder enviarle un objeto como fuente de datos
                //Mi adaptador recibe el contexto y su lista de objetos
                val adapterPersonalizado = AdaptadorInstituciones(this, institucionesList)

                // Aplicar el adaptador al Spinner
                spInstituciones.adapter = adapterPersonalizado
            }
            .addOnFailureListener() { exception ->
                // Manejar el error en caso de que ocurra
                exception.printStackTrace()
            }
    }

    //Funcion que llena los spiner de carreras y lo filtra por institucion
    fun getCarreras(codigo_institucion : Int)
    {
        //Inicializamos el spinner
        spCarreras = findViewById(R.id.spCarrera)

        // Referencia a la colección de Firebase
        val carrerasRef = db.collection("carreras")

        //Obtenemos los datos de la coleccion
        carrerasRef.get()
            .addOnSuccessListener { result ->
                //Creamos una lista de objetos de carrera
                val carrerasList = mutableListOf<Carrera>()

                //Recorremos los documentos y agregamos los nombres a la lista
                for(document in result)
                {
                    val codigoInstitucion = document.getLong("codigoInstitucion")
                    val codigoCarrera = document.getLong("codigoCarrera")
                    val nombreCarrera = document.getString("nombreCarrera")

                    if(codigoInstitucion != null)
                    {
                        if(codigo_institucion == codigoInstitucion.toInt() && nombreCarrera != null && codigoCarrera != null && codigoInstitucion != null)
                        {
                            //Llenamos un objeto de carrera
                            var carrera = Carrera(codigoCarrera.toInt(),codigoInstitucion.toInt(), nombreCarrera)

                            //Agregamos el objeto carrera a la lista
                            carrerasList.add(carrera)
                        }
                    }

                }

                // Crear un ArrayAdapter usando la lista de instituciones y un layout predeterminado de Spinner
                //val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, institucionesList)

                //Llamamos a nuestro adaptador personalizado para poder enviarle un objeto como fuente de datos
                //Mi adaptador recibe el contexto y su lista de objetos
                val adapterPersonalizado = AdaptadorCarreras(this, carrerasList)

                // Aplicar el adaptador al Spinner
                spCarreras.adapter = adapterPersonalizado
            }
            .addOnFailureListener() { exception ->
                // Manejar el error en caso de que ocurra
                exception.printStackTrace()
            }
    }

    //funcion de prueba con adapter personalizado
    fun llenarInstituciones()
    {
        // Crear lista de objetos Institucion con codigoInstitucion como entero
        val institucionesList = listOf(
            Institucion(1, "Institucion A"),  // Crear un objeto Institucion
            Institucion(2, "Institucion B"),
            Institucion(3, "Institucion C")
        )

        // Crear el adaptador personalizado con la lista de instituciones
        val adapter = AdaptadorInstituciones(this, institucionesList)

        // Asignar el adaptador al Spinner
        spInstituciones.adapter = adapter
    }

    fun InsertarDatosAdicionales(v : View)
    {
        try
        {
            //Agarramos los datos adicionales
            // Obtener el ítem seleccionado del Spinner
            var selectedInstitucion = spInstituciones.selectedItem as Institucion
            var selectedCarrera = spCarreras.selectedItem as Carrera
            var primerNombre = txtPrimerNombre.text.toString()
            var segundoNombre = txtSegundoNombre.text.toString()
            var primerApellido = txtPrimerApellido.text.toString()
            var segundoApellido = txtSegundoApellido.text.toString()

            if(selectedInstitucion == null || selectedCarrera == null || primerNombre == null || segundoNombre == null || primerApellido == null || segundoApellido == null)
            {
                Toast.makeText(this, "Llene todos los campos para poder continuar", Toast.LENGTH_SHORT).show()
            }
            else
            {
                //si todo esta bien redireccionamos a la funcion de guardar
                //Creamos el nombre completo del usuario
                name = primerNombre + " " + segundoNombre + " " + primerApellido + " " + segundoApellido
                name = name.trim()
                val AuthManager = AuthManager(this)
                AuthManager.saveUserToFirestore(providerID, uid, email, name, primerNombre, segundoNombre, primerApellido, segundoApellido, selectedInstitucion.codigoInstitucion, selectedCarrera.codigoCarrera)
            }
        }
        catch(ex : Exception)
        {
            Toast.makeText(this, "Error: ${ex.message}", Toast.LENGTH_SHORT).show()
        }

        // Mostrar un Toast con el nombre de la institución seleccionada
        //Toast.makeText(this, "Seleccionado: ${selectedInstitucion.codigoInstitucion}", Toast.LENGTH_SHORT).show()


    }
}
