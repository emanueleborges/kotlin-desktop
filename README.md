# Kotlin Desktop CRUD Application

Este é um aplicativo desktop em Kotlin que se conecta ao backend CRUD para gerenciar usuários.

## Requisitos

- JDK 17 ou superior
- Gradle
- O backend Kotlin rodando na porta 8080

## Como executar

1. Certifique-se de que o backend esteja rodando
2. Execute o aplicativo com o seguinte comando:

```bash
./gradlew run
```

## Recursos

- Interface gráfica com JavaFX usando TornadoFX
- CRUD completo de usuários
- Comunicação com o backend via Retrofit

## Estrutura do Projeto

- `Main.kt` - Ponto de entrada da aplicação
- `UserView.kt` - Interface gráfica principal
- `UserController.kt` - Lógica de negócios e comunicação com a API
- `UserModel.kt` - Modelo de dados para a interface
- `User.kt` - Classe de modelo
- `UserApi.kt` - Interface para a API REST
- `ApiService.kt` - Configuração do cliente Retrofit
- `Styles.kt` - Estilos CSS para a interface
