package com.example.safeentry

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.safeentry.databinding.ActivityRegistrarBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class Registrar : AppCompatActivity() {


    private lateinit var binding: ActivityRegistrarBinding

    // Declarar FirebaseAuth
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicia ViewBinding
        binding = ActivityRegistrarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicia FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Configurar el botón para registrar cuenta
        binding.btnCrearCuenta.setOnClickListener {
            val correo = binding.etCorreo.text.toString().trim()
            val contrasena = binding.etPassword.text.toString().trim()
            val contrasenaConfirmar = binding.etPasswordConfirm.text.toString().trim()

            // Validar campos de entrada
            if (correo.isEmpty()) {
                binding.tiCorreo.error = "Por favor ingresa tu correo."
                return@setOnClickListener
            } else {
                binding.tiCorreo.isErrorEnabled = false
            }

            if (contrasena.isEmpty()) {
                binding.tiContrasena.error = "Por favor ingresa tu contraseña."
                return@setOnClickListener
            } else {
                binding.tiContrasena.isErrorEnabled = false
            }

            if (contrasenaConfirmar.isEmpty()) {
                binding.tiPasswordConfirm.error = "Por favor confirma tu contraseña."
                return@setOnClickListener
            } else {
                binding.tiPasswordConfirm.isErrorEnabled = false
            }

            if (contrasena != contrasenaConfirmar) {
                Snackbar.make(it, "Las contraseñas no coinciden.", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Registrar usuario en Firebase
            registrarUsuarioConFirebase(correo, contrasena)
        }

        binding.TvTienesCuenta.setOnClickListener {
            val intent = Intent(this, IniciarSesion::class.java)
            startActivity(intent)
        }
    }

    //Registra el usuario en la base de datos Firebase
    private fun registrarUsuarioConFirebase(correo: String, contrasena: String) {
        auth.createUserWithEmailAndPassword(correo, contrasena)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registro exitoso
                    Snackbar.make(binding.root, "Cuenta creada con éxito.", Snackbar.LENGTH_LONG).show()
                    val intent = Intent(this, IniciarSesion::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Error en el registro
                    Snackbar.make(
                        binding.root,
                        "Hubo un problema al registrar tu cuenta. Inténtalo nuevamente.",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
            .addOnFailureListener { exception ->
                // Manejo de errores
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}
