package com.educhaap.edulinkup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.educhaap.edulinkup.Controlador.ServicesMain
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {
    private lateinit var servicesMain: ServicesMain
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponents()

        setSupportActionBar(toolbar)
        servicesMain.setupRecyclerView(recyclerView)
    }

    private fun initComponents() {
        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.recyclerViewChats)
        servicesMain = ServicesMain(this)
    }


    //Metodos para mostrar actionBar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return servicesMain.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return servicesMain.onOptionsItemSelected(item)
    }

    fun startMisAmigosActivity(view : View)
    {
        val intent = Intent(this, MisAmigos::class.java)
        startActivity(intent)
    }
}