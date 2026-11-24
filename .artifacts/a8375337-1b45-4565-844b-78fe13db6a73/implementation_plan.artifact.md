# Implementación de Cámara, Galería y GPS en AplicaciónDAM2

Este plan detalla los cambios necesarios para integrar la funcionalidad de **Cámara**, **Galería** y **GPS** en la aplicación de gestión de coches (`aplicacionDAM2`), siguiendo las directrices de la *Guía rápida: cámara, galería y GPS*.

## User Review Required

> [!IMPORTANT]
> - Se actualizará la versión de la base de datos Room de `1` a `2` en `AppDatabase.kt` debido a la incorporación de nuevos campos en la entidad `Coche`. Al reinstalar/actualizar en el emulador, se aplicarán los nuevos cambios sin errores de migración.
> - Se añadirán botones y vistas en el formulario de añadir coche (`activity_anadir_coche.xml`) y en la pantalla de detalle (`activity_detalle_coche.xml`) para permitir capturar fotos, seleccionar de la galería y obtener coordenadas GPS.

## Open Questions

- Ninguna duda pendiente. Los contratos (`ActivityResultContracts`), el `FileProvider` y los permisos de ubicación y cámara siguen exactamente el estándar oficial explicados en la guía.

## Proposed Changes

### Dependencias y Configuración

#### [MODIFY] [build.gradle.kts](file:///C:/Users/Vaskeh/Desktop/2DAM/PMDMO/proyectoLibre/app/build.gradle.kts)
- Añadir dependencia de `play-services-location`.

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/Vaskeh/Desktop/2DAM/PMDMO/proyectoLibre/app/src/main/AndroidManifest.xml)
- Añadir permisos `CAMERA`, `ACCESS_FINE_LOCATION` y `ACCESS_COARSE_LOCATION`.
- Configurar el `FileProvider` dentro de `<application>`.

#### [NEW] [file_paths.xml](file:///C:/Users/Vaskeh/Desktop/2DAM/PMDMO/proyectoLibre/app/src/main/res/xml/file_paths.xml)
- Crear archivo de configuración de rutas para el `FileProvider`.

---

### Componentes de Utilidad y Modelo de Datos

#### [NEW] [Fotos.kt](file:///C:/Users/Vaskeh/Desktop/2DAM/PMDMO/proyectoLibre/app/src/main/java/com/example/aplicaciondam2/Fotos.kt)
- Añadir la clase de ayuda para la gestión de archivos de imagen, URIs y carga de Bitmaps.

#### [MODIFY] [Coche.kt](file:///C:/Users/Vaskeh/Desktop/2DAM/PMDMO/proyectoLibre/app/src/main/java/com/example/aplicaciondam2/Coche.kt)
- Añadir campos opcionales: `fotoPath: String? = null`, `latitud: Double? = null`, `longitud: Double? = null`.

#### [MODIFY] [AppDatabase.kt](file:///C:/Users/Vaskeh/Desktop/2DAM/PMDMO/proyectoLibre/app/src/main/java/com/example/aplicaciondam2/AppDatabase.kt)
- Incrementar la versión de la base de datos de `1` a `2`.

---

### Interfaz de Usuario y Actividades

#### [MODIFY] [activity_anadir_coche.xml](file:///C:/Users/Vaskeh/Desktop/2DAM/PMDMO/proyectoLibre/app/src/main/res/layout/activity_anadir_coche.xml)
- Añadir botones para hacer foto, elegir de galería y obtener ubicación GPS, junto con un `ImageView` de previsualización y `TextView` de ubicación.

#### [MODIFY] [AnadirCocheActivity.kt](file:///C:/Users/Vaskeh/Desktop/2DAM/PMDMO/proyectoLibre/app/src/main/java/com/example/aplicaciondam2/AnadirCocheActivity.kt)
- Implementar los lanzadores de permisos y resultados (`registerForActivityResult` para `TakePicture`, `PickVisualMedia`, `RequestPermission`, `RequestMultiplePermissions`).
- Integrar `FusedLocationProviderClient` para la geolocalización.
- Guardar el coche en Room con `fotoPath`, `latitud` y `longitud`.

#### [MODIFY] [activity_detalle_coche.xml](file:///C:/Users/Vaskeh/Desktop/2DAM/PMDMO/proyectoLibre/app/src/main/res/layout/activity_detalle_coche.xml)
- Añadir `TextView` para mostrar las coordenadas GPS si están disponibles.

#### [MODIFY] [DetalleCocheActivity.kt](file:///C:/Users/Vaskeh/Desktop/2DAM/PMDMO/proyectoLibre/app/src/main/java/com/example/aplicaciondam2/DetalleCocheActivity.kt)
- Recoger y mostrar `fotoPath` (cargada con `Fotos.cargar`) y la ubicación GPS.

#### [MODIFY] [item_coche.xml](file:///C:/Users/Vaskeh/Desktop/2DAM/PMDMO/proyectoLibre/app/src/main/res/layout/item_coche.xml) y [CocheAdapter.kt](file:///C:/Users/Vaskeh/Desktop/2DAM/PMDMO/proyectoLibre/app/src/main/java/com/example/aplicaciondam2/CocheAdapter.kt)
- Mostrar miniatura de foto y ubicación en la lista si procede.

## Verification Plan

### Automated Tests
- Ejecutar compilación con `./gradlew app:assembleDebug` para verificar que KSP y Room compilan correctamente con los nuevos campos de la Entity.
- Ejecutar pruebas unitarias con `./gradlew app:testDebugUnitTest`.

### Manual Verification
- Desplegar la app en el emulador.
- Probar la toma de fotos con la cámara (concediendo permisos).
- Probar la selección de imágenes desde la galería.
- Probar la obtención de ubicación GPS (configurando la ubicación en los Extended Controls del emulador).
- Guardar el coche, verificar que aparece en el catálogo con su foto y ubicación, y comprobar que persisten al cerrar y reabrir la app.
