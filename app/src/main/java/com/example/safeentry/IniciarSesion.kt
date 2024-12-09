package com.example.safeentry

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.safeentry.databinding.ActivityIniciarSesionBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class IniciarSesion : AppCompatActivity() {

    private lateinit var binding: ActivityIniciarSesionBinding

    // Declarar FirebaseAuth para la autenticación de usuario
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityIniciarSesionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicia FirebaseAuth para autenticar al usuario
        auth = FirebaseAuth.getInstance()

        // Configurar el botón de inicio de sesión
        binding.btnIniciarSesion.setOnClickListener {
            // Obtener los valores del correo y la contraseña desde los campos de entrada
            val correo = binding.etCorreo.text.toString().trim()
            val contrasena = binding.etContrasena.text.toString().trim()

            // Validar si el correo está vacío
            if (correo.isEmpty()) {
                binding.tilCorreo.error = "Por favor ingresa tu correo."
                return@setOnClickListener
            } else {
                binding.tilCorreo.isErrorEnabled = false
            }

            // Validar si la contraseña está vacía
            if (contrasena.isEmpty()) {
                binding.tilContrasena.error = "Por favor ingresa tu contraseña."
                return@setOnClickListener
            } else {
                binding.tilContrasena.isErrorEnabled = false
            }

            // Llamar a la función de iniciar sesión con el correo y la contraseña
            iniciarSesion(correo, contrasena)
        }

        // Configurar el enlace para la pantalla de recuperación de contraseña
        binding.OlvidasteContrasena.setOnClickListener {
            val intent = Intent(this, OlvidasteContrasena::class.java)
            startActivity(intent)
        }

        // Configurar el enlace para la pantalla de registro
        binding.TvRegistrar.setOnClickListener {
            val intent = Intent(this, Registrar::class.java)
            startActivity(intent)
        }
    }

    // Función para iniciar sesión con Firebase
    private fun iniciarSesion(correo: String, contrasena: String) {
        auth.signInWithEmailAndPassword(correo, contrasena)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Si el inicio de sesión es exitoso, redirigir al usuario a la pantalla principal
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Finalizar la actividad actual
                } else {
                    // Si el inicio de sesión falla, mostrar un mensaje de error
                    Snackbar.make(binding.root, "Correo o contraseña incorrectos", Snackbar.LENGTH_LONG).show()
                }
            }
    }
}
