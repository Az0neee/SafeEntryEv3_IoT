package com.example.safeentry

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class Registro_puerta : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_puerta)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.barra_navegacion)
        // Asocia la barra de navegación inferior con el diseño
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent) // Navega a la actividad principal
                    true
                }
                R.id.nav_registro -> {
                    Toast.makeText(this, "Registro seleccionado", Toast.LENGTH_SHORT).show()
                    true  // Muestra un mensaje indicando que se seleccionó "Registro"
                }
                R.id.nav_ayuda -> {
                    val intent = Intent(this, ayuda::class.java)
                    startActivity(intent) // Navega a la actividad de ayuda
                    true
                }
                R.id.nav_configuracion -> {
                    val intent = Intent(this, Configuracion::class.java)
                    startActivity(intent) // Navega a la actividad de configuracion
                    true
                }
                R.id.nav_perfil -> {
                    val intent = Intent(this, MiPerfil::class.java)
                    startActivity(intent) // Navega a la actividad de perfil
                    true
                }
                else -> false
            }
        }
    }
}