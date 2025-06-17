package com.example

import javafx.geometry.Insets
import javafx.scene.control.TableView
import javafx.scene.layout.Priority
import tornadofx.*
import java.time.format.DateTimeFormatter

class UserView : View("Gerenciamento de Usuários") {
    private val controller: UserController by inject()
    private val model = UserModel()
    private var userTable: TableView<User> by singleAssign()

    override val root = borderpane {
        left = vbox(10.0) {
            padding = Insets(10.0)
            prefWidth = 320.0

            form {
                fieldset("Dados do Usuário") {
                    field("Nome") {
                        textfield(model.name) {
                            required(ValidationTrigger.OnBlur)
                        }
                    }

                    field("ID") {
                        label(model.id) {
                            style = "-fx-font-weight: bold"
                        }
                    }

                    field("Criado em") {
                        label(model.createdAt.stringBinding { date ->
                            date?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) ?: ""
                        })
                    }

                    field("Atualizado em") {
                        label(model.updatedAt.stringBinding { date ->
                            date?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) ?: "Nunca"
                        })
                    }
                }

                hbox(10.0) {
                    button("Novo") {
                        action {
                            model.clear()
                            controller.selectedUser.value = null
                        }
                    }

                    button("Salvar") {
                        enableWhen(model.valid)
                        action {
                            model.commit()
                            controller.saveUser(model.toUser())
                        }
                    }

                    button("Excluir") {
                        enableWhen(model.id.greaterThan(0))
                        action {
                            model.commit()
                            val user = model.toUser()
                            controller.confirmDelete(user) {
                                controller.deleteUser(user)
                                model.clear()
                            }
                        }
                    }
                }
            }
        }

        center = vbox(10.0) {
            padding = Insets(10.0)

            hbox(10.0) {
                label("Lista de Usuários") {
                    style = "-fx-font-size: 16px; -fx-font-weight: bold"
                }
                region { hgrow = Priority.ALWAYS }
                button("Atualizar") {
                    action { controller.loadUsers() }
                }
            }

            userTable = tableview(controller.users) {
                readonlyColumn("ID", User::id)
                readonlyColumn("Nome", User::name)
                readonlyColumn("Criado em", User::createdAt) { 
                    cellFormat { datetime ->
                        text = datetime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                    }
                }
                readonlyColumn("Atualizado em", User::updatedAt) { 
                    cellFormat { datetime ->
                        text = datetime?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) ?: "Nunca"
                    }
                }

                onSelectionChange { selected ->
                    if (selected != null) {
                        model.update(selected)
                        controller.selectedUser.value = selected
                    }
                }

                vgrow = Priority.ALWAYS
                columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY

            }
        }
    }
}
