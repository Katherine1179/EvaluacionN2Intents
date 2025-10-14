package com.devst.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class CameraSelectionActivity extends AppCompatActivity {

    // Botones para seleccionar cámara frontal o trasera
    Button btnCamaraFrontal, btnCamaraTrasera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_selection);

        // Inicializar botones
        btnCamaraFrontal = findViewById(R.id.btnCamaraFrontal);
        btnCamaraTrasera = findViewById(R.id.btnCamaraTrasera);

        // Acción al presionar botón de cámara frontal
        btnCamaraFrontal.setOnClickListener(v -> {
            startActivity(new Intent(this, CamaraFrontalActivity.class));
            // Aplicar transición personalizada
            overridePendingTransition(R.anim.fade_in_zoom, R.anim.fade_out_zoom);
        });

        // Acción al presionar botón de cámara trasera
        btnCamaraTrasera.setOnClickListener(v -> {
            startActivity(new Intent(this, CamaraActivity.class));
            // Aplicar transición personalizada
            overridePendingTransition(R.anim.fade_in_zoom, R.anim.fade_out_zoom);
        });
    }
}
