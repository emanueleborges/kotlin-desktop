package com.example

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.SimpleLongProperty
import tornadofx.*
import java.time.LocalDateTime

class UserModel(user: User? = null) : ViewModel() {
    val id = SimpleLongProperty(user?.id ?: 0L)
    //val name = SimpleStringProperty(user?.name ?: "")
    val name = bind { SimpleStringProperty(user?.name ?: "") }

    val createdAt = SimpleObjectProperty(user?.createdAt ?: LocalDateTime.now())
    val updatedAt = SimpleObjectProperty<LocalDateTime?>().apply {
        value = user?.updatedAt
    }

    // Renomeada para não conflitar com ViewModel.valid
    val isUserValid = SimpleBooleanProperty(true)

    init {
        name.onChange {
            isUserValid.value = !name.value.isNullOrBlank()
        }
    }

    fun toUser(): User = User(
        id = id.value.toLong(),
        name = name.value,
        createdAt = createdAt.value ?: LocalDateTime.now(),
        updatedAt = updatedAt.value
    )

    fun update(user: User) {
        id.value = user.id
        name.value = user.name
        createdAt.value = user.createdAt
        updatedAt.value = user.updatedAt
    }

    fun clear() {
        id.value = 0
        name.value = ""
        createdAt.value = LocalDateTime.now()
        updatedAt.value = null
    }
}
