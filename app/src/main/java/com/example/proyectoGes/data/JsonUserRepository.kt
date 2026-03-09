package com.example.proyectoGes.data

/*class JsonUserRepository(private val context: Context): UserRepository {

    private val TAG = "Fichero.json";

    //private val jsonFile = File("res/assets/users.json")
    val jsonFile = context.assets.open("users.json")
        .bufferedReader()
        .use { it.readText() }

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }
    private val users = mutableListOf<User>()
    private var nextId: Int = 1

    init {
        loadFile()
    }


    private fun loadFile() {
        Log.i(TAG, "Carga fichero")
        if (jsonFile.exists()) {
            Log.i(TAG, "Encontrado Fichero")
            val text = jsonFile.readText()
            if (text.isNotBlank()) {
                val loadedUsers: List<User> = json.decodeFromString(text)
                users.clear()
                users.addAll(loadedUsers)
                nextId = (users.maxOfOrNull { it.id } ?: 0) + 1
            }
        } else {
            Log.i(TAG, "carga desde assets")
            loadFromAssets()
            saveToFile()
        }


    }


    private fun loadFromAssets() {

        val loadedUsers: List<User> = json.decodeFromString(jsonFile)
        users.clear()
        users.addAll(loadedUsers)

        nextId = (users.maxOfOrNull { it.id } ?: 0) + 1
    }

    private fun saveToFile() {
        val text = json.encodeToString(users)
        // jsonFile.writeText(text)
    }

    private fun getNewId(): Int {
        return (users.maxOfOrNull { it.id } ?: 0) + 1
    }


    override suspend fun getAllUsers(): List<User> {
        return users.toList()
    }

    override suspend fun getUsersByRole(rol: String): List<User> {
        return users.filter { it.rol == rol }
    }

    override suspend fun getUserById(id: Int): User? {
        return users.find { it.id == id }
    }

    override suspend fun addUser(user: User): User {
        nextId = getNewId()
        val newUser = user.copy(id = nextId)
        users.add(newUser)
        saveToFile()
        return newUser
    }

    override suspend fun updateUser(user: User): Boolean {
        val index = users.indexOfFirst { it.id == user.id }
        if (index == -1) return false
        users[index] = user
        saveToFile()
        return true
    }

    override suspend fun deleteUser(id: Int): Boolean {
        val removed = users.removeIf { it.id == id }
        if (removed) {
            saveToFile()
        }
        return removed
    }
}


/*// ========================
    // Implementación interfaz
    // ========================

    override suspend fun getAllUsers(): List<User> {
        return users.toList() // copia para no exponer la lista interna
    }

    override suspend fun getUsersByRole(role: String): List<User> {
        return users.filter { it.rol == role }
    }

    override suspend fun getUserById(id: Int): User? {
        return users.find { it.id == id }
    }

    override suspend fun addUser(user: User): User {
        val newUser = user.copy(id = nextId++)
        users.add(newUser)
        saveToFile()
        return newUser
    }

    override suspend fun updateUser(user: User): Boolean {
        val index = users.indexOfFirst { it.id == user.id }
        if (index == -1) return false

        users[index] = user
        saveToFile()
        return true
    }

    override suspend fun deleteUser(id: Int): Boolean {
        val removed = users.removeIf { it.id == id }
        if (removed) {
            saveToFile()
        }
        return removed
    }

    */