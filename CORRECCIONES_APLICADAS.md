# GesSport - Proyecto Corregido con Room

## Cambios Realizados

### 1. Configuración de Room
- ✅ Añadido plugin KSP (Kotlin Symbol Processing) necesario para Room
- ✅ Añadidas dependencias del compilador de Room (`room-compiler`)
- ✅ Configurado correctamente en `build.gradle.kts` y `libs.versions.toml`

### 2. Modelo de Datos
- ✅ Corregido `User.kt` para usar `autoGenerate = true` en la clave primaria
- ✅ Añadido valor por defecto `id = 0` para inserción automática

### 3. Base de Datos
- ✅ `AppDatabase.kt` - Configurada correctamente con callback para datos iniciales
- ✅ `UserDao.kt` - Todas las operaciones CRUD implementadas correctamente
- ✅ `RoomUserRepository.kt` - Implementación completa del repositorio

### 4. ViewModels y Factories
- ✅ Creado `GesUserViewModelFactory.kt` para instanciar correctamente el ViewModel con contexto
- ✅ `GesUserViewModel.kt` - Actualizado para recibir Context en lugar de Repository

### 5. Pantallas (UI)
Todas las pantallas ahora usan correctamente:
- ✅ `LocalContext.current` para obtener el contexto
- ✅ `GesUserViewModelFactory(context)` para crear el ViewModel
- ✅ `.collectAsState()` para observar los flows de Room
- ✅ Rutas centralizadas en el objeto `Routes`

Pantallas corregidas:
- ✅ `LoginScreen.kt` - Usa RoomUserRepository correctamente
- ✅ `AddUserScreen.kt` - Usa el nuevo factory
- ✅ `UpdateUserScreen.kt` (EditUserScreen) - Usa el nuevo factory
- ✅ `DeleteUserScreen.kt` - Usa el nuevo factory con collectAsState()
- ✅ `SelectUserScreen.kt` - Usa el nuevo factory con collectAsState()

### 6. Navegación
- ✅ `Navigation.kt` - Eliminadas rutas duplicadas
- ✅ Todas las rutas ahora usan el objeto `Routes` centralizado
- ✅ Corregida la navegación de login para admin (ADMIN_DEPORTIVO)

### 7. Imports y Dependencias
- ✅ Añadidos imports necesarios (`androidx.compose.foundation.lazy.items`)
- ✅ Eliminadas referencias a `DataUserRepository` y `JsonUserRepository`
- ✅ Todos los imports de contexto añadidos donde eran necesarios

## Cómo Usar

### Prerequisitos
- Android Studio Hedgehog o superior
- Kotlin 2.0.21
- Gradle 8.13.0

### Compilación
1. Abre el proyecto en Android Studio
2. Sync Gradle (el plugin KSP generará las clases de Room automáticamente)
3. Compila y ejecuta

### Usuarios por Defecto
La base de datos se inicializa con estos usuarios:
- **Admin**: lolo.admin@club.es / 1234 (ADMIN_DEPORTIVO)
- **Entrenador**: pedro.entrenador@club.es / 1234 (ENTRENADOR)
- **Jugador 1**: laura.jugadora@club.es / 1234 (JUGADOR)
- **Árbitro**: luis.arbitro@club.es / 1234 (ARBITRO)
- **Jugador 2**: maria.jugadora@club.es / 1234 (JUGADOR)

## Estructura del Proyecto

```
app/src/main/java/com/example/gessport/
├── data/
│   ├── RoomUserRepository.kt      # Implementación con Room
│   ├── LoginRepository.kt          
│   └── UserDataSource.kt          
├── database/
│   ├── AppDatabase.kt              # Base de datos Room
│   └── UserDao.kt                  # DAO de Room
├── domain/
│   └── LogicLogin.kt               # Lógica de negocio
├── models/
│   ├── User.kt                     # Entidad Room
│   └── UserRoles.kt                
├── navigation/
│   └── Navigation.kt               # Sistema de navegación
├── repository/
│   └── UserRepository.kt           # Interfaz del repositorio
└── ui/
    ├── backend/ges_user/
    │   ├── GesUserViewModel.kt            # ViewModel principal
    │   ├── GesUserViewModelFactory.kt     # Factory para el ViewModel
    │   ├── AddUserScreen.kt               
    │   ├── UpdateUserScreen.kt (EditUserScreen)
    │   ├── DeleteUserScreen.kt            
    │   ├── SelectUserScreen.kt            
    │   ├── AdminPanelScreen.kt            
    │   └── GesUserScreen.kt               
    ├── home/
    │   └── HomeScreen.kt           
    ├── login/
    │   └── LoginScreen.kt          
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

## Características de Room Implementadas

1. **Operaciones CRUD Completas**
   - Create: `insert()`, `insertAll()`
   - Read: `getAll()`, `getByRole()`, `getById()`, `getByEmail()`
   - Update: `update()`
   - Delete: `delete()`, `deleteAll()`

2. **Login Seguro**
   - Query específico para login con email y password
   - Validación en capa de dominio

3. **Observación Reactiva**
   - Uso de `Flow` para observar cambios en tiempo real
   - Todas las listas se actualizan automáticamente

4. **Filtrado por Rol**
   - Filtro dinámico de usuarios por rol
   - Query optimizado en Room

## Problemas Solucionados

❌ **Problema**: Faltaba el plugin KSP  
✅ **Solución**: Añadido en `libs.versions.toml` y `build.gradle.kts`

❌ **Problema**: No se generaban las clases de Room  
✅ **Solución**: Añadido `ksp(libs.androidx.room.compiler)`

❌ **Problema**: ViewModel necesitaba Repository en lugar de Context  
✅ **Solución**: Cambiado para recibir Context y crear el Repository internamente

❌ **Problema**: Pantallas usaban `DataUserRepository` inexistente  
✅ **Solución**: Todas usan `RoomUserRepository` via el Factory

❌ **Problema**: Rutas duplicadas en Navigation  
✅ **Solución**: Limpiada la navegación, solo una ruta de cada tipo

❌ **Problema**: No se observaban cambios en las listas  
✅ **Solución**: Uso correcto de `.collectAsState()` en todos los lugares

## Notas Importantes

- La base de datos se crea automáticamente en el primer arranque
- Los datos persisten entre ejecuciones de la app
- El patrón Repository permite cambiar fácilmente la fuente de datos
- Room maneja automáticamente las operaciones en hilos background

## Próximos Pasos Sugeridos

1. Implementar validación de email más robusta
2. Añadir encriptación para las contraseñas (nunca guardar en texto plano)
3. Implementar paginación para listas grandes
4. Añadir búsqueda de usuarios
5. Implementar caché y sincronización con backend remoto
6. Añadir tests unitarios para el DAO y Repository
