# 📚 Sistema de Inventario - Gestión de Archivos de Texto

## 🎯 Descripción del Proyecto

Este proyecto demuestra la implementación de un **sistema de gestión de archivos de texto** en Java, utilizando JavaFX para la interfaz gráfica. El objetivo principal es mostrar cómo manejar operaciones básicas de archivos (lectura, escritura, validación) para almacenar y gestionar información de usuarios.

## 🏗️ Arquitectura del Proyecto

### Estructura de Directorios
```
inventario/
├── src/main/java/com/programacion/inventario/
│   ├── controller/          # Controladores de la interfaz
│   ├── model/              # Modelos de datos
│   ├── util/               # Utilidades y gestores
│   └── HelloApplication.java # Punto de entrada principal
├── src/main/resources/
│   └── view/               # Archivos FXML de la interfaz
├── data/                   # Directorio de almacenamiento
│   └── usuarios.txt        # Archivo de usuarios
└── pom.xml                 # Configuración de Maven
```

## 🔧 Tecnologías Utilizadas

- **Java 23** - Lenguaje de programación principal
- **JavaFX 21** - Framework para interfaces gráficas
- **Maven** - Gestión de dependencias y build
- **NIO.2** - API moderna de Java para manejo de archivos

## 📁 Sistema de Gestión de Archivos

### 1. Clase FileManager (`util/FileManager.java`)

Esta es la **clase central** que maneja todas las operaciones de archivos. Implementa el patrón de diseño **Singleton** para gestionar el acceso a archivos de manera centralizada.

#### Funcionalidades Principales:

```java
public class FileManager {
    // Directorio de datos de la aplicación
    public static final String DATA_DIRECTORY = "data";
    
    // Métodos principales:
    - writeToFile()     // Escribir en archivos
    - readFromFile()    // Leer desde archivos
    - fileExists()      // Verificar existencia
    - deleteFile()      // Eliminar archivos
    - getFileSize()     // Obtener tamaño
}
```

#### Características Clave:

- **Creación automática de directorios**: Si el directorio `data/` no existe, se crea automáticamente
- **Manejo de errores robusto**: Captura y maneja excepciones de I/O
- **Operaciones atómicas**: Usa try-with-resources para cerrar archivos automáticamente
- **Filtrado de líneas vacías**: Ignora líneas en blanco al leer archivos

### 2. Almacenamiento de Usuarios

#### Formato del Archivo (`data/usuarios.txt`)
```
usuario:contraseña
admin:admin123
profesor:clase2024
test:test
```

#### Ventajas de este Formato:
- **Simple y legible**: Fácil de entender y modificar manualmente
- **Separador claro**: El carácter `:` delimita usuario y contraseña
- **Una línea por registro**: Facilita el procesamiento línea por línea

## 🔐 Sistema de Autenticación

### Flujo de Login:

1. **Entrada de credenciales** → Usuario ingresa nombre y contraseña
2. **Validación de archivo** → Se verifica que `usuarios.txt` exista
3. **Lectura de archivo** → Se lee todo el archivo línea por línea
4. **Parsing de credenciales** → Cada línea se divide por `:`
5. **Comparación** → Se comparan las credenciales ingresadas
6. **Resultado** → Se permite o deniega el acceso
7. **Navegación** → Si es exitoso, se navega a la pantalla principal

### Código Clave del Login:

```java
public boolean validateUserCredentials(String username, String password) {
    // 1. Verificar existencia del archivo
    if (!fileManager.fileExists(USERS_FILE)) {
        createDefaultUser(); // Crear usuarios por defecto
    }
    
    // 2. Leer todas las líneas del archivo
    List<String> listUsuarios = fileManager.readFromFile(USERS_FILE);
    
    // 3. Procesar cada línea
    for(String line : listUsuarios) {
        if (line.trim().isEmpty()) continue; // Saltar líneas vacías
        
        // 4. Dividir por el separador ':'
        String[] credentials = line.split(":");
        
        // 5. Validar formato y comparar
        if (credentials.length == 2) {
            if (credentials[0].equals(username) && 
                credentials[1].equals(password)) {
                return true; // Credenciales válidas
            }
        }
    }
    return false; // Credenciales inválidas
}
```

## 🚀 Cómo Ejecutar el Proyecto

### Prerrequisitos:
- Java 23 o superior
- Maven 3.6+

### Comandos de Ejecución:

```bash
# Compilar el proyecto
mvn clean compile

# Ejecutar la aplicación
mvn javafx:run

# Crear JAR ejecutable
mvn clean package
```

## 🧭 Sistema de Navegación (Routing) en JavaFX

### 🎯 Descripción del Sistema de Navegación

El proyecto implementa un **sistema de navegación centralizado y reutilizable** que permite moverse entre diferentes pantallas de la aplicación de manera fluida y consistente. Este sistema está diseñado para ser fácil de entender y mantener.

### 🏗️ Arquitectura del Sistema de Navegación

#### 1. **NavigationManager** (`util/NavigationManager.java`)

Esta es la **clase central** que maneja toda la navegación. Implementa el patrón **Singleton** para asegurar que solo exista una instancia en toda la aplicación.

```java
public class NavigationManager {
    // Instancia única (Singleton)
    private static NavigationManager instance;
    
    // Stage principal de la aplicación
    private Stage primaryStage;
    
    // Cache de controladores para evitar recargar
    private Map<String, Object> controllerCache;
}
```

#### 2. **Enumeración de Pantallas**

Todas las pantallas disponibles están definidas en una enumeración para evitar errores de tipeo:

```java
public enum Screen {
    LOGIN("login-view.fxml", "Login - Sistema de Inventario"),
    MAIN("main-view.fxml", "Sistema Principal - Inventario"),
    USUARIOS("usuarios-view.fxml", "Gestión de Usuarios"),
    PRODUCTOS("productos-view.fxml", "Gestión de Productos"),
    REPORTES("reportes-view.fxml", "Reportes del Sistema");
}
```

### 🔄 Flujo de Navegación

#### **Login → Pantalla Principal:**

```java
// En LoginController después de validar credenciales
Map<String, Object> parameters = new HashMap<>();
parameters.put("username", username);
parameters.put("role", "Usuario");

NavigationManager navigationManager = NavigationManager.getInstance();
navigationManager.navigateTo(NavigationManager.Screen.MAIN, parameters);
```

#### **Navegación entre Módulos:**

```java
// En MainController para navegar a diferentes secciones
@FXML
private void navigateToUsuarios() {
    showDynamicContent("Gestión de Usuarios", 
                      "Aquí puedes administrar todos los usuarios del sistema...");
    currentScreen = NavigationManager.Screen.USUARIOS;
}
```

### 🎨 Características del Sistema de Navegación

#### **1. Navegación Centralizada**
- **Un solo punto de control**: Todas las transiciones pasan por `NavigationManager`
- **Consistencia**: Mismo comportamiento en toda la aplicación
- **Mantenibilidad**: Cambios en un solo lugar afectan toda la navegación

#### **2. Cache de Controladores**
- **Reutilización**: Los controladores se mantienen en memoria
- **Rendimiento**: No se recargan archivos FXML innecesariamente
- **Estado**: Se preserva el estado de las pantallas

#### **3. Manejo de Parámetros**
- **Transferencia de datos**: Se pueden pasar parámetros entre pantallas
- **Interfaz estándar**: `ParameterReceiver` para controladores que reciben datos
- **Flexibilidad**: Diferentes tipos de parámetros (String, Object, etc.)

#### **4. Ventanas Modales**
- **Diálogos**: Soporte para ventanas modales
- **Propietario**: Las modales tienen un stage padre
- **Bloqueo**: La aplicación principal se bloquea hasta cerrar la modal

### 📱 Pantallas del Sistema

#### **1. Login (`login-view.fxml`)**
- **Propósito**: Autenticación de usuarios
- **Controlador**: `LoginController`
- **Funcionalidad**: Validación de credenciales y navegación al main

#### **2. Pantalla Principal (`main-view.fxml`)**
- **Propósito**: Dashboard central del sistema
- **Controlador**: `MainController`
- **Funcionalidad**: Menú de navegación y contenido dinámico

#### **3. Gestión de Usuarios (`usuarios-view.fxml`)**
- **Propósito**: Administración de usuarios del sistema
- **Controlador**: `UsuariosController`
- **Funcionalidad**: CRUD de usuarios

### 🔧 Cómo Usar el Sistema de Navegación

#### **Para Navegar a una Pantalla:**

```java
// Navegación simple
NavigationManager.getInstance().navigateTo(NavigationManager.Screen.MAIN);

// Navegación con parámetros
Map<String, Object> params = new HashMap<>();
params.put("username", "admin");
NavigationManager.getInstance().navigateTo(NavigationManager.Screen.MAIN, params);

// Navegación limpiando cache
NavigationManager.getInstance().navigateTo(NavigationManager.Screen.LOGIN, true);
```

#### **Para Abrir una Ventana Modal:**

```java
// Modal simple
NavigationManager.getInstance().openModal(
    NavigationManager.Screen.USUARIOS, 
    "Nuevo Usuario", 
    null
);
```

#### **Para Recibir Parámetros en un Controlador:**

```java
public class MainController implements NavigationManager.ParameterReceiver {
    @Override
    public void receiveParameters(Map<String, Object> parameters) {
        if (parameters.containsKey("username")) {
            String username = (String) parameters.get("username");
            // Usar el parámetro recibido
        }
    }
}
```

### 🎓 Ventajas para los Estudiantes

#### **1. Separación de Responsabilidades**
- **Navegación**: `NavigationManager` se encarga solo de cambiar pantallas
- **Lógica**: Cada controlador maneja su propia funcionalidad
- **Interfaz**: Los archivos FXML definen solo la presentación

#### **2. Reutilización de Código**
- **Patrón Singleton**: Una sola instancia para toda la aplicación
- **Métodos estándar**: Navegación consistente en todos lados
- **Cache inteligente**: Evita recargar recursos innecesariamente

#### **3. Mantenibilidad**
- **Cambios centralizados**: Modificar la navegación en un solo lugar
- **Debugging fácil**: Flujo de navegación claro y predecible
- **Escalabilidad**: Agregar nuevas pantallas es sencillo

#### **4. Buenas Prácticas**
- **Manejo de errores**: Errores de navegación manejados centralmente
- **Logging**: Registro de todas las transiciones
- **Validaciones**: Verificación de recursos antes de navegar

### 🚀 Extensión del Sistema

#### **Agregar una Nueva Pantalla:**

1. **Crear el archivo FXML** en `src/main/resources/com/programacion/inventario/view/`
2. **Crear el controlador** en `src/main/java/com/programacion/inventario/controller/`
3. **Agregar a la enumeración** `Screen` en `NavigationManager`
4. **Implementar la navegación** en los controladores existentes

#### **Ejemplo de Nueva Pantalla:**

```java
// En NavigationManager.Screen
CONFIGURACION("configuracion-view.fxml", "Configuración del Sistema"),

// En MainController
@FXML
private void navigateToConfiguracion() {
    showDynamicContent("Configuración del Sistema", 
                      "Configura parámetros del sistema...");
    currentScreen = NavigationManager.Screen.CONFIGURACION;
}
```

### ⚠️ Consideraciones Importantes

#### **1. Inicialización**
- **NavigationManager debe inicializarse** en `HelloApplication.start()`
- **Stage principal requerido** para que funcione la navegación
- **Orden de inicialización** es crítico

#### **2. Manejo de Errores**
- **Archivos FXML faltantes** se manejan gracefulmente
- **Controladores inexistentes** generan errores informativos
- **Recursos no encontrados** muestran alertas al usuario

#### **3. Rendimiento**
- **Cache de controladores** mejora la velocidad de navegación
- **Limpieza de cache** cuando sea necesario (ej: logout)
- **Carga lazy** de recursos FXML

### 🔍 Casos de Uso Comunes

#### **1. Navegación Secuencial**
```java
// Login → Main → Usuarios → Productos
navigationManager.navigateTo(Screen.LOGIN);
// Después del login exitoso:
navigationManager.navigateTo(Screen.MAIN, userParams);
// Desde el menú:
navigationManager.navigateTo(Screen.USUARIOS);
```

#### **2. Navegación con Datos**
```java
// Pasar información del usuario al main
Map<String, Object> userInfo = new HashMap<>();
userInfo.put("username", "admin");
userInfo.put("role", "Administrador");
userInfo.put("permissions", Arrays.asList("read", "write", "delete"));

navigationManager.navigateTo(Screen.MAIN, userInfo);
```

#### **3. Navegación Condicional**
```java
// Navegar según el rol del usuario
if (userRole.equals("admin")) {
    navigationManager.navigateTo(Screen.MAIN, adminParams);
} else {
    navigationManager.navigateTo(Screen.MAIN, userParams);
}
```

---

**Este sistema de navegación proporciona una base sólida y profesional para aplicaciones JavaFX, siguiendo las mejores prácticas de la industria y siendo fácil de entender para estudiantes.**

## 📖 Conceptos de Programación Aplicados

### 1. **Manejo de Excepciones**
- Uso de `try-catch` para operaciones de I/O
- Manejo graceful de errores de archivo

### 2. **Streams y Buffers**
- `BufferedReader` para lectura eficiente
- `FileWriter` para escritura de archivos

### 3. **NIO.2 API**
- `Files.exists()` para verificar existencia
- `Paths.get()` para manejo de rutas
- `Files.createDirectories()` para crear directorios

### 4. **Patrones de Diseño**
- **MVC**: Separación de Modelo, Vista y Controlador
- **Singleton**: Una instancia de FileManager por aplicación

## 🔍 Casos de Uso del Sistema de Archivos

### 1. **Registro de Usuarios**
```java
// Agregar nuevo usuario al archivo
fileManager.writeToFile(USERS_FILE, "nuevo:password123\n", true);
```

### 2. **Validación de Credenciales**
```java
// Leer y validar credenciales
List<String> usuarios = fileManager.readFromFile(USERS_FILE);
```

### 3. **Gestión de Archivos**
```java
// Verificar si existe
if (fileManager.fileExists(USERS_FILE)) {
    // Operaciones con el archivo
}

// Obtener información
long tamaño = fileManager.getFileSize(USERS_FILE);
```

## ⚠️ Consideraciones de Seguridad

### Limitaciones del Sistema Actual:
- **Contraseñas en texto plano**: No están encriptadas
- **Validación básica**: Solo verifica formato, no fortaleza
- **Sin auditoría**: No registra intentos de login

### Mejoras Futuras:
- Implementar hash de contraseñas (SHA-256, bcrypt)
- Agregar logs de acceso
- Validación de fortaleza de contraseñas
- Bloqueo temporal tras múltiples intentos fallidos

## 🎓 Aprendizajes para Estudiantes

### 1. **Manejo de Archivos**
- Cómo leer y escribir archivos de texto
- Manejo de excepciones en operaciones de I/O
- Uso de buffers para eficiencia

### 2. **Estructura de Proyectos**
- Organización en paquetes (controller, model, util)
- Separación de responsabilidades
- Uso de Maven para gestión de dependencias

### 3. **Interfaces Gráficas**
- Integración de JavaFX con lógica de negocio
- Manejo de eventos de usuario
- Validación de formularios

### 4. **Buenas Prácticas**
- Código comentado y documentado
- Manejo robusto de errores
- Uso de constantes para valores fijos

## 🔧 Personalización y Extensión

### Agregar Nuevos Tipos de Datos:
1. Crear nueva clase modelo (ej: `Producto.java`)
2. Agregar métodos en `FileManager` para el nuevo tipo
3. Crear controlador para la nueva funcionalidad
4. Diseñar interfaz FXML correspondiente

### Cambiar Formato de Almacenamiento:
- Modificar el separador en `LoginController`
- Actualizar métodos de parsing
- Migrar datos existentes si es necesario

## 📚 Recursos Adicionales

- [Java NIO.2 Tutorial](https://docs.oracle.com/javase/tutorial/essential/io/file.html)
- [JavaFX Documentation](https://openjfx.io/)
- [Maven Getting Started](https://maven.apache.org/guides/getting-started/)

## 🤝 Contribuciones

Este proyecto está diseñado para fines educativos. Las contribuciones son bienvenidas para:
- Mejorar la documentación
- Agregar nuevas funcionalidades
- Corregir bugs o problemas de seguridad
- Optimizar el rendimiento

---

**Desarrollado para el curso de Programación**  
*Sistema de Inventario v1.0 - Gestión de Archivos de Texto*
