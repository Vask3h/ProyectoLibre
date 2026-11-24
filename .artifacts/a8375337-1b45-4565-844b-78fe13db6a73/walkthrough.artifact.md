# Walkthrough - Resumen de Cambios Proyecto V2

Se ha completado con éxito la evolución del proyecto **Concesionario** a la **Versión 2**, según los requisitos especificados en `Proyecto_V2_App_conectada_a_Internet.pdf`.

---

## 1. Resumen de Cambios Realizados

### Hito 1: Integración de API REST y Peticiones Online
- **Configuración de red:** Permisos `<uses-permission android:name="android.permission.INTERNET" />` y `ACCESS_NETWORK_STATE` añadidos al `AndroidManifest.xml`.
- **Integración de Retrofit 2 y Gson:**
  - `NhtsaModels.kt`: DTOs para mapear las respuestas de la API de vehículos NHTSA VPIC (`NhtsaModelResponse`, `NhtsaModelDto`).
  - `ApiService.kt`: Interfaz con métodos `suspend` para la consulta de modelos por marca.
  - `RetrofitClient.kt`: Singleton para manejar el cliente de red contra `https://vpic.nhtsa.dot.gov/api/`.

### Hito 2: Detalle, Carga de Imágenes y Caché Offline con Room
- **Glide para carga de imágenes:**
  - Actualización de `CocheAdapter.kt` y `DetalleCocheActivity.kt` para decodificar e interactuar con imágenes remotas mediante la librería Glide.
- **Estrategia de Caché en Room:**
  - `Coche.kt`: Entidad ampliada con `esOnline: Boolean` e `imageUrl: String?`.
  - `CocheDao.kt`: Añadida consulta `@Query` e inserción en lote con `@Insert(onConflict = OnConflictStrategy.REPLACE)` para almacenar en caché las consultas de la API.
  - `CocheRepository.kt`: Implementa el patrón Repository unificado. Cuando hay conexión, consulta la API REST y guarda los datos en Room. Si la conexión falla o el dispositivo está en Modo Avión, recupera automáticamente los datos cacheados en Room.

### Hito 3: Arquitectura MVVM y Mejora Propia
- **Patrón MVVM (ViewModel + Repository):**
  - `CatalogoViewModel.kt`: Administra el estado de la interfaz (`CatalogoUiState`: `Cargando`, `Exito`, `Error`), protegiendo la UI ante cambios de configuración y eliminando lógica de red/BD en la Activity.
  - `CatalogoActivity.kt` & `activity_catalogo.xml`: Rediseño con pestañas/toggle entre "Mis Coches (Local)" y "Catálogo API Online", selector Spinner por marcas (Seat, Toyota, Ford, BMW, VW, etc.), indicador de carga `ProgressBar` y banner de estado/error.
- **Mejora Propia (Importador de Modelos al Concesionario):**
  - Botón de acción **"Importar al concesionario"** (`btnImportar`) en cada vehículo del catálogo online.
  - Al pulsar importar, se abre `AnadirCocheActivity` autocompletando la marca y el modelo, permitiendo al usuario tomar una fotografía real con la cámara y capturar la posición GPS actual de su concesionario.

---

## 2. Archivos Creados y Modificados

```diff
  gradle/libs.versions.toml
  app/build.gradle.kts
  app/src/main/AndroidManifest.xml
+ app/src/main/java/com/example/aplicaciondam2/data/remote/NhtsaModels.kt
+ app/src/main/java/com/example/aplicaciondam2/data/remote/ApiService.kt
+ app/src/main/java/com/example/aplicaciondam2/data/remote/RetrofitClient.kt
+ app/src/main/java/com/example/aplicaciondam2/ui/CatalogoViewModel.kt
+ app/src/main/res/drawable/ic_add.xml
  app/src/main/java/com/example/aplicaciondam2/Coche.kt
  app/src/main/java/com/example/aplicaciondam2/CocheDao.kt
  app/src/main/java/com/example/aplicaciondam2/AppDatabase.kt
  app/src/main/java/com/example/aplicaciondam2/CocheRepository.kt
  app/src/main/java/com/example/aplicaciondam2/CocheAdapter.kt
  app/src/main/java/com/example/aplicaciondam2/CatalogoActivity.kt
  app/src/main/java/com/example/aplicaciondam2/AnadirCocheActivity.kt
  app/src/main/java/com/example/aplicaciondam2/DetalleCocheActivity.kt
  app/src/main/res/layout/activity_catalogo.xml
  app/src/main/res/layout/item_coche.xml
```

---

## 3. Resultados de Verificación

- **Compilación Gradle:** Ejecutada la tarea `app:assembleDebug` con estado de éxito total (`Build finished successfully`).
- **Análisis de código estático:** Todos los archivos pasaron el análisis de inspección `analyze_file` con **0 errores**.
