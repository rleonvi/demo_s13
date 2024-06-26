package com.educhaap.edulinkup.Controlador

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.educhaap.edulinkup.Modelo.Carrera
import com.educhaap.edulinkup.R

//Adaptador personalizado que permitira que el spinner reciba una lista de modelos y no solo una lista de strings
//este adaptador recibe un diseño personalizado de spinner llamando a la plantilla que creamos llamada spinner_item
class AdaptadorCarreras (contex : Context, private val carreras: List<Carrera>) : ArrayAdapter<Carrera>(contex, 0, carreras)
{
    // Método que crea y retorna la vista para un elemento del Spinner
    private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup, resource: Int): View {
        // Reutilizar la vista si es posible, de lo contrario inflar una nueva
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)
        // Obtener referencia al TextView dentro de la vista
        val textView = view.findViewById<TextView>(R.id.spinnerTextView)
        // Establecer el texto del TextView con el nombre de la institución
        textView.text = carreras[position].nombreCarrera
        return view
    }

    // Método para obtener la vista que se mostrará cuando el Spinner no esté desplegado
    //Este metodo esta sobre escrito para que tome su nueva fuente de datos y nuevo estilo de spinner
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent, R.layout.spinner_item)
    }

    // Método para obtener la vista que se mostrará cuando el Spinner esté desplegado
    //Este metodo esta sobre escrito para que tome su nueva fuente de datos y nuevo estilo de spinner
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent, R.layout.spinner_item)
    }
}