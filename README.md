# App DevST

## Resumen del Proyecto
Esta aplicación permite realizar diversas funciones de sistema y utilidades, como abrir configuraciones de Wi-Fi, enviar SMS, activar linterna, gestionar contactos, usar cámara y galería, y compartir información.  
La app cuenta con un diseño por fragmentos usando `ViewPager2` y pestañas para navegar entre **Evaluación** y **Otros**.  

**Versión de Android/AGP:**  
- **Min SDK:** 31 (Android 12)  
- **Target/Compile SDK:** 36 (Android 13)  
- **Android Gradle Plugin:** 8.13.0

---

## Credenciales: mail: estudiante@st.cl password: 123456

## Intents Implementados

### 5 Intents Implícitos

1. **Abrir Configuración Wi-Fi**  
   - **Intent:** `ACTION_WIFI_SETTINGS`  
   - **Pasos de prueba:**   
     1. Presionar **"Abrir configuraciones Wi-Fi"**.  
     2. Verificar que se abra la pantalla de Wi-Fi del sistema.  

2. **Enviar SMS**  
   - **Intent:** `ACTION_SENDTO`  
   - **Pasos de prueba:**  
     1. Presionar **"Enviar SMS"**.  
     2. Elegir **Ingresar número manualmente** o **Seleccionar desde contactos**.  
     3. Ingresar número o seleccionar contacto y comprobar que se abra la app de SMS con el mensaje prellenado.  

3. **Prender Linterna**  
   - **Intent:** `CameraManager.setTorchMode`  
   - **Pasos de prueba:**  
     1. Presionar **"Prender linterna"**.  
     2. Conceder permiso de cámara si se solicita.  
     3. Verificar que el flash trasero del dispositivo se encienda y que el texto del botón cambie a **"Apagar Linterna"**.  

4. **Ver Contactos**  
   - **Intent:** `ACTION_VIEW` con URI `ContactsContract.Contacts.CONTENT_URI`  
   - **Pasos de prueba:**  
     1. Presionar **"Ver contactos"**.  
     2. Comprobar que se abra la aplicación de contactos mostrando la lista.  

5. **Seleccionar Imagen desde Galería**  
   - **Intent:** `ACTION_GET_CONTENT` con tipo `image/*`  
   - **Pasos de prueba:**  
     1. Presionar **"Seleccionar imagen galería"**.  
     2. Elegir cualquier imagen.  
     3. Verificar que la imagen seleccionada se muestre en el `ImageView`.  

---

### 3 Intents Explícitos

1. **FragmentActivity con ViewPager**  
   - **Destino:** `FragmentActivity` que contiene los fragments de Evaluación y Otros.  
   - **Pasos de prueba:**  
     1. Abrir la app.  
     2. Verificar que se muestren las pestañas de Evaluación y Otros.  
     3. Cambiar entre pestañas y comprobar que los botones de cada fragment se muestren correctamente.  

2. **OverridePendingTransition al abrir cámara**  
   - **Destino:** `CamaraActivity` o `CamaraFrontalActivity`  
   - **Pasos de prueba:**  
     1. Presionar botón **"Camara"** en pestaña Evaluación.  
     2. Elegir **Cámara Frontal** o **Trasera**.  
     3. Comprobar que se abra la actividad de cámara con la transición personalizada (`fade_in_zoom` / `fade_out_zoom`).  
     4. Volver atrás y comprobar que la transición de regreso también se aplique.  

3. **SwitchActivity**  
   - **Destino:** `CamaraActivity` o `CamaraFrontalActivity`  
   - **Pasos de prueba:**  
     1. Presionar el botón que abre la actividad.  
     2. Verificar que se abra la actividad específica correspondiente.  

---

## Capturas de Pantalla / GIF
![Demostracion app](screenshots/gifaplicacion.gif)

---

## APK Debug

El APK de debug se encuentra en:  
app/build/outputs/apk/debug/app-debug.apk


O para compilar manualmente:  
1. Abrir Android Studio.  
2. Seleccionar **Build → Build Bundle(s) / APK(s) → Build APK(s)**  
3. El APK se genera en la ruta anterior.
