# Documentación del Proyecto: Aplicación DAM2 (Concesionario de Coches)

---

## 1. Estructura del Proyecto

El proyecto sigue una arquitectura modular orientada a Activities en Kotlin, organizada bajo el paquete raíz `com.example.aplicaciondam2`:

###  Capa de Datos y Persistencia (Room)
- **`Coche.kt` (`@Entity`)**: Define la tabla `coches` en SQLite, incluyendo campos de identificación (`id` autogenerado), marca, modelo, año, precio, descripción, ruta de foto (`fotoPath`) y coordenadas GPS (`latitud`, `longitud`).
- **`CocheDao.kt` (`@Dao`)**: Interfaz de acceso a datos que expone operaciones asíncronas (`insertar`, `obtenerTodos`, `borrar`, `contar`) mediante funciones `suspend`.
- **`AppDatabase.kt` (`@Database`)**: Clase abstracta singleton que instancia la base de datos Room (`coches_database`), configurada con un callback para precargar datos iniciales y migración destructiva automática (`.fallbackToDestructiveMigration()`).

###  Capa de Utilidades y Gestión de Archivos
- **`Fotos.kt`**: Objeto utilitario responsable de la creación segura de archivos JPEG en el almacenamiento interno de la app (`filesDir/fotos`), la generación de URIs mediante `FileProvider`, la copia de imágenes seleccionadas desde la galería y la decodificación en `Bitmap`.

###  Capa de Interfaz de Usuario (Activities y Adaptadores)
- **`MainActivity.kt`**: Pantalla principal (*Dashboard*). Muestra el resumen de vehículos registrados (`cocheDao.contar()`) y botones de navegación hacia el catálogo y el formulario de alta.
- **`CatalogoActivity.kt`**: Pantalla que lista todos los coches guardados en la base de datos usando un `RecyclerView` y el `CocheAdapter`. Permite ver detalles mediante un toque corto o eliminar registros con confirmación.
- **`AnadirCocheActivity.kt`**: Formulario de alta de vehículos. Valida datos de entrada y gestiona la interacción con la **Cámara**, la **Galería** y el **GPS** mediante contratos de resultados de actividad.
- **`DetalleCocheActivity.kt`**: Pantalla de detalle que muestra la imagen completa del coche, sus características técnicas y su ubicación GPS registrada.
- **`CocheAdapter.kt`**: Adaptador del `RecyclerView` optimizado para pintar elementos y manejar la carga condicional de miniaturas de fotos y eventos de borrado/clic.

---

## 2. Justificación de las APIs y Librerías Utilizadas

Para cumplir con los requisitos de persistencia local, robustez y experiencias multimedia/geolocalización, se han seleccionado las siguientes APIs oficiales de Android:

### A. Room Persistence Library (`androidx.room`)
- **Por qué se usa**: A diferencia de SharedPreferences (limitada a pares clave-valor simples) o listas en memoria volátiles, Room proporciona una capa de abstracción sobre SQLite robusta, tipada y verificada en tiempo de compilación.
- **Ventaja**: Garantiza que los datos del concesionario (vehículos, fotos asociadas y coordenadas) persistan de manera segura tras cerrar completamente la aplicación, integrándose perfectamente con corrutinas de Kotlin mediante funciones `suspend`.

### B. Activity Result API (`androidx.activity.result.contract.ActivityResultContracts`)
- **Por qué se usa**: Las APIs heredadas como `startActivityForResult` están deprecadas y propensas a errores de ciclo de vida. La Activity Result API desacopla el lanzamiento de intents de la recepción de resultados.
- **Contratos específicos utilizados**:
  - `RequestPermission` / `RequestMultiplePermissions`: Para solicitar permisos de `CAMERA`, `ACCESS_FINE_LOCATION` y `ACCESS_COARSE_LOCATION` en tiempo de ejecución.
  - `TakePicture`: Para invocar la aplicación de cámara del sistema y guardar la fotografía en un archivo privado gestionado por el `FileProvider`.
  - `PickVisualMedia`: Para invocar el selector visual de fotos nativo del sistema sin necesidad de solicitar permisos de almacenamiento externo.

### C. Google Play Services Location (`com.google.android.gms:play-services-location:21.4.0`)
- **Por qué se usa**: Proporciona el cliente `FusedLocationProviderClient`, que gestiona de forma inteligente y eficiente las fuentes de ubicación del dispositivo (GPS, Wi-Fi, antenas de telefonía).
- **Ventaja**: Permite obtener la posición geográfica actual con alta precisión (`Priority.PRIORITY_HIGH_ACCURACY`) mediante `getCurrentLocation(...)` para asociar coordenadas GPS a cada vehículo registrado.

### D. Corrutinas de Kotlin y Lifecycle Scope (`androidx.lifecycle.lifecycleScope`)
- **Por qué se usa**: Android prohíbe realizar operaciones de E/S o base de datos en el hilo principal (*Main Thread*), ya que provocaría bloqueos (*ANR*).
- **Ventaja**: El uso de `lifecycleScope.launch` junto con funciones `suspend` en el DAO permite ejecutar consultas pesadas en segundo plano de manera limpia y sincronizada con el ciclo de vida de la Activity.

### E. FileProvider (`androidx.core.content.FileProvider`)
- **Por qué se usa**: Permite compartir de forma segura ficheros privados de la aplicación con aplicaciones externas (como la cámara del sistema) mediante URIs temporales con permisos concedidos (`grantUriPermissions`), evitando excepciones de tipo `FileUriExposedException` introducidas en versiones modernas de Android.
