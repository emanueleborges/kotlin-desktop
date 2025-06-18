package com.example

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter

@Composable
@Preview
fun App() {
    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var nameField by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    // Paginação
    var currentPage by remember { mutableStateOf(0) }
    val pageSize = 5
    val coroutineScope = rememberCoroutineScope()
    val apiService = ApiService()
    val userApi = apiService.userApi
    
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Form panel
                Card(
                    modifier = Modifier.width(320.dp).fillMaxHeight().padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Detalhes do Usuário",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        
                        OutlinedTextField(
                            value = nameField,
                            onValueChange = { nameField = it },
                            label = { Text("Nome") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = nameField.isBlank(),
                            supportingText = { 
                                if (nameField.isBlank()) Text("Nome é obrigatório") 
                            }
                        )
                        
                        Text(
                            text = "ID: ${selectedUser?.id ?: 0}",
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = "Criado em: ${selectedUser?.createdAt?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) ?: ""}"
                        )
                        
                        Text(
                            text = "Atualizado em: ${selectedUser?.updatedAt?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) ?: "Nunca"}"
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { 
                                    selectedUser = null
                                    nameField = ""
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Novo")
                            }
                            
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        try {
                                            if (selectedUser != null) {
                                                // Update
                                                val response = withContext(Dispatchers.IO) {
                                                    userApi.updateUser(
                                                        selectedUser!!.id, 
                                                        UserUpdateRequest(nameField)
                                                    ).execute()
                                                }
                                                if (response.isSuccessful) {                                                    
                                                    response.body()?.let {
                                                        val index = users.indexOfFirst { u -> u.id == it.id }
                                                        if (index >= 0) {
                                                            val newList = users.toMutableList()
                                                            newList[index] = it
                                                            users = newList
                                                            selectedUser = it
                                                        }
                                                    }
                                                }
                                            } else {
                                                // Create
                                                val response = withContext(Dispatchers.IO) {
                                                    userApi.createUser(UserCreateRequest(nameField)).execute()                                                }
                                                
                                                if (response.isSuccessful) {
                                                    response.body()?.let {
                                                        users = users + it
                                                        selectedUser = it
                                                        // Ajustar a página para mostrar o novo usuário
                                                        currentPage = (users.size - 1) / pageSize
                                                    }
                                                }
                                            }
                                        } catch (e: Exception) {
                                            println("Error saving user: ${e.message}")
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                enabled = nameField.isNotBlank()
                            ) {
                                Text("Salvar")
                            }
                            
                            Button(
                                onClick = {
                                    selectedUser?.let { user ->
                                        coroutineScope.launch {
                                            try {                                                
                                                val response = withContext(Dispatchers.IO) {
                                                    userApi.deleteUser(user.id).execute()
                                                }
                                                
                                                if (response.isSuccessful) {
                                                    users = users.filter { it.id != user.id }
                                                    selectedUser = null
                                                    nameField = ""
                                                    
                                                    // Ajustar página se necessário
                                                    val totalPages = (users.size + pageSize - 1) / pageSize
                                                    if (currentPage >= totalPages && currentPage > 0) {
                                                        currentPage--
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                println("Error deleting user: ${e.message}")
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                enabled = selectedUser != null,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Excluir")
                            }
                        }
                    }
                }
                
            // Table panel
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Lista de Usuários",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                label = { Text("Buscar por nome") },
                                singleLine = true,
                                modifier = Modifier.width(200.dp)
                            )
                            
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        try {
                                            val response = if (searchQuery.isBlank()) {
                                                withContext(Dispatchers.IO) {
                                                    userApi.getAllUsers().execute()
                                                }
                                            } else {
                                                withContext(Dispatchers.IO) {
                                                    userApi.searchUsersByName(searchQuery).execute()
                                                }
                                            }
                                            
                                            if (response.isSuccessful) {
                                                users = response.body() ?: emptyList()
                                                currentPage = 0
                                            }
                                        } catch (e: Exception) {
                                            println("Error searching users: ${e.message}")
                                        }
                                    }
                                }
                            ) {
                                Text("Buscar")
                            }
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Button(
                                onClick = {
                                    searchQuery = ""
                                    coroutineScope.launch {
                                        try {
                                            val response = withContext(Dispatchers.IO) {
                                                userApi.getAllUsers().execute()
                                            }
                                            if (response.isSuccessful) {
                                                users = response.body() ?: emptyList()
                                                currentPage = 0
                                            }
                                        } catch (e: Exception) {
                                            println("Error loading users: ${e.message}")
                                        }
                                    }
                                }
                            ) {
                                Text("Limpar")
                            }
                        }
                    }
                      Card(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Mostrar apenas os usuários da página atual
                            val paginatedUsers = users.chunked(pageSize).getOrNull(currentPage) ?: emptyList()
                            items(paginatedUsers) { user ->
                                ListItem(
                                    headlineContent = { Text(user.name) },
                                    supportingContent = { 
                                        Text("ID: ${user.id} | Criado: ${user.createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}") 
                                    },
                                    modifier = Modifier.clickable {
                                        selectedUser = user
                                        nameField = user.name
                                    }
                                )
                                Divider()
                            }
                        }
                    }
                    
                    // Controles de paginação
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {                        Button(
                            onClick = { 
                                if (currentPage > 0) currentPage--
                            },
                            enabled = currentPage > 0
                        ) {
                            Text("← Anterior")
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Text(
                            text = "Página ${currentPage + 1} de ${(users.size + pageSize - 1) / pageSize}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                          Spacer(modifier = Modifier.width(16.dp))
                        
                        Button(
                            onClick = { 
                                if (currentPage < (users.size - 1) / pageSize) currentPage++
                            },
                            enabled = currentPage < (users.size - 1) / pageSize
                        ) {
                            Text("Próximo →")
                        }
                    }
                }
            }
        }
    }
      // Load users when the app starts
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    userApi.getAllUsers().execute()
                }
                if (response.isSuccessful) {
                    users = response.body() ?: emptyList()
                    currentPage = 0
                }
            } catch (e: Exception) {
                println("Error loading users: ${e.message}")
            }
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Gerenciamento de Usuários",
        state = rememberWindowState(width = 1024.dp, height = 768.dp)
    ) {
        App()
    }
}
