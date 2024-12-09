package com.example.safeentry

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.safeentry.Models.Puertas
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var linearLayoutButtons: LinearLayout
    private lateinit var botonAñadirPuerta: Button
    private lateinit var database: DatabaseReference
    private var contadorPuertas = 0
    private val maxPuertas = 5
    private var seAgregoPuerta = false // Bandera para evitar múltiples inserciones

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicia Firebase Database y las vistas
        linearLayoutButtons = findViewById(R.id.linear_layout_buttons)
        botonAñadirPuerta = findViewById(R.id.boton_añadir_puerta)
        database = FirebaseDatabase.getInstance().reference

        // Configura el botón para añadir puertas
        botonAñadirPuerta.setOnClickListener {
            AgregarPuerta()
        }

        // Cargar puertas desde Firebase solo una vez
        cargarPuertasDesdeFirebase()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.barra_navegacion)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    Toast.makeText(this, "Ya estás en Inicio", Toast.LENGTH_SHORT).show()
                    true // Muestra un mensaje indicando que se seleccionó "Inicio"
                }
                R.id.nav_registro -> {
                    val intent = Intent(this, Registro_puerta::class.java)
                    startActivity(intent) // Cambia a la actividad de registro
                    true
                }
                R.id.nav_ayuda -> {
                    val intent = Intent(this, ayuda::class.java)
                    startActivity(intent) // Cambia a la actividad de Ayuda
                    true
                }
                R.id.nav_configuracion -> {
                    val intent = Intent(this, Configuracion::class.java)
                    startActivity(intent) // Cambia a la actividad de Configuración
                    true
                }
                R.id.nav_perfil -> {
                    val intent = Intent(this, MiPerfil::class.java)
                    startActivity(intent) // Cambia a la actividad de Perfil
                    true
                }
                else -> false
            }
        }
    }


    private fun AgregarPuerta() {
        if (contadorPuertas >= maxPuertas) {
            Toast.makeText(this, "Límite de puertas alcanzado", Toast.LENGTH_SHORT).show()
            return
        }

        val editTextNombre = EditText(this).apply {
            hint = "Nombre de la puerta"
        }

        AlertDialog.Builder(this)
            .setTitle("Añadir Puerta")
            .setView(editTextNombre)
            .setPositiveButton("Añadir") { _, _ ->
                val nombrePuerta = editTextNombre.text.toString().trim()
                if (nombrePuerta.isNotEmpty()) {
                    verificarSiPuertaExisteEnFirebase(nombrePuerta) { existe ->
                        if (existe) {
                            Toast.makeText(this, "Ya existe una puerta con ese nombre", Toast.LENGTH_SHORT).show()
                        } else {
                            val nuevaPuerta = Puertas(nombre = nombrePuerta)
                            añadirPuerta(nuevaPuerta)
                        }
                    }
                } else {
                    Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    // Comprueba si una puerta ya existe en Firebase
    private fun verificarSiPuertaExisteEnFirebase(nombrePuerta: String, callback: (Boolean) -> Unit) {
        database.child("puertas").child(nombrePuerta).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                callback(snapshot.exists())
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false)
            }
        })
    }

    // Añade una nueva puerta a Firebase y la muestra en la UI
    private fun añadirPuerta(puerta: Puertas) {
        if (seAgregoPuerta) {
            Toast.makeText(this, "Espere a que se complete la operación anterior", Toast.LENGTH_SHORT).show()
            return
        }

        seAgregoPuerta = true // Evita múltiples inserciones simultáneas
        guardarPuertaEnFirebase(puerta) // Guarda la puerta en Firebase
        mostrarPuertaEnUI(puerta) // La añade a la UI
    }

    // Muestra una puerta en la interfaz de usuario
    private fun mostrarPuertaEnUI(puerta: Puertas) {
        val layoutPuerta = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 16, 0, 16)
            }
        }

        val textViewNombrePuerta = TextView(this).apply {
            text = puerta.nombre
            textSize = 18f
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.5f
            ).apply {
                gravity = Gravity.CENTER
                setMargins(20, 0, 10, 0)
            }
        }

        val buttonPuerta = Button(this).apply {
            text = if (puerta.activa) "Activado" else "Desactivado"
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.3f).apply {
                setMargins(10, 0, 20, 0)
            }
            setBackgroundResource(if (puerta.activa) R.drawable.boton_activado else R.drawable.boton_desactivado)
            setOnClickListener {
                confirmarAccion(
                    "¿Seguro que deseas ${if (puerta.activa) "desactivar" else "activar"} ${puerta.nombre}?",
                    onConfirm = {
                        puerta.activa = !puerta.activa
                        text = if (puerta.activa) "Activado" else "Desactivado"
                        setBackgroundResource(if (puerta.activa) R.drawable.boton_activado else R.drawable.boton_desactivado)
                        actualizarEstadoPuertaEnFirebase(puerta)
                    }
                )
            }
        }

        layoutPuerta.addView(textViewNombrePuerta)
        layoutPuerta.addView(buttonPuerta)
        linearLayoutButtons.addView(layoutPuerta)
    }

    // Guarda la puerta en Firebase
    private fun guardarPuertaEnFirebase(puerta: Puertas) {
        val idUnico = database.child("puertas").push().key // Genera un ID único
        if (idUnico != null) {
            puerta.id = idUnico // Asignamos el ID generado a la puerta

            database.child("puertas").child(idUnico).setValue(puerta)
                .addOnSuccessListener {
                    contadorPuertas++
                    seAgregoPuerta = false
                    Toast.makeText(this, "${puerta.nombre} añadida correctamente ", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    seAgregoPuerta = false
                    Toast.makeText(this, "Error al guardar puerta: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            seAgregoPuerta = false
            Toast.makeText(this, "Error al generar ID único para la puerta", Toast.LENGTH_SHORT).show()
        }
    }

    // Carga puertas existentes desde Firebase al iniciar
    private fun cargarPuertasDesdeFirebase() {
        database.child("puertas").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                linearLayoutButtons.removeAllViews()
                if (snapshot.exists()) {
                    contadorPuertas = snapshot.childrenCount.toInt()
                    snapshot.children.forEach { data ->
                        val puerta = data.getValue(Puertas::class.java)
                        if (puerta != null) {
                            puerta.id = data.key ?: "" // Asigna el ID desde la clave en Firebase
                            mostrarPuertaEnUI(puerta) // Solo añade a la UI
                        }
                    }
                } else {
                    Toast.makeText(this@MainActivity, "No hay puertas para cargar", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error al cargar puertas: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Muestra un cuadro de confirmación para realizar una acción
    private fun confirmarAccion(mensaje: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setMessage(mensaje)
            .setPositiveButton("Sí") { _, _ -> onConfirm() }
            .setNegativeButton("No", null)
            .create()
            .show()
    }

    // Actualiza el estado de una puerta en Firebase
    private fun actualizarEstadoPuertaEnFirebase(puerta: Puertas) {
        if (puerta.id != null) {
            database.child("puertas").child(puerta.id!!).child("activa").setValue(puerta.activa)
                .addOnSuccessListener {
                    Toast.makeText(this, "Estado de la puerta actualizado correctamente", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { error ->
                    Toast.makeText(this, "Error al actualizar estado: ${error.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Error: La puerta no tiene un ID válido", Toast.LENGTH_SHORT).show()
        }
    }
}
