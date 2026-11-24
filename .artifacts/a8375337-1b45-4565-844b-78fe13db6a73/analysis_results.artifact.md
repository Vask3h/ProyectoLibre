# Reporte de Análisis del Proyecto - Aplicación Concesionario DAM2

> [!NOTE]
> Este documento contiene el análisis integral del proyecto Android **aplicacionDAM2** ("Concesionario"), incluyendo su estructura, arquitectura, funcionalidades, tecnologías utilizadas y recomendaciones de mejora.

---

## 1. Resumen General del Proyecto

- **Nombre de la Aplicación:** Concesionario
- **Package ID:** `com.example.aplicaciondam2`
- **Módulo Principal:** `:app`
- **Propósito:** Gestión de catálogo de coches de segunda mano. Permite visualizar un catálogo de vehículos, ver sus detalles, añadir nuevos registros (incluyendo datos técnicos, captura de fotografía mediante cámara o galería y localización GPS del vehículo) y eliminar registros existentes.

---

## 2. Pila Tecnológica y Configuración de Gradle

| Componente | Tecnología / Librería | Versión / Detalle |
| :--- | :--- | :--- |
| **Lenguaje principal** | Kotlin | `2.2.10` |
| **Android Gradle Plugin** | AGP | `9.3.3` |
| **SDK objetivo / Compilación** | Target SDK 36 / Compile SDK 37 | Min SDK 24 (Android 7.0) |
| **Procesador de Anotaciones** | KSP (Kotlin Symbol Processing) | `2.3.12` |
| **Base de datos local** | Room Database (runtime, ktx, compiler) | `2.8.5` |
| **Ubicación / GPS** | Google Play Services Location | `21.3.0` (`FusedLocationProviderClient`) |
| **Vistas y UI** | XML Layouts + AppCompat + RecyclerView + Material Components | AndroidX AppCompat `1.7.0`, Material `1.12.0`, RecyclerView `1.3.2` |
| **Soporte Compose** | Jetpack Compose BOM `2026.02.01` | Habilitado en Gradle, pero la UI actual está desarrollada en XML Layouts |

---

## 3. Arquitectura y Componentes Principales

### 3.1 Capa de Datos (Data Layer)
- **`Coche`** (`Coche.kt`): Entidad de Room (`tableName = "coches"`).
  - Campos: `id` (PrimaryKey auto-generada), `marca`, `modelo`, `anio`, `precio`, `descripcion`, `imagenResId`, `fotoPath`, `latitud`, `longitud`.
- **`CocheDao`** (`CocheDao.kt`): Interfaz DAO con operaciones suspendibles de corrutinas (`insertar`, `obtenerTodos`, `borrar`, `contar`).
- **`AppDatabase`** (`AppDatabase.kt`): Singleton de Room (versión 2) con pre-poblado de datos iniciales mediante `RoomDatabase.Callback` (inserta 3 coches de prueba al crear la BD por primera vez: Seat León, VW Golf, Toyota Corolla).

### 3.2 Capa de Presentación (UI Layer)
La aplicación utiliza un esquema basado en **Activities** tradicionales comunicadas vía `Intent`s:

1. **`MainActivity`** (`MainActivity.kt` & `activity_main.xml`):
   - Pantalla principal / Dashboard.
   - Muestra el número total de vehículos registrados (actualizado en `onResume`).
   - Botones de navegación explícita hacia el catálogo y la pantalla de añadir vehículo.

2. **`CatalogoActivity`** (`CatalogoActivity.kt` & `activity_catalogo.xml`):
   - Lista vertical de vehículos mediante `RecyclerView` y `CocheAdapter`.
   - Permite pulsar sobre un elemento para ver su detalle (`DetalleCocheActivity`).
   - Permite borrar un elemento pulsando el botón de papelera o manteniendo pulsada la fila, mostrando un `AlertDialog` de confirmación.

3. **`AnadirCocheActivity`** (`AnadirCocheActivity.kt` & `activity_anadir_coche.xml`):
   - Formulario de entrada con validaciones de campos (marca, modelo, año entre 1950 y año actual+1, precio > 0, descripción).
   - Captura de fotos desde la **Cámara** usando `ActivityResultContracts.TakePicture()` y `FileProvider`.
   - Selección de imágenes desde la **Galería** mediante `ActivityResultContracts.PickVisualMedia()`.
   - Obtención de coordenadas **GPS (Latitud / Longitud)** en tiempo real usando `FusedLocationProviderClient` con nivel de precisión alto (`PRIORITY_HIGH_ACCURACY`).

4. **`DetalleCocheActivity`** (`DetalleCocheActivity.kt` & `activity_detalle_coche.xml`):
   - Muestra la ficha detallada de un vehículo recibida a través de los extras del `Intent`.
   - Carga la imagen personalizada desde el sistema de archivos (`fotoPath`) o la imagen por defecto (`imagenResId`).
   - Muestra la ubicación GPS guardada o "Ubicación: No registrada".

### 3.3 Módulos de Soporte y Utilidades
- **`Fotos`** (`Fotos.kt`): Objeto de utilidad que gestiona:
  - Creación de archivos temporales en el almacenamiento interno de la app (`filesDir/fotos/`).
  - Obtención de `Uri` seguro con `FileProvider`.
  - Copiado de fotos desde la galería URI hacia el almacenamiento local de la app.
  - Carga de imágenes en formato `Bitmap`.

---

## 4. Fortalezas del Proyecto

1. **Uso de Apis Modernas de Android:**
   - Empleo de `ActivityResultContracts` (`TakePicture`, `PickVisualMedia`, `RequestPermission`) en lugar de `startActivityForResult` deprecado.
   - Uso de `FusedLocationProviderClient` para posicionamiento GPS eficiente.
   - Persistencia local con Room utilizando Corrutinas Kotlin (`suspend` functions y `lifecycleScope`).
2. **Seguridad y Gestión de Ficheros:**
   - Configuración correcta de `FileProvider` en `AndroidManifest.xml` y `res/xml/file_paths.xml` para compartir URIs de cámara de forma segura.
3. **Control de Flujo de Usuario y Manejo de Ciclo de Vida:**
   - La lista del catálogo y el resumen de la pantalla principal se recargan automáticamente en `onResume()`.
4. **Validaciones de Entrada:**
   - Comprobaciones exhaustivas de tipos, rango de años y precios positivos en `AnadirCocheActivity`.

---

## 5. Oportunidades de Mejora y Recomendaciones

> [!TIP]
> **1. Refactorización a Patrón MVVM (ViewModel + LiveData / StateFlow)**
> Actualmente las actividades gestionan directamente las llamadas a la base de datos dentro de `lifecycleScope.launch`. Implementar `ViewModel` separaría la lógica de negocio de la UI y conservaría el estado en cambios de orientación de pantalla.

> [!TIP]
> **2. Optimización en la Carga de Imágenes**
> `Fotos.cargar(path)` utiliza `BitmapFactory.decodeFile()` en el hilo UI sin aplicar submuestreo (`inSampleSize`). Con fotos de alta resolución tomadas por cámaras modernas, esto puede causar lags o fallos de memoria (`OutOfMemoryError`). Se recomienda usar una librería como **Coil** o aplicar decodificación con escalado.

> [!IMPORTANT]
> **3. Simplificación en el Paso de Datos entre Actividades**
> `CatalogoActivity` pasa más de 8 extras individuales por Intent a `DetalleCocheActivity`. Sería preferible pasar únicamente el `id` del coche y consultarlo desde la BD en la pantalla de detalle, o hacer `Coche` implemente `@Parcelize Parcelable`.

> [!NOTE]
> **4. Limpieza de Código y Dependencias No Utilizadas**
> - `CocheRepository.kt` está actualmente vacío (se mantiene solo un comentario sobre Room).
> - Se incluyen dependencias de Jetpack Compose en `build.gradle.kts` e archivos de tema en `ui/theme/`, pero la app está construida con Views XML. Se podría decidir migrar la UI totalmente a Compose o limpiar las dependencias si no se va a utilizar.
> - Recursos sin uso en `res/raw/` (`sonido1.mp3`, `sonido2.mp3`, `sonido3.mp3`) e imágenes en `res/drawable/` (`foto_arbol.jpg`, `foto_pantera.jpg`).

> [!NOTE]
> **5. Internacionalización de Strings**
> Hay cadenas de texto harcodeadas en código Kotlin (p. ej. `tvResumen.text = "Coches registrados: $total"`). Moverlas a `res/values/strings.xml` facilitará la traducción y resolverá advertencias de lint.

---
