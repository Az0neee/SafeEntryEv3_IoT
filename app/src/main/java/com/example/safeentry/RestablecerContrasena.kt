package com.example.safeentry

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.safeentry.databinding.ActivityRestablecerContrasenaBinding
import com.google.firebase.auth.FirebaseAuth

class RestablecerContrasena : AppCompatActivity() {

    private lateinit var binding: ActivityRestablecerContrasenaBinding // Vinculación con el diseño XML
    private var correo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestablecerContrasenaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtiene el correo enviado desde la actividad anterior
        correo = intent.getStringExtra("correo")

        // Configura el botón para restablecer la contraseña
        binding.btnRestablecer.setOnClickListener {
            val nuevaContrasena = binding.etContrasenaNueva.text.toString().trim() // Obtiene la nueva contraseña
            val confirmarContrasena = binding.etConfirmarContrasena.text.toString().trim() // Obtiene la confirmación de la contraseña
            // Verifica que las contraseñas coincidan y no estén vacías
            if (nuevaContrasena.isNotEmpty() && nuevaContrasena == confirmarContrasena) {
                // Si las contraseñas coinciden, intenta restablecerla
                restablecerContrasena(nuevaContrasena)
            } else {
                // Si no coinciden, muestra un mensaje de error
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Método para restablecer la contraseña en Firebase
    private fun restablecerContrasena(nuevaContrasena: String) {
        if (correo.isNullOrEmpty()) {
            Toast.makeText(this, "Correo no válido", Toast.LENGTH_SHORT).show()
            return
        }

        val user = FirebaseAuth.getInstance().currentUser

        // Verifica que el usuario esté autenticado y que su correo coincida con el proporcionado
        if (user != null && user.email == correo) {
            user.updatePassword(nuevaContrasena)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Contraseña restablecida exitosamente", Toast.LENGTH_SHORT).show()

                        // Redirige al usuario a la pantalla de inicio de sesión
                        val intent = Intent(this, IniciarSesion::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Si ocurre un error durante la actualización
                        Toast.makeText(this, "Error al restablecer la contraseña: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            // Si no se pudo autenticar al usuario
            Toast.makeText(this, "No se pudo autenticar al usuario", Toast.LENGTH_SHORT).show()
        }
    }
}
