package com.devst.app;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Habilitar Edge-to-Edge para que el contenido use toda la pantalla
        EdgeToEdge.enable(this);
        // Cargar el layout del perfil
        setContentView(R.layout.activity_perfil);

    }
}