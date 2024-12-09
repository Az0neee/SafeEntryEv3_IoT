package com.example.safeentry

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MiPerfil : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mi_perfil)

        // Asocia la barra de navegación inferior con el diseño
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.barra_navegacion)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent) // Navega a la actividad principal
                    true
                }
                R.id.nav_registro -> {
                    val intent = Intent(this, Registro_puerta::class.java)
                    startActivity(intent) // Navega a la actividad de registro de puertas
                    true
                }
                R.id.nav_ayuda -> {
                    val intent = Intent(this, ayuda::class.java)
                    startActivity(intent) // Navega a la actividad de ayuda
                    true
                }
                R.id.nav_configuracion -> {
                    val intent = Intent(this, Configuracion::class.java)
                    startActivity(intent) // Navega a la actividad de configuración
                    true
                }
                R.id.nav_perfil -> {
                    Toast.makeText(this, "Perfil seleccionado", Toast.LENGTH_SHORT).show()
                    true // Muestra un mensaje indicando que se seleccionó "Perfil"
                }
                else -> false
            }
        }

        // Obtener el botón de cerrar sesión
        val btnCerrarSesion = findViewById<Button>(R.id.btn_cerrar_sesion)

        // Configurar el OnClickListener para cerrar sesión
        btnCerrarSesion.setOnClickListener {
            val intent = Intent(this, IniciarSesion::class.java)
            startActivity(intent) // Navega al login
            finish()
        }
    }
}
