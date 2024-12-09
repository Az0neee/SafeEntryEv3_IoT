package com.example.safeentry

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.safeentry.databinding.ActivityOlvidasteContrasenaBinding

class OlvidasteContrasena : AppCompatActivity() {

    private lateinit var binding: ActivityOlvidasteContrasenaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOlvidasteContrasenaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRecuperar.setOnClickListener {
            val correo = binding.etCorreo.text.toString().trim()

            if (correo.isNotEmpty()) {
                // Verificar si el correo existe (esto lo puedes hacer verificando con la base de datos)
                if (correoExisteEnBaseDeDatos(correo)) {
                    // Redirigir a la actividad de restablecer contraseña
                    val intent = Intent(this, RestablecerContrasena::class.java)
                    intent.putExtra("correo", correo)
                    startActivity(intent)
                } else {
                    // Mostrar mensaje de error si el correo no existe
                    Toast.makeText(this, "Correo no registrado", Toast.LENGTH_SHORT).show()
                }
            } else {
                binding.etCorreo.error = "Por favor ingresa un correo"
            }
        }
    }

    // Método ficticio para verificar si el correo está en la base de datos
    private fun correoExisteEnBaseDeDatos(correo: String): Boolean {
        // proximamente se agregara la verificacion en la base de datos ya que ahora solo pasa igual cambia
        // la contraseña si el correo ingresado existe.
        return true
    }
}