package com.devst.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class OtrosFragment extends Fragment {

    // Botones principales del fragment
    private Button btnIrPerfil, btnAbrirWeb, btnEnviarCorreo, btnCompartir;
    private String emailUsuario = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflar layout del fragment
        View view = inflater.inflate(R.layout.fragment_otros, container, false);

        // Obtener email desde los argumentos del fragment
        Bundle args = getArguments();
        if (args != null) {
            emailUsuario = args.getString("email_usuario", "");
        }

        // Asociar botones del layout
        btnIrPerfil = view.findViewById(R.id.btnPerfil);
        btnAbrirWeb = view.findViewById(R.id.btnWeb);
        btnEnviarCorreo = view.findViewById(R.id.btnCorreo);
        btnCompartir = view.findViewById(R.id.btnCompartir);

        // Ir a perfil y pasar email
        btnIrPerfil.setOnClickListener(v -> {
            Intent perfil = new Intent(getContext(), PerfilActivity.class);
            perfil.putExtra("email_usuario", emailUsuario);
            startActivity(perfil);
        });

        // Botón para abrir web
        btnAbrirWeb.setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://santotomas.cl")))
        );

        // Botón para enviar correo
        btnEnviarCorreo.setOnClickListener(v -> {
            if (emailUsuario.isEmpty()) emailUsuario = "default@correo.com";
            Intent correo = new Intent(Intent.ACTION_SENDTO);
            correo.setData(Uri.parse("mailto:"));
            correo.putExtra(Intent.EXTRA_EMAIL, new String[]{emailUsuario});
            correo.putExtra(Intent.EXTRA_SUBJECT, "Prueba de correo");
            correo.putExtra(Intent.EXTRA_TEXT, "Hola mundo desde el botón correo");
            startActivity(Intent.createChooser(correo, "Enviar correo a:"));
        });

        // Botón para compartir texto
        btnCompartir.setOnClickListener(v -> {
            Intent compartir = new Intent(Intent.ACTION_SEND);
            compartir.setType("text/plain");
            compartir.putExtra(Intent.EXTRA_TEXT, "HOLA MUNDO DESDE ANDROID 😁👍");
            startActivity(Intent.createChooser(compartir, "Compartir con:"));
        });

        return view;
    }
}
