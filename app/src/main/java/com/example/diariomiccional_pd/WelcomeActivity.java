package com.example.diariomiccional_pd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WelcomeActivity extends AppCompatActivity {


    // Declaración variables globales de botones:
    private Button b_registroChrono;
    private Button b_registroPosterior;
    private Button b_registroIngesta;
    private Button b_exportData;

    // Declaración de los Textviews:
    private TextView textWelcome1;
    private TextView textWelcome2;
    private TextView textWarningChrono;
    
    // Declaración de los ImageViews (íconos):
    private ImageView saveIcon;

    // Declaración del Request Code para exportar el .CSV:
    private static final int REQUEST_CODE = 1;

    // Inicializzación de la variable en la que se guarda la información del archivo a exportar:
    private String DATA_TO_EXPORT;

    // Declaración del string con el nombre que se le dará al archivo donde se guardan los
    // datos, que será el mismo para el archivo exportado:
    private String dateFileName = new SimpleDateFormat("yyyy_MMM", Locale.getDefault()).format(new Date()); // Fecha para el nombre del .CS
    private String FILE_NAME = "Diario_miccional.csv";


    // Declaración del cloud storage:
    private String dateChildName = new SimpleDateFormat("yyyy_MMM_dd", Locale.getDefault()).format(new Date()); // Fecha para el nombre del child
    private FirebaseDatabase cloudStorage = FirebaseDatabase.getInstance("https://micflow-cdc72-default-rtdb.firebaseio.com/");
    private DatabaseReference reference = cloudStorage.getReference().child(dateChildName);


    // Declaración de la sincrinización de registros a la base de datos después de estar offline:
    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }



    // ---------- MÉTODO ON CREATE EN DONDE ESTAN TODOS LOS PROCESOS MANEJADOS POR EL USUARIO: ---------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


//        // Esconde la navigation bar en esta activity de visualización para no distorsionar la UI en
//        // pantallas pequeñas:
//        fullScreenCall();

        // Inicialización de los textView del layout en la variable de la clase:
        textWelcome2 = findViewById(R.id.textView_welcome);
        textWarningChrono = findViewById(R.id.textView_chronoWarning);

        // Inicialización de botones del layout en la variable de la la clase:
        b_registroChrono = findViewById(R.id.button_registroChrono);
        b_registroPosterior = findViewById(R.id.button_registroPosterior);
        b_registroIngesta = findViewById(R.id.button_registroIngesta);
        b_exportData = findViewById(R.id.button_exportData);
        
        // Inicialización de los íconos de la activity:
        saveIcon = findViewById(R.id.imageView_exportIcon);

//        // Inicialización del saludo:
//        String horaDelDia = new SimpleDateFormat("HHmmss", Locale.getDefault()).format(new Date());


        // Animación que muestra el texto con las instrucciones y el aviso del cronómetro:
        String textGreeting1 = "¿Qué desea hacer?";
        String textGreeting2 = getGreeting();
        runAnimationText(textWelcome2,600,"in",textGreeting2); // Animación de entrada.
        textAnimationLoopWelcome(textWelcome2, 2500, textGreeting1, textGreeting2); // Instrucciones.
        runAnimationText(textWarningChrono, 700, "in", null); // Aviso cronometro.

        // Animación que muestra los botones:
        runAnimationButton(b_registroChrono, 300);
        runAnimationButton(b_registroPosterior, 400);
        runAnimationButton(b_registroIngesta, 500);
        runAnimationButton(b_exportData, 600);
        
        // Animaciones que muestran los íconos:
        runAnimationImageView(saveIcon, 1000);




        //---------------------- BOTONES: ---------------------------------------------------------

        // Botón que inicia registro con cronómetro:
        b_registroChrono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openMainActivity();
            }
        });


        // Botón que inicia registro posterior al vaciado (sin cronómetro):
        b_registroPosterior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openPosteriorActivity();
            }
        });
//
//
        // Botón para registrar ingesta de líquidos:
        b_registroIngesta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openIngestaActivity();
            }
        });


        // Botón que permite pasar a la pantalla de exportar los datos:
        b_exportData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File file = getFileStreamPath(FILE_NAME);

                if(file.exists()) {
                    openExportVisualizeActivity();
                }

                // Si no existen registros en la app, le pide al usuario hacer uno antes de exportar:
                else if(!file.exists()){
                    Toast.makeText(getBaseContext(), "Registre algún dato primero para exportar.", Toast.LENGTH_LONG).show();
                }

//                // ---- EXPORTACIÓN DE DATOS: ----
//                String dateTimeExport = new SimpleDateFormat("yyyy_MMM_dd_HHmmss", Locale.getDefault()).format(new Date());
//                FileInputStream fis = null;
//                File file = getFileStreamPath(FILE_NAME);
//                String FILE_NAME_EXPORT = "Diario_" + dateTimeExport + ".csv";
//
//                // Lee el .CSV si este existe (ESTO ES PARA CREAR EL ARCHIVO QUE SE EXPORTA):
//                if(file.exists()) {
//
//                    try {
//                        fis = openFileInput(FILE_NAME);
//                        InputStreamReader inputStreamReader = new InputStreamReader(fis);
//
//                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                        StringBuffer stringBuffer = new StringBuffer();
//
//                        String lines;
//                        while ((lines = bufferedReader.readLine()) != null) {
//                            stringBuffer.append(lines + "\n");
//                        }
//
//                        DATA_TO_EXPORT = stringBuffer.toString();
////                        displayText.setText(stringBuffer.toString());
////                        urgMic7.setText(stringBuffer.toString());
//
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//
//                    // Método que comparte el .CSV creado:
//                    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
//                    intent.addCategory(Intent.CATEGORY_OPENABLE);
//                    intent.setType("application/csv"); //not needed, but maybe useful
//                    intent.putExtra(Intent.EXTRA_TITLE, FILE_NAME_EXPORT); //not needed, but maybe useful
//                    startActivityForResult(intent, REQUEST_CODE);
//                }
//
//                // Si no existen registros en la app, le pide al usuario hacer uno antes de exportar:
//                else if(!file.exists()){
//                    Toast.makeText(getBaseContext(), "Registre algún dato primero para exportar.", Toast.LENGTH_LONG).show();
//                }

            }
        });



    }



// -------- MÉTODOS POR FUERA DEL OnCreate: ----------------


    // Método que exporta el archivo .CSV una vez se tiene la dirección para guardarlo:
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();

            //just as an example, I am writing a String to the Uri I received from the user:
            try {
                OutputStream output = getContentResolver().openOutputStream(uri);
                output.write(DATA_TO_EXPORT.getBytes());
                output.flush();
                output.close();
                Toast.makeText(getBaseContext(), "Exportado a: " + uri.getLastPathSegment(), Toast.LENGTH_LONG).show();

            } catch (IOException e) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }



    // Método que genera la animación con la que aparece el texto:
    private void runAnimationText(TextView text, int delay, String type, String newText) {

        if (type == "in") {
            Animation a1 = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);

            if(newText != null){
                text.setText(newText);
            }

            a1.reset();
            a1.setStartOffset(delay);
            text.clearAnimation();
            text.setVisibility(View.VISIBLE);
            text.startAnimation(a1);
        }

        else if(type == "out"){
            Animation a1 = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);

            a1.reset();
            a1.setStartOffset(delay);
            text.clearAnimation();
            text.startAnimation(a1);
            text.setVisibility(View.INVISIBLE);
        }

        else if(type == "out_in"){
            final Animation a1 = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
            final Animation a1_2 = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);

            a1.reset();
            a1.setStartOffset(delay);
            text.clearAnimation();
            text.startAnimation(a1);

            a1.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // We start the Activity

                    text.setText(newText);
                    a1_2.reset();
                    a1_2.setStartOffset(delay);
                    text.clearAnimation();
                    text.startAnimation(a1_2);
                }
            });
        }
    }


    // Método que genera la animación con la que aparece el texto
    private void runAnimationImageView(ImageView imageView, int delay) {

        Animation a2 = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        a2.reset();
        a2.setStartOffset(delay);
        imageView.clearAnimation();
        imageView.startAnimation(a2);
    }


    // Método que genera la animación con la que aparecen los botones:
    private void runAnimationButton(Button button, int delay) {

        Animation a3 = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        a3.reset();
        a3.setStartOffset(delay);
        button.clearAnimation();
        button.startAnimation(a3);
    }





    // Método que hace un loop con las animaciones para las instrucciones:
    private void textAnimationLoopWelcome(TextView text, int delay, String text1, String text2){

        Handler handler = new Handler(); // Crea el handler

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Actions to do after 10 seconds
                runAnimationText(text, 0, "out_in", text1);
            }
        }, delay);


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Actions to do after 10 seconds
                runAnimationText(text, 0, "out_in", text2);
                textAnimationLoopWelcome(text, delay, text1, text2);
            }
        }, 2* delay);
    }






    // Método que envía al usuario a la Acticity de registro con cronómetro:
    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // Método que envía al usuario a la Acticity de registro sin cronómetro:
    public void openPosteriorActivity() {
        Intent intent = new Intent(this, PosteriorActivity.class);
        startActivity(intent);
    }

    // Método que envía al usuario a la Acticity de registro de ingesta de líquidos:
    public void openIngestaActivity() {
        Intent intent = new Intent(this, IngestaActivity.class);
        startActivity(intent);
    }

    // Método que envía al usuario a la Acticity de registro de ingesta de líquidos:
    public void openExportVisualizeActivity() {
        Intent intent = new Intent(this, ExportVisualizeActivity.class);
        startActivity(intent);
    }





    // Método que define el saludo dependiendo de la hora del día:
    private String getGreeting(){

        LocalTime localTime = new LocalTime(DateTimeZone.getDefault()); // Hora actual

        String saludo = null; // Variable que se retorna.

        // Horas a las que cambia el saludo
        LocalTime dias = new LocalTime("00:00:00");
        LocalTime tardes = new LocalTime("12:00:00");
        LocalTime noches = new LocalTime("19:00:00");


        if(localTime.isAfter(dias) && localTime.isBefore(tardes)){
            saludo = "¡Buenos días!";
        }
        else if(localTime.isAfter(tardes) && localTime.isBefore(noches)){
            saludo = "¡Buenas tardes!";
        }
        else if(localTime.isAfter(noches)){
            saludo = "¡Buenas noches!";
        }

        return saludo;
    }







    // Método que cierra la aplicación si se usa el boton "back" de android:
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
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



    // Esconde la navigation bar en esta activity de visualización para no distorsionar la UI en
    // pantallas pequeñas:
    public void fullScreenCall() {
        if(Build.VERSION.SDK_INT < 19){
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else {
            //for higher api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }





}