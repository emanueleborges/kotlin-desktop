package com.example

import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import tornadofx.*
import java.time.format.DateTimeFormatter

/**
 * Controller for User CRUD operations
 */
class UserController : Controller() {
    private val api: UserApi = ApiService().userApi
    val users = FXCollections.observableArrayList<User>()
    val selectedUser = SimpleObjectProperty<User>()
    
    init {
        loadUsers()
    }
    
    fun loadUsers() {
        api.getAllUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    runLater {
                        users.clear()
                        response.body()?.let { users.addAll(it) }
                    }
                } else {
                    showError("Erro ao carregar usuários: ${response.code()}")
                }
            }
            
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                showError("Erro de conexão: ${t.message}")
            }
        })
    }
    
    fun saveUser(user: User) {
        if (user.id == 0L) {
            createUser(user)
        } else {
            updateUser(user)
        }
    }
    
    private fun createUser(user: User) {
        val request = UserCreateRequest(user.name)
        api.createUser(request).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    runLater {
                        response.body()?.let {
                            users.add(it)
                            showSuccess("Usuário criado com sucesso")
                        }
                    }
                } else {
                    showError("Erro ao criar usuário: ${response.code()}")
                }
            }
            
            override fun onFailure(call: Call<User>, t: Throwable) {
                showError("Erro de conexão: ${t.message}")
            }
        })
    }
    
    private fun updateUser(user: User) {
        val request = UserUpdateRequest(user.name)
        api.updateUser(user.id, request).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    runLater {
                        response.body()?.let {
                            val index = users.indexOfFirst { u -> u.id == it.id }
                            if (index >= 0) {
                                users[index] = it
                            }
                            showSuccess("Usuário atualizado com sucesso")
                        }
                    }
                } else {
                    showError("Erro ao atualizar usuário: ${response.code()}")                }
            }
            
            override fun onFailure(call: Call<User>, t: Throwable) {
                showError("Erro de conexão: ${t.message}")
            }
        })
    }
    
    fun deleteUser(user: User) {
        api.deleteUser(user.id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    runLater {
                        users.removeIf { u -> u.id == user.id }
                        showSuccess("Usuário excluído com sucesso")
                    }
                } else {
                    showError("Erro ao excluir usuário: ${response.code()}")
                }
            }              override fun onFailure(call: Call<Void>, t: Throwable) {
                showError("Erro de conexão: ${t.message}")
            }
        })
    }
    
    fun confirmDelete(user: User, action: () -> Unit) {
        val alert = Alert(Alert.AlertType.CONFIRMATION).apply {
            title = "Confirmar exclusão"
            headerText = "Confirmar exclusão"
            contentText = "Tem certeza que deseja excluir o usuário ${user.name}?"
            buttonTypes.setAll(ButtonType.YES, ButtonType.NO)
        }
        
        val result = alert.showAndWait()
        if (result.isPresent && result.get() == ButtonType.YES) {
            action()
        }
    }
    
    private fun showError(message: String) {
        runLater {
            Alert(Alert.AlertType.ERROR, message).show()
        }
    }
    
    private fun showSuccess(message: String) {
        runLater {
            Alert(Alert.AlertType.INFORMATION, message).show()
        }
    }
}
