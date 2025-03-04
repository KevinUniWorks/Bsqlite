package com.example.bsqlite

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
    private  lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)
        setContent {
            var showSplash by remember { mutableStateOf(true) }

            if (showSplash) {
                SplashScreen(onTimeout = {
                    showSplash = false
                })
            } else {
                addUser()
            }
        }
    }

    @Composable
    fun SplashScreen(onTimeout: ( )-> Unit) {

        LaunchedEffect(Unit) {
            delay(3000)
            onTimeout()
        }


        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ){
            Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Logo",
                    modifier = Modifier.size(200.dp)
                )
            }
        }

    }


    @Composable
    fun addUser() {

        var editingUserId by remember { mutableStateOf<Int?>(null) }
        var validationFields = false
        var users by remember { mutableStateOf(dbHelper.getAllUsers()) }

        var name by remember { mutableStateOf("") }
        var last_name by remember { mutableStateOf("") }
        var edad by remember { mutableStateOf("") }
        var genero by remember { mutableStateOf("") }
        val generoOptions = listOf("Masculino", "Femenino", "Otro")
        var email by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var address by remember { mutableStateOf("") }
        var expanded by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf("") }


        Column(modifier = Modifier.padding(25.dp)) {

            val fieldModifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)

            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                isError = name.isEmpty(),
                modifier = fieldModifier
            )
            TextField(
                value = last_name,
                onValueChange = { last_name = it },
                label = { Text("Apellido") },
                isError = last_name.isEmpty(),
                modifier = fieldModifier
            )
            TextField(
                value = edad,
                onValueChange = { edad = it },
                label = { Text("Edad") },
                isError = edad.isEmpty() || edad.toIntOrNull() == null,
                modifier = fieldModifier
            )
            OutlinedTextField(
                value = genero,
                onValueChange = {},
                readOnly = true,
                label = { Text("Género") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Seleccionar Género"
                        )
                    }
                }
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                generoOptions.forEach { selectOption ->
                    DropdownMenuItem(
                        text = { Text(text = selectOption) },
                        onClick = {
                            genero = selectOption
                            expanded = false
                        }
                    )
                }
            }

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                isError = email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches(),
                modifier = fieldModifier
            )
            TextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Teléfono") },
                isError = phone.isEmpty() || phone.length < 10,
                modifier = fieldModifier
            )
            TextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Dirección") },
                isError = address.isEmpty(),
                modifier = fieldModifier
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para registrar al usuario
            Button(
                onClick = {
                    // Validaciones
                    when {
                        name.isEmpty() -> errorMessage = "El nombre no puede estar vacío."
                        last_name.isEmpty() -> errorMessage = "El apellido no puede estar vacío."
                        edad.isEmpty() || edad.toIntOrNull() == null -> errorMessage = "Por favor, ingresa una edad válida."
                        genero.isEmpty() -> errorMessage = "Por favor, selecciona un género."
                        email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> errorMessage = "Por favor, ingresa un correo válido."
                        phone.isEmpty() || phone.length < 10 -> errorMessage = "Por favor, ingresa un número de teléfono válido."
                        address.isEmpty() -> errorMessage = "La dirección no puede estar vacía."
                        else -> {
                            // Si todos los campos son válidos
                            errorMessage = ""
                            validationFields = true
                        }
                    }

                    // Si `validationFields` es verdadero (todos los campos son válidos)
                    if (validationFields) {
                        if (editingUserId == null) {
                            // Registro de un nuevo usuario
                            if (dbHelper.addUser(name, last_name, edad.toInt(), genero, email, phone, address)) {
                                Toast.makeText(this@MainActivity, "Registro exitoso", Toast.LENGTH_LONG).show()
                                // Limpia los campos después del registro exitoso
                                name = ""
                                last_name = ""
                                edad = ""
                                genero = ""
                                email = ""
                                phone = ""
                                address = ""
                                // Actualiza la lista de usuarios
                                users = dbHelper.getAllUsers()
                            } else {
                                Toast.makeText(this@MainActivity, "Error al registrar", Toast.LENGTH_LONG).show()
                            }
                        } else {

                            if (dbHelper.updateUser(editingUserId!!, name, last_name, edad.toInt(), genero, email, phone, address)) {
                                Toast.makeText(this@MainActivity, "Actualización exitosa", Toast.LENGTH_LONG).show()
                                 users = dbHelper.getAllUsers()
                             } else {
                                 Toast.makeText(this@MainActivity, "Error al actualizar", Toast.LENGTH_LONG).show()
                            }
                        }
                    }

                },
                modifier = fieldModifier

            ) {

                Text(text = if (editingUserId == null) "Registrar Usuario" else "Actualizar Usuario")
            }

            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            // Botón para limpiar campos
            Button(
                onClick = {
                    name = ""
                    last_name = ""
                    edad = ""
                    genero = ""
                    email = ""
                    phone = ""
                    address = ""
                    errorMessage = ""
                    editingUserId = null
                },
                modifier = fieldModifier
            ) {
                Text("Limpiar Campos")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mostrar usuarios registrados
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(6.dp)
            ) {
                items(users) { user ->
                    UserRow(
                        user = user,
                        onDelete = {
                            if (dbHelper.deleteUser(user["id"] as Int)) {
                                users = dbHelper.getAllUsers()
                                Toast.makeText(this@MainActivity, "Usuario eliminado", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(this@MainActivity, "Error al eliminar", Toast.LENGTH_LONG).show()
                            }
                        },
                        onEdit = {
                            editingUserId = user["id"] as Int
                            name = user["name"] as String
                            last_name = user["lastname"] as String
                            edad = user["age"].toString()
                            genero = user["gender"] as String
                            email = user["email"] as String
                            phone = user["phone"] as String
                            address = user["address"] as String
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun UserRow(user: Map<String, Any>, onDelete: () -> Unit, onEdit: () -> Unit) {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFE8DEF8))
            .padding(15.dp)
        ) {

            Text(text = "Nombre: ${user["name"]}")
            Text(text = "Apellido: ${user["lastname"]}")
            Text(text = "Edad: ${user["age"]}")
            Text(text = "Género: ${user["gender"]}")
            Text(text = "Email: ${user["email"]}")
            Text(text = "Teléfono: ${user["phone"]}")
            Text(text = "Dirección: ${user["address"]}")
            Spacer(modifier = Modifier.height(8.dp))
            Row{
                Button(onClick = onEdit){
                    Text(text = "Editar")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onDelete){
                    Text(text = "Eliminar")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

