package com.devst.app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.InputStream;

public class EvaluacionFragment extends Fragment {

    // Email del usuario recibido desde la actividad principal
    private String emailUsuario = "";

    // Botones para distintas funcionalidades (linterna, Wi-Fi, SMS, cámara, contactos, galería)
    private Button btnLinterna, btnWifi, btnSeleccionarImagen, btnEnviarSMS, btnCamaraFunciones, btnAbrirContactos;
    private ImageView imageViewSeleccionada; // Muestra la imagen seleccionada
    private CameraManager camara; // Gestión de cámara para linterna
    private String camaraID = null; // ID de la cámara trasera con flash
    private boolean luz = false; // Estado de la linterna

    // Lanzador para solicitar permiso de cámara y activar linterna
    private final ActivityResultLauncher<String> permisoCamaraLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) alternarluz();
                else Toast.makeText(getContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            });

    // Lanzador para seleccionar imagen desde galería
    private final ActivityResultLauncher<Intent> seleccionarImagenLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    Uri imagenUri = result.getData().getData();
                    mostrarImagen(imagenUri);
                }
            });

    // Lanzador para seleccionar contacto y enviar SMS
    private final ActivityResultLauncher<Intent> seleccionarContactoLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    Uri contactUri = result.getData().getData();
                    if (contactUri != null) {
                        String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
                        try (Cursor cursor = getActivity().getContentResolver().query(contactUri, projection, null, null, null)) {
                            if (cursor != null && cursor.moveToFirst()) {
                                String numero = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                String mensaje = "Hola, este es un mensaje prellenado desde mi app.";
                                Intent intentSms = new Intent(Intent.ACTION_SENDTO);
                                intentSms.setData(Uri.parse("smsto:" + numero));
                                intentSms.putExtra("sms_body", mensaje);
                                startActivity(intentSms);
                            }
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Error al obtener número de contacto", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflar layout del fragment
        View view = inflater.inflate(R.layout.fragment_evaluacion, container, false);

        // Inicializar botones e imagen
        btnLinterna = view.findViewById(R.id.btnLinterna);
        btnWifi = view.findViewById(R.id.btnWifi);
        btnSeleccionarImagen = view.findViewById(R.id.btnGaleria);
        btnEnviarSMS = view.findViewById(R.id.btnSMS);
        btnCamaraFunciones = view.findViewById(R.id.btnCamaraFunciones);
        btnAbrirContactos = view.findViewById(R.id.btnContactos);
        imageViewSeleccionada = view.findViewById(R.id.imgSeleccionada);

        // Obtener email del usuario de la actividad
        emailUsuario = getActivity().getIntent().getStringExtra("email_usuario");
        if (emailUsuario == null) emailUsuario = "";

        // Inicializar CameraManager y obtener ID de cámara trasera con flash
        camara = (CameraManager) getActivity().getSystemService(getContext().CAMERA_SERVICE);
        try {
            for (String id : camara.getCameraIdList()) {
                CameraCharacteristics cc = camara.getCameraCharacteristics(id);
                Boolean flash = cc.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                Integer facing = cc.get(CameraCharacteristics.LENS_FACING);
                if (Boolean.TRUE.equals(flash) && facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                    camaraID = id;
                    break;
                }
            }
        } catch (CameraAccessException e) {
            Toast.makeText(getContext(), "No se puede acceder a la cámara", Toast.LENGTH_SHORT).show();
        }

        //Intent implicito Configuracion Wi-Fi
        // Abrir ajustes Wi-Fi
        btnWifi.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)));

        // Seleccionar imagen desde galería
        btnSeleccionarImagen.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            seleccionarImagenLauncher.launch(Intent.createChooser(intent, "Selecciona una imagen"));
        });

        //Intent implicito Enviar SMS
        // Enviar SMS manual o desde contactos
        btnEnviarSMS.setOnClickListener(v -> {
            String[] opciones = {"Ingresar número manualmente", "Seleccionar desde contactos"};
            new androidx.appcompat.app.AlertDialog.Builder(getContext())
                    .setTitle("Enviar SMS")
                    .setItems(opciones, (dialog, which) -> {
                        if (which == 0) {
                            EditText input = new EditText(getContext());
                            input.setHint("Número de teléfono");
                            new androidx.appcompat.app.AlertDialog.Builder(getContext())
                                    .setTitle("Ingrese número")
                                    .setView(input)
                                    .setPositiveButton("Enviar", (d, w) -> {
                                        String numero = input.getText().toString().trim();
                                        if (!numero.isEmpty()) {
                                            Intent sms = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + numero));
                                            sms.putExtra("sms_body", "Hola, este es un mensaje prellenado desde mi app.");
                                            startActivity(sms);
                                        } else Toast.makeText(getContext(), "Número vacío", Toast.LENGTH_SHORT).show();
                                    })
                                    .setNegativeButton("Cancelar", null)
                                    .show();
                        } else {
                            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                            seleccionarContactoLauncher.launch(intent);
                        }
                    }).show();
        });

        // Activar/desactivar linterna
        btnLinterna.setOnClickListener(v -> {
            if (camaraID == null) {
                Toast.makeText(getContext(), "Este dispositivo no tiene flash", Toast.LENGTH_SHORT).show();
                return;
            }
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) alternarluz();
            else permisoCamaraLauncher.launch(Manifest.permission.CAMERA);
        });

        // Abrir cámara frontal o trasera
        btnCamaraFunciones.setOnClickListener(v -> {
            String[] opciones = {"Cámara Frontal", "Cámara Trasera"};
            new androidx.appcompat.app.AlertDialog.Builder(getContext())
                    .setTitle("Selecciona una cámara")
                    .setItems(opciones, (dialog, which) -> {
                        Intent selectedIntent = null;
                        if (which == 0) selectedIntent = new Intent(getContext(), CamaraFrontalActivity.class);
                        else if (which == 1) selectedIntent = new Intent(getContext(), CamaraActivity.class);

                        if (selectedIntent != null) {
                            final Intent intentFinal = selectedIntent; // <-- Hacerlo final
                            view.animate().alpha(0.5f).setDuration(200).withEndAction(() -> {
                                if (getContext() != null) {
                                    requireActivity().startActivity(intentFinal);
                                    requireActivity().overridePendingTransition(R.anim.fade_in_zoom, R.anim.fade_out_zoom);
                                }
                                // Restaurar alpha al volver
                                view.animate().alpha(1f).setDuration(200).start();
                            }).start();
                        }
                    })
                    .setCancelable(true)
                    .show();
        });



        // Abrir aplicación de contactos
        btnAbrirContactos.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI)));

        return view;
    }

    // Metodo para alternar linterna encendida/apagada
    private void alternarluz() {
        try {
            luz = !luz;
            camara.setTorchMode(camaraID, luz);
            btnLinterna.setText(luz ? "Apagar Linterna" : "Encender Linterna");
        } catch (CameraAccessException e) {
            Toast.makeText(getContext(), "Error al controlar la linterna", Toast.LENGTH_SHORT).show();
        }
    }


    // Metodo para mostrar imagen seleccionada en ImageView
    private void mostrarImagen(Uri imagenUri) {
        if (imagenUri == null) return;
        try (InputStream inputStream = getActivity().getContentResolver().openInputStream(imagenUri)) {
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            imageViewSeleccionada.setImageBitmap(bitmap);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al cargar imagen", Toast.LENGTH_SHORT).show();
        }
    }
}
