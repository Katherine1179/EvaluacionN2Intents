package com.devst.app;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    // Constructor: recibe la actividad que contendrá el ViewPager2
    public ViewPagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    // Crea el fragment correspondiente según la posición de la pestaña
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) return new EvaluacionFragment(); // Primer tab: Evaluación
        else return new OtrosFragment(); // Segundo tab: Otros
    }

    // Devuelve la cantidad de tabs/páginas
    @Override
    public int getItemCount() {
        return 2;
    }
}
