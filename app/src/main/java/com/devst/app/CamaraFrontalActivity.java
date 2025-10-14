package com.devst.app;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class CamaraFrontalActivity extends AppCompatActivity {

    // Vistas de la interfaz
    private PreviewView previewView;
    private ImageView imgPreview, imgUltimaFoto;
    private TextView tvUltimaFoto;
    private LinearLayout layoutGuardar;
    private Button btnCapturar, btnGuardar, btnNoGuardar;

    // Objeto para capturar fotos
    private ImageCapture imageCapture;

    // URIs para la foto actual y última foto guardada
    private Uri fotoUri, ultimaFotoUri;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara);

        // Inicializar vistas
        previewView = findViewById(R.id.previewView);
        imgPreview = findViewById(R.id.imgPreview);
        imgUltimaFoto = findViewById(R.id.imgUltimaFoto);
        tvUltimaFoto = findViewById(R.id.tvUltimaFoto);
        layoutGuardar = findViewById(R.id.layoutGuardar);
        btnCapturar = findViewById(R.id.btnCapturar);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnNoGuardar = findViewById(R.id.btnNoGuardar);

        // Ocultar barra de sistema para pantalla completa
        hideSystemUI();
        // Iniciar cámara frontal
        startCamera();

        // Capturar foto al presionar botón
        btnCapturar.setOnClickListener(v -> takePhoto());

        // Guardar foto en galería
        btnGuardar.setOnClickListener(v -> {
            if (fotoUri == null) return;

            ultimaFotoUri = fotoUri;

            // Guardar en galería al presionar "Guardar"
            try {
                // Crear nombre de archivo con fecha y hora
                String nombreArchivo = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                        .format(new Date()) + ".jpg";

                // Guardar foto en galería para Android 10+
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DISPLAY_NAME, nombreArchivo);
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    values.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/MiAppFotos");

                    Uri uriGaleria = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    if (uriGaleria != null) {
                        try (OutputStream out = getContentResolver().openOutputStream(uriGaleria)) {
                            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                            Toast.makeText(this, "Foto guardada en galería", Toast.LENGTH_SHORT).show();
                        }
                    }
                    // Guardar foto en versión legacy
                } else {
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(fotoUri);
                    sendBroadcast(mediaScanIntent);
                    Toast.makeText(this, "Foto guardada (Legacy)", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "No se pudo guardar en galería, queda en la app", Toast.LENGTH_SHORT).show();
            }

            // Mostrar última foto guardada
            imgUltimaFoto.setImageURI(ultimaFotoUri);
            imgUltimaFoto.setVisibility(View.VISIBLE);
            tvUltimaFoto.setVisibility(View.VISIBLE);
            resetPreview();
        });

        // Cancelar guardado y reiniciar vista previa
        btnNoGuardar.setOnClickListener(v -> resetPreview());

        // Controlar acción de retroceso con transición
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
                overridePendingTransition(R.anim.fade_in_zoom, R.anim.fade_out_zoom);
            }
        });
    }

    // Metodo para ocultar UI y mostrar pantalla completa

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }

    // Iniciar cámara frontal usando CameraX
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().build();

                // Selección de cámara frontal
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build();


                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al iniciar cámara", Toast.LENGTH_LONG).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    // Capturar foto y mostrar vista previa
    private void takePhoto() {
        if (imageCapture == null) return;

        String nombreArchivo = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date()) + ".jpg";

        photoFile = new File(getExternalFilesDir("Pictures"), nombreArchivo);

        ImageCapture.OutputFileOptions outputOptions =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        // Guardar URI de foto y mostrar en ImageView
                        fotoUri = FileProvider.getUriForFile(
                                CamaraFrontalActivity.this,
                                getPackageName() + ".fileprovider",
                                photoFile
                        );
                        imgPreview.setImageURI(fotoUri);
                        imgPreview.setVisibility(View.VISIBLE);
                        layoutGuardar.setVisibility(View.VISIBLE);
                        btnCapturar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(CamaraFrontalActivity.this, "Error al capturar: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Reiniciar vista previa para tomar nueva foto
    private void resetPreview() {
        imgPreview.setVisibility(View.GONE);
        layoutGuardar.setVisibility(View.GONE);
        btnCapturar.setVisibility(View.VISIBLE);
    }
}
