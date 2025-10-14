package com.devst.app;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomeActivity extends AppCompatActivity {

    private ViewPager2 viewPager; // Contenedor de fragments con swipe horizontal
    private TabLayout tabLayout; // Pestañas para navegar entre fragments

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        // Texto de bienvenida con email del usuario
        TextView tvBienvenida = findViewById(R.id.tvBienvenida);
        String emailUsuario = getIntent().getStringExtra("email_usuario");
        if (emailUsuario != null && !emailUsuario.isEmpty()) {
            tvBienvenida.setText("¡Bienvenida, " + emailUsuario + "!");
        }


        // Configurar adapter para manejar los fragments
        viewPager.setAdapter(new ViewPagerAdapter(this));

        // Configurar TabLayout con ViewPager2 para mostrar nombres de pestañas
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) tab.setText("Evaluación");
                    else tab.setText("Otros");
                }).attach();
    }
}
