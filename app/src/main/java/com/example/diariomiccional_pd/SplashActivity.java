package com.example.diariomiccional_pd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import java.io.File;

public class SplashActivity extends AppCompatActivity {

    private View SplashView;
    private Handler handler;
    int duration = 300;


    // Declaración del string con el nombre que se le dará al archivo donde se guarda la info del
    // paciente:
    private String INFO_FILE_NAME = "Info_paciente.csv";



    // ---------- MÉTODO ON CREATE EN DONDE ESTAN TODOS LOS PROCESOS MANEJADOS POR EL USUARIO: ---------
    // Idea tomada de: https://www.youtube.com/watch?v=HW5udWvGH-U&ab_channel=SmallAcademy
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        // Desactiva el night mode en la aplicación:
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Configura la splash screen:
        SplashView = findViewById(R.id.text_logo);
        SplashView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


            File file = getFileStreamPath(INFO_FILE_NAME);


        // Método que inicializa la pantalla de bienvenida y pasa a la actividad principal de la app:
        if(file.exists()){
            openWelcomeActivity();
        }
        else if(!file.exists()){
            openInfoUserActivity();
        }


    }


    // ---- Método attachBaseContext: ----
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);

        // Método que evita cambios en el tamaño de fuente de la aplicación generados por
        // la configuración de tamaño de fuente del celular
        Configuration config = new Configuration(newBase.getResources().getConfiguration());
        config.fontScale = 1.0f;
        applyOverrideConfiguration(config);

        // Método que instala  instala la librería multidex:
        MultiDex.install(this);
    }





    // ---- Navegación: ----

    // Método que envía al usuario a la Activity principal con cronómetro:
    private void openInfoUserActivity() {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), InfoUserActivity.class));
            }
        }, duration); // Tiempo en ms que demora mostrandose la pantalla de inicio.

    }


    // Método que inicializa la pantalla de bienvenida y pasa a la actividad principal de la app:
    private void openWelcomeActivity(){

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
            }
        }, duration); // Tiempo en ms que demora mostrandose la pantalla de inicio.

    }


}