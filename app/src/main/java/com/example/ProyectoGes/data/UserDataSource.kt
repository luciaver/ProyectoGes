package com.example.ProyectoGes.data


import com.example.ProyectoGes.models.User

object UserDataSource {
    val users = mutableListOf(
        User(
            id = 1,
            nombre = "Ana López",
            email = "ana@correo.com",
            password = "1234",
            rol = "admin"
        ),
        User(
            id = 2,
            nombre = "Luis Gómez",
            email = "luis@correo.com",
            password = "abcd",
            rol = "entrenador"
        ),
        User(
            id = 3,
            nombre = "María Pérez",
            email = "maria@correo.com",
            password = "contras1",
            rol = "entrenador"
        ),
        User(
            id = 4,
            nombre = "Carlos Ruiz",
            email = "carlos@correo.com",
            password = "pass2",
            rol = "jugador"
        ),
        User(
            id = 5,
            nombre = "Laura Díaz",
            email = "laura@correo.com",
            password = "laura123",
            rol = "jugador"
        ),
        User(
            id = 6,
            nombre = "Javier Torres",
            email = "javier@correo.com",
            password = "javi2025",
            rol = "jugador"
        ),
        User(
            id = 7,
            nombre = "Sofía Sánchez",
            email = "sofia@correo.com",
            password = "sofia!",
            rol = "admin"
        ),
        User(
            id = 8,
            nombre = "Miguel Fernández",
            email = "miguel@correo.com",
            password = "clave",
            rol = "jugador"
        ),
        User(
            id = 9,
            nombre = "Elena Ramírez",
            email = "elena@correo.com",
            password = "hola123",
            rol = "jugador"
        ),
        User(
            id = 10,
            nombre = "Pedro Martín",
            email = "pedro@correo.com",
            password = "pedro321",
            rol = "admin"
        )
    )
}

