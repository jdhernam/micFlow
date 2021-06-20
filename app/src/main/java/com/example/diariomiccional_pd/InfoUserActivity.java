package com.example.diariomiccional_pd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

public class InfoUserActivity extends AppCompatActivity {


    // Declaración de los textViews de la clase:
    private TextView textGreeting;
    private TextView textInstructions;
    private TextView textBack;
    private TextView textNextConfirm;
    private TextView textIdType;
    private TextView textPatientSex;

    // Declaración de los editText de la clase:
    private EditText name;
    private EditText idNumber;

    // Declaración de los spinners de la clase:
    private Spinner idType;
    private Spinner patientSex;

    //Declaración de los botones de la clase:
    private Button buttonBack;
    private Button buttonNext;
    private Button buttonConfirm;

    // Declaración de la variable que indica el paso:
    private int step;
    
    // Declaración del delay de las animaciones
    private int MODULE_DELAY = 600;

    // Declaración del string con el nombre que se le dará al archivo donde se guarda la info del
    // paciente:
    private String FILE_NAME = "Info_paciente.csv";




    // ---------- MÉTODO ON CREATE EN DONDE ESTAN TODOS LOS PROCESOS MANEJADOS POR EL USUARIO: ---------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_user);


        // Esconde la navigation bar en esta activity de visualización para no distorsionar la UI en
        // pantallas pequeñas:
        fullScreenCall();


        // Inicialización de los textView del layout en la clase:
        textGreeting = findViewById(R.id.textView_welcome_info);
        textInstructions = findViewById(R.id.text_instructions_info);
        textBack = findViewById(R.id.text_backLabel_info);
        textNextConfirm = findViewById(R.id.text_nextLabel_info);
        textIdType = findViewById(R.id.text_idTypeLabel_info);
        textPatientSex = findViewById(R.id.text_patientSexLabel_info);

        // Inicialización de los edittext del layout en la clase:
        name = findViewById(R.id.editText_name_info);
        idNumber = findViewById(R.id.editText_idNumber_info);

        // Inicialización de los spinners del layout en la clase:
        idType = findViewById(R.id.spinner_idType_info);
        patientSex = findViewById((R.id.spinner_patientSex_info));

        // Inicialización de los botones del layout en la clase:
        buttonBack = findViewById(R.id.button_back_info);
        buttonNext = findViewById(R.id.button_next_info);
        buttonConfirm = findViewById(R.id.button_confirm_info);

        // Marca el step como el primero cuando se inicia la clase
        step = 1;


        // Desactiva el registro de documento cuando se crea la clase:
        idNumber.setEnabled(false);
        idNumber.setVisibility(View.INVISIBLE);

        idType.setEnabled(false);
        idType.setVisibility(View.INVISIBLE);
        textIdType.setVisibility(View.INVISIBLE);


        // Desactiva los botones de regresar y confirmar cuando se crea la clase:
        buttonBack.setEnabled(false);
        buttonBack.setVisibility(View.INVISIBLE);
        textBack.setVisibility(View.INVISIBLE);

        buttonConfirm.setEnabled(false);
        buttonConfirm.setVisibility(View.INVISIBLE);
        textNextConfirm.setText("Continuar");


        // Mensaje de bienvenida:
        runAnimationText(textGreeting, MODULE_DELAY, 0, "in", null);
        textAnimationLoopWelcome(textGreeting, 4000, "¡Bienvenida!", "¡Bienvenido!");
        
        // Animaciones de entrada de las instrucciones, registro de nombre y sexo, y botones:
        runAnimationEditTextSlide(name, 300);
        runAnimationSpinner(patientSex, MODULE_DELAY, "in");
        runAnimationText(textPatientSex, MODULE_DELAY,0, "in", null);
        runAnimationButton(buttonNext, 2 * MODULE_DELAY, 0, "in");
        runAnimationText(textNextConfirm, 2 * MODULE_DELAY, 0, "in", null);
        runAnimationText(textInstructions, 3 * MODULE_DELAY, 0, "in", null);


        // Iniciaización del spinner de tipo de ID:
        final String[] tipos = {"C.C:", "T.I:", "Registro civil:"};
        ArrayAdapter<String> adapterTipos = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipos);
        adapterTipos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        idType.setAdapter(adapterTipos);

        // Iniciaización del spinner de sexo del paciente:
        final String[] sexos = {"Seleccione:", "Hombre", "Mujer"};
        ArrayAdapter<String> adapterSexos = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sexos);
        adapterSexos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        patientSex.setAdapter(adapterSexos);







        //---------------------- BOTONES: ---------------------------------------------------------

        // Botón de regresar:
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(step == 2){

                    // Desctiva el registro de documento:
                    idNumber.setEnabled(false);
                    runAnimationEditText(idNumber, 0, "out");
                    idType.setEnabled(false);
                    runAnimationSpinner(idType, 0, "out");
                    runAnimationText(textIdType, 0,0, "out", null);

                    // Desctiva el registro de nombre y sexo de nuevo:
                    name.setEnabled(true);
                    runAnimationEditText(name, MODULE_DELAY, "in");
                    runAnimationSpinner(patientSex, MODULE_DELAY, "in");
                    runAnimationText(textPatientSex, MODULE_DELAY,0, "in", null);

                    // Desactiva el botón de confirmar:
                    buttonConfirm.setEnabled(false);
                    runAnimationButton(buttonConfirm, 0, 0, "out");
                    runAnimationText(textNextConfirm, 0, 0,  "out_in", "Continuar");

                    // Activa de nuevo el botón de next:
                    buttonNext.setEnabled(true);
                    runAnimationButton(buttonNext, MODULE_DELAY, 0, "in"); // Cambiar por animación

                    // Desactiva el botón de regresar:
                    buttonBack.setEnabled(false);
                    runAnimationButton(buttonBack, 0, 0, "out");
                    runAnimationText(textBack, 0, 0, "out", null);

                    // Cambia las instrucciones:
                    runAnimationText(textInstructions, 0, 0, "out_in", "Puede modificar los datos ingresados si desea:");

                }

                step --;

            }
        });


        // Botón de continuar/confirmar:
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!name.getText().toString().matches("") && !patientSex.getSelectedItem().toString().equals("Seleccione:")){
                    // Evita el ingreso de tildes:
                    if(Pattern.matches(".*[ÁáÉéÍíÓóÚú].*", name.getText().toString())) {
                        runAnimationText(textInstructions, 0, 0, "out_in", "Por favor no incluya tildes en su nombre.");
                    }
                    // Genera la mecánica del boton de continuar si el nombre cumple con las condiciones:
                    else {
                        continueButton();
                    }

                }
                else if(!name.getText().toString().matches("") && patientSex.getSelectedItem().toString().matches("Seleccione:")){
                    runAnimationText(textInstructions, 0, 0, "out_in", "¡Para mi es muy importante saber su sexo biológico antes de continuar!");


                }
                else if(name.getText().toString().matches("") && !patientSex.getSelectedItem().toString().matches("Seleccione:")){
                    // Cambia las instrucciones:
                    runAnimationText(textInstructions, 0, 0, "out_in", "¡Para mi es muy importante saber su nombre antes de continuar!");

                }
                else if(name.getText().toString().matches("") && patientSex.getSelectedItem().toString().equals("Seleccione:")){
                    runAnimationText(textInstructions, 0, 0, "out_in", "¡Para mi es muy importante tener estos datos antes de continuar!");

                }


            }
        });



        // Botón que guarda la info y pasa a la WelcomeActivity
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveInfo();
                openWelcomeActivity();

            }
        });





    }



    // -------- MÉTODOS POR FUERA DEL OnCreate: ----------------

    // Método que se activa al usar el boton de continuar:
    private void continueButton(){

        if(step == 1){

            // Activa el registro de documento:
            idNumber.setEnabled(true);
            runAnimationEditText(idNumber, MODULE_DELAY, "in");
            idType.setEnabled(true);
            runAnimationSpinner(idType, MODULE_DELAY, "in");
            runAnimationText(textIdType, MODULE_DELAY,0, "in", null);

            // Desctiva el registro de nombre y sexo:
            name.setEnabled(false);
            runAnimationEditText(name, 0, "out");
            runAnimationSpinner(patientSex, 0, "out");
            runAnimationText(textPatientSex, 0,0, "out", null);


            // Desctiva de nuevo el botón de next:
            buttonNext.setEnabled(false);
            runAnimationButton(buttonNext, 0, 0, "out"); // Cambiar por animación
            buttonNext.setVisibility(View.INVISIBLE);

            // Activa el botón de confirmar:
            buttonConfirm.setEnabled(true);
            runAnimationButton(buttonConfirm, MODULE_DELAY, MODULE_DELAY, "in");
            runAnimationText(textNextConfirm, 0, 0,  "out_in", "Confirmar");


            // Activa el botón de regresar:
            buttonBack.setEnabled(true);
            runAnimationButton(buttonBack, MODULE_DELAY, 0, "in");
            runAnimationText(textBack, MODULE_DELAY, 0, "in", null);

            // Cambia las instrucciones:
            runAnimationText(textInstructions, 0, 0, "out_in", name.getText().toString() + ", ahora necesito su número de identificación:");

        }

        step ++;


    }



    // ---- Almacenamiento de info: ----
    private void saveInfo() {

        // Extracción de fecha, hora, urgencia y volumen como un string:
        String nombre = "Nombre: " + name.getText().toString();
        String sexo = "Sexo: " + patientSex.getSelectedItem().toString();
        String idNumero = idNumber.getText().toString();
        String idTipo = idType.getSelectedItem().toString();

        // Comando que guarda la info en un archivo .CSV:
        // Idea tomada de: https://github.com/JohnsAndroidStudioTutorials/ReadandWritetoInternalStorage
        String dataToSave = nombre + "," + sexo + "," + idTipo + " " + idNumero;
        FileOutputStream fos = null;


        // Agrega los nuevos datos al archivo .CSV:
        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(dataToSave.getBytes());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }






    // ---- Navegación: ----
    // Método que envía al usuario a la Activity principal con cronómetro:
    private void openWelcomeActivity() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
    }




    // ---- Animaciones: ----

    // Método que genera la animación con la que aparecen los botones:
    private void runAnimationButton(Button button, int delay, int delay2, String type) {

        if(type == "in") {
            Animation a3 = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            a3.reset();
            a3.setStartOffset(delay);
            button.clearAnimation();
            button.setVisibility(View.VISIBLE);
            button.startAnimation(a3);
        }
        else if(type == "out"){
            Animation a3_2 = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
            a3_2.reset();
            a3_2.setStartOffset(delay);
            a3_2.setDuration(600);
            button.clearAnimation();
            button.startAnimation(a3_2);
            button.setVisibility(View.INVISIBLE);
        }
        else if(type == "out_in"){
            final Animation a3 = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
            final Animation a3_2 = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);

            a3.reset();
            a3.setStartOffset(delay);
            button.clearAnimation();
            button.startAnimation(a3);

            a3.setAnimationListener(new Animation.AnimationListener() {

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

                    a3_2.reset();
                    a3_2.setStartOffset(delay2);
                    button.clearAnimation();
                    button.startAnimation(a3_2);
                }
            });
        }
    }





    // Método que genera la animación con la que aparecen los botones:
    private void runAnimationEditTextSlide(EditText editText, int delay) {

        Animation a3 = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        a3.reset();
        a3.setStartOffset(delay);
        editText.clearAnimation();
        editText.startAnimation(a3);
    }
    




    // Método que genera la animación con la que aparece el texto
    private void runAnimationText(TextView text, int delay, int delay2, String type, String newText) {

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
            a1.setDuration(600);
            text.clearAnimation();
            text.startAnimation(a1);
            text.setVisibility(View.INVISIBLE);
        }

        else if(type == "out_in"){
            final Animation a1 = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
            final Animation a1_2 = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);

            a1.reset();
            a1.setStartOffset(delay);
            a1.setDuration(600);
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
                    a1_2.setStartOffset(delay2);
                    text.clearAnimation();
                    text.startAnimation(a1_2);
                }
            });
        }
    }



    // Método que genera la animación con la que aparecen los spinners:
    private void runAnimationSpinner(Spinner spinner, int delay,  String type) {

        if (type == "in") {
            Animation a1 = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);

            a1.reset();
            a1.setStartOffset(delay);
            spinner.clearAnimation();
            spinner.setVisibility(View.VISIBLE);
            spinner.startAnimation(a1);
        } else if (type == "out") {
            Animation a1 = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);

            a1.reset();
            a1.setStartOffset(delay);
            spinner.clearAnimation();
            spinner.startAnimation(a1);
            spinner.setVisibility(View.INVISIBLE);
        }
    }




    // Método que genera la animación con la que aparecen los botones:
    private void runAnimationEditText(EditText editText, int delay, String type) {

        if(type == "in") {
            Animation a5 = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            a5.reset();
            a5.setStartOffset(delay);
            editText.clearAnimation();
            editText.setVisibility(View.VISIBLE);
            editText.startAnimation(a5);
        }
        else if(type == "out"){
            Animation a5_2 = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
            a5_2.reset();
            a5_2.setStartOffset(delay);
            editText.clearAnimation();
            editText.startAnimation(a5_2);
            editText.setVisibility(View.INVISIBLE);
        }
    }





    // Método que genera la animación con la que aparece el texto:
    private void runAnimationTextNormal(TextView text, int delay, String type, String newText) {

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





    // Método que hace un loop con las animaciones para las instrucciones:
    private void textAnimationLoopWelcome(TextView text, int delay, String text1, String text2){

        Handler handler = new Handler(); // Crea el handler

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Actions to do after 10 seconds
                runAnimationTextNormal(text, 0,"out_in", text1);
            }
        }, delay);


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Actions to do after 10 seconds
                runAnimationTextNormal(text, 0, "out_in", text2);
                textAnimationLoopWelcome(text, delay, text1, text2);
            }
        }, 2* delay);
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
