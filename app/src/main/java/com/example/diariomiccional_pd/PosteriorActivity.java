package com.example.diariomiccional_pd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.multidex.MultiDex;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class PosteriorActivity extends AppCompatActivity {



    // Declaración variables globales de botones:
    private Button b_savePosteior;
    private Button b_back;
    private Button b_cancel;

    // Declaración variables globales de spinners:
    private Spinner s_Urg;

    // Declaración variables globales de edittext:
    private EditText n_Vol;

    // Declaración variables globales de booleanos:
    private boolean volSelected;
    private boolean typeSelected;
    private boolean dataRegistered;
    private boolean spinnerTouched;

    // Declaración del TextView con las instrucciones:
    private TextView textInstructions;

    // Declaración de los TextView con los labels de los botónes a exccepción del del cronómetro:
    private TextView labelUrg;
    private TextView labelVol;
    private TextView labelSave;
    private TextView labelBack;
    private TextView labelCancel;

    // Declaración de las variables con los ImageView de los íconos de guardar y exportar:
    private ImageView iconSave;

    // Declaración del divider que tiene el EditText del volumen:
    private View dividerVol;

    // Declaración de shared preferences:
    private static final String SHARED_PREFERENCES = "SHARED_PREFERENCES";

    // Declaración del Request Code para exportar el .CSV:
    private static final int REQUEST_CODE = 1;

    // Inicializzación de la variable en la que se guarda la información del archivo a exportar:
    private String DATA_TO_EXPORT;

    // Declaración del int con el delay que tendrán todos los módulos de registro:
    private static final int moduleDelay = 300;

    // Declaración del string con el nombre que se le dará al archivo donde se guardan los
    // datos, que será el mismo para el archivo exportado:
    private String FILE_NAME = "Diario_miccional.csv";

    // Declaración del cloud storage:
    private String dateChild = new SimpleDateFormat("yyyy_MMM_dd", Locale.getDefault()).format(new Date()); // Fecha para el nombre del child
    private String typeChild = "Vaciado";
    private FirebaseDatabase cloudStorage = FirebaseDatabase.getInstance();
    private DatabaseReference reference = cloudStorage.getReference().child(typeChild).child(dateChild);



    // ---------- MÉTODO ON CREATE EN DONDE ESTAN TODOS LOS PROCESOS MANEJADOS POR EL USUARIO: ---------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posterior);


        // Esconde la navigation bar en esta activity de visualización para no distorsionar la UI en
        // pantallas pequeñas:
        fullScreenCall();

//        // Desactiva el night mode en la aplicación:
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

//        // Avisa al usuario que debe utilizar el cronómetro para iniciar un registro:
//        Toast.makeText(getBaseContext(), "Si desea incluir el flujo promedio en el registro use el cronómetro.",
//                Toast.LENGTH_LONG).show();


        // Inicialización de botones del layout en las variables de la clase:
        b_savePosteior = findViewById(R.id.button_save_posterior);
        b_back = findViewById(R.id.button_back_posterior);
        b_cancel = findViewById(R.id.button_cancel_posterior);

        // Inicialización de spinners y textEdits del layout en las variables de la clase:
        s_Urg = findViewById(R.id.spinner_urgVaciado_posterior);
        n_Vol = findViewById(R.id.EditText_vol_posterior);
        volSelected = false;
        dataRegistered = false;
        spinnerTouched = false;

        
        // Inicialización de los TextView del layout con los labels de los botónes a exccepción del del cronómetro:
        labelUrg = findViewById(R.id.text_urgLabel_posterior);
        labelVol = findViewById(R.id.text_volLabel_posterior);
        labelSave = findViewById(R.id.text_saveLabel_posterior);
        labelBack = findViewById(R.id.text_backLabel_posterior);
        labelCancel = findViewById(R.id.textView_cancelLabel_posterior);

        // Inicialización de los ImageView del layout con los íconos de guardar y exportar:
        iconSave = findViewById(R.id.imageView_saveIcon_posterior);

        // Inicialización del divider (View) que tiene el módulo de volumen:
        dividerVol = findViewById(R.id.dividerVol_posterior);

        // Inicialización del TextView con las instrucciones del layout en la clase:
        textInstructions = findViewById(R.id.text_instructions_posterior);
        textInstructions.setText("Seleccione el nivel de urgencia que sintió ..."); // Configura la primera instrucción en el TextEdit de instrucciones:
        runAnimationText(textInstructions,300,"in",null); // Animación de entrada.


        // Iniciaización del spinner con los niveles de urgencia
        final String[] urgencias = {"Seleccione nivel:", "Ninguna", "Poca", "Moderada","Mucha"};
        final int[] valores1 = {0,1,2,3,4};
        Spinner spinner_urgVaciado;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, urgencias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s_Urg.setAdapter(adapter);


        // --- Desactiva todas las funciones que no son el cronómetro cuando se crea la actividad: ---
        // Urgencia:
        s_Urg.setEnabled(true);
        s_Urg.setVisibility(View.VISIBLE);
        labelUrg.setVisibility(View.VISIBLE);

        // Volumen:
        n_Vol.setEnabled(false);
        n_Vol.setVisibility(View.INVISIBLE);
        labelVol.setVisibility(View.INVISIBLE);
        dividerVol.setVisibility(View.INVISIBLE);

        // Registro:
        b_savePosteior.setEnabled(false);
        b_savePosteior.setVisibility(View.INVISIBLE);
        labelSave.setVisibility(View.INVISIBLE);
        iconSave.setVisibility(View.INVISIBLE);

        // Exportación:
        b_back.setEnabled(false);
        b_back.setVisibility(View.INVISIBLE);
        labelBack.setVisibility(View.INVISIBLE);


        // --- Se activa el botón de cancelar: ---
        b_cancel.setEnabled(true);
        runAnimationButton(b_cancel, moduleDelay, "in");
        b_cancel.setVisibility(View.VISIBLE);
        runAnimationText(labelCancel, moduleDelay, "in", null);
        labelCancel.setVisibility(View.VISIBLE);



        // ------ LÓGICA DE ACTIVACIÓN Y DESACTIVACIÓN DE BOTONES: ------

        // Condicional que activa el botón de guardar datos cuando se ha seleccionado un nivel de urgencia y el volumen
        s_Urg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (spinnerTouched == true && s_Urg.getSelectedItem().toString() == "Seleccione nivel:") {
//                    ViewCompat.setBackgroundTintList(b_saveChrono, ContextCompat.getColorStateList(getApplicationContext(), R.color.save_button_gray));
//                    b_saveChrono.setEnabled(false);

                    runAnimationEditText(n_Vol, moduleDelay, "out");
                    runAnimationText(labelVol, moduleDelay, "out", null);
                    runAnimationView(dividerVol, moduleDelay, "out");

                    n_Vol.setEnabled(false);
                    n_Vol.setVisibility(View.INVISIBLE);
                    labelVol.setVisibility(View.INVISIBLE);
                    dividerVol.setVisibility(View.INVISIBLE);


                    runAnimationText(textInstructions,0,"out_in","Seleccione un nivel de urgencia válido...");

                    typeSelected = false;
                }

                // Garantiza que el botón de guardar sólo se active al seleccionar un nivel de urgencia
                // cuando se ha ingresado un valor de volumen:
                else if(dataRegistered == false && s_Urg.getSelectedItem().toString() != "Seleccione nivel:"){
//                    ViewCompat.setBackgroundTintList(b_saveChrono, ContextCompat.getColorStateList(getApplicationContext(), R.color.save_button_yellow));
//                    b_saveChrono.setEnabled(true);
                    n_Vol.setEnabled(true);
                    n_Vol.setVisibility(View.VISIBLE);
                    labelVol.setVisibility(View.VISIBLE);
                    dividerVol.setVisibility(View.VISIBLE);

                    runAnimationEditText(n_Vol, moduleDelay, "in");
                    runAnimationText(labelVol, moduleDelay, "in", null);
                    runAnimationView(dividerVol, moduleDelay, "in");

                    runAnimationText(textInstructions,0,"out_in","Ahora, ingrese el volumen de vaciado...");

                    spinnerTouched = true;
                    typeSelected = true;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                b_savePosteior.setEnabled(false);
            }
        });




        // Condicional que activa el botón de guardar cuando hay un volumen ingresado:
        n_Vol.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Garantiza que el botón de guardar sólo se active al ingresar el volumen
                // cuando se ha detenido el cronometro y se ha seleccionado un nivel de urgencia:
                int lenVolInput = n_Vol.getText().toString().trim().length();

                if(spinnerTouched == true && lenVolInput == 1) {
//                    ViewCompat.setBackgroundTintList(b_saveChrono, ContextCompat.getColorStateList(getApplicationContext(), R.color.save_button_yellow));
                    b_savePosteior.setEnabled(true);
                    b_savePosteior.setVisibility(View.VISIBLE);
                    labelSave.setVisibility(View.VISIBLE);
                    iconSave.setVisibility(View.VISIBLE);

                    runAnimationButton(b_savePosteior, 2 * moduleDelay, "in");
                    runAnimationText(labelSave, 2 * moduleDelay, "in", null);
                    runAnimationImageView(iconSave, 2 * moduleDelay, "in");

                    // Se cambia el estado del textView de las instrucciones:
                    runAnimationText(textInstructions,0,"out_in","Regristre los datos con el botón...");

                    volSelected = true;
                }

                else if(spinnerTouched == true && lenVolInput == 1){
                    b_savePosteior.setEnabled(true);
                }

                else if(spinnerTouched == true && lenVolInput <= 0 && dataRegistered == false) {
//                    ViewCompat.setBackgroundTintList(b_saveChrono, ContextCompat.getColorStateList(getApplicationContext(), R.color.save_button_yellow));
                    runAnimationButton(b_savePosteior, moduleDelay, "out");
                    runAnimationText(labelSave, moduleDelay, "out", null);
                    runAnimationImageView(iconSave, moduleDelay, "out");
                    b_savePosteior.setEnabled(false);

                    b_savePosteior.setVisibility(View.INVISIBLE);
                    labelSave.setVisibility(View.INVISIBLE);
                    iconSave.setVisibility(View.INVISIBLE);

                    // Se cambia el estado del textView de las instrucciones:
                    runAnimationText(textInstructions,0,"out_in","Ingrese un volumen...");

                    volSelected = false;
                }


//                // Cuando no se ha seleccionado un nivel de urgencia se declara el volumen como seleccionado:
//                else if(urgSelected == false & running == false){
//                    volSelected = true;
//                }
            }
        });




        //---------------------- BOTONES: ---------------------------------------------------------



        // Botón que guarda todos los datos:
        b_savePosteior.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Extracción de fecha, hora, urgencia y volumen como un string:
                String date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date()); // Fecha en datos registrados
                String time = DateFormat.getTimeInstance().format(new Date());
                String nivelUrgencia = s_Urg.getSelectedItem().toString(); // String con el nivel de urgencia que se registra y también se muestra en reg. recientes.
                String volumen = n_Vol.getText().toString(); // String con el valor del volumen que se registra en el .CSV.
                Double volumeDouble = Double.valueOf(n_Vol.getText().toString());
                String volumenDisp = volumen + " mL"; // String con el valor del volumen que se muestra en registros recientes.


                // ---- Cálculo del flujo promedio: ----
                String flujoAvgString = "N/A";
                String flujoAvgStringDisp = "N/A";


                // Información de usuario:
                String userInfo = userInfo();


                // Comando que guarda la info en un archivo .CSV:
                // Idea tomada de: https://github.com/JohnsAndroidStudioTutorials/ReadandWritetoInternalStorage
                String dataToSave = date + "," + time + "," + "Vaciado," + "N/A" + "," + flujoAvgString + "," + nivelUrgencia + "," + volumen + "," + "N/A\n";
                String dataToSaveFirst = "Fecha:,Hora:,Tipo de registro:,Duracion [mm:ss]:,Flujo promedio [mL/s]:,Sensacion de urgencia:,Volumen [mL]:,Tipo de liquido ingerido:,"
                        + userInfo + dataToSave;
                FileOutputStream fos = null;
                File file = getFileStreamPath(FILE_NAME);


                // Agrega los nuevos datos al archivo .CSV:
                try {
                    if(file.exists()) {
                        fos = openFileOutput(FILE_NAME, MODE_APPEND);
                        fos.write(dataToSave.getBytes());
//                        Toast.makeText(getBaseContext(), "Registrado en " + getFilesDir() + "/" + FILE_NAME,
//                                Toast.LENGTH_SHORT).show();
                        uploadToCloud(dataToSaveFirst, dataToSave);

                        // Se cambia el estado del textView de las instrucciones:
                        runAnimationText(textInstructions,0,"out_in","Registro exitoso. Regrese al menú principal");

                    }

                    // Si no se han hecho registros antes, agrega las cabeceras de las columnas al .CSV:
                    else if(!file.exists()){
                        fos = openFileOutput(FILE_NAME, MODE_APPEND);
                        fos.write(dataToSaveFirst.getBytes());
//                        Toast.makeText(getBaseContext(), "Registrado en " + getFilesDir() + "/" + FILE_NAME,
//                                Toast.LENGTH_SHORT).show();
                        uploadToCloud(dataToSaveFirst, dataToSave);

                        // Se cambia el estado del textView de las instrucciones:
                        runAnimationText(textInstructions,0,"out_in","Registro exitoso. Regrese al menú principal.");
                    }

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




                // -- Se desactivan los módulos de guardar, urgenica y volumen hasta que se tome otro tiempo y se inicie otro registro: --
                // Urgencia
                ViewCompat.setBackgroundTintList(s_Urg, ContextCompat.getColorStateList(getApplicationContext(), R.color.save_button_gray));
                s_Urg.setEnabled(false);

                // Volumen
                ViewCompat.setBackgroundTintList(n_Vol, ContextCompat.getColorStateList(getApplicationContext(), R.color.save_button_gray));
                n_Vol.setEnabled(false);

                // Guardar
                ViewCompat.setBackgroundTintList(b_savePosteior, ContextCompat.getColorStateList(getApplicationContext(), R.color.save_button_gray));
                b_savePosteior.setEnabled(false);


                // Se activa el botón de regresar:
//                ViewCompat.setBackgroundTintList(b_export, ContextCompat.getColorStateList(getApplicationContext(), R.color.save_button_yellow));
                b_back.setEnabled(true);
                runAnimationButton(b_back, moduleDelay, "in");
                runAnimationText(labelBack, moduleDelay, "in", null);
                b_back.setVisibility(View.VISIBLE);
                labelBack.setVisibility(View.VISIBLE);

                // Se desactiva el botón de cancelar:
                b_cancel.setEnabled(true);
                runAnimationButton(b_cancel, moduleDelay, "out");
                b_cancel.setVisibility(View.INVISIBLE);
                runAnimationText(labelCancel, moduleDelay, "out", null);
                labelCancel.setVisibility(View.INVISIBLE);


                // Guarda el estado de la interfaz para cuando se vuelva a abrir la app:
                saveSharedPreferences();

                // Cambia al estado a "datos registrados":
                dataRegistered = true;


            }
        });




        // Botón que regresa a la welcome screen:
        b_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                backToWelcomeActivity();

            }
        });



        // Botón que cancela el registro en cualquier momento al mantenerlo oprimido:
        b_cancel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                backToWelcomeActivity();

                return true;
            }
        });




        // Método que carga el estado de la interfaz para cuando se vuelva a abrir la app:
        loadSharedPreferences();


    }





// -------- MÉTODOS POR FUERA DEL OnCreate: ----------------


    // ---- Información a cloud: ----
    // Método que sube los registros al cloud:
    private void uploadToCloud(String labelsDataToRegister, String dataToRegister){

        // Elimina los valores de lor registros y deja solo los labels, resultando en un arreglo de
        // los valores de los registros:
        String[] labelsDataString = labelsDataToRegister.replace(dataToRegister, "").split(",");
        // Arreglo de valores de los registros:
        String[] dataString = dataToRegister.split(",");

        HashMap<String, String> dataMap = new HashMap();
        SimpleDateFormat currentDate = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentDateString = currentDate.format(new Date());


        for(int i = 0; i < dataString.length; i++){

            labelsDataString[i] = labelsDataString[i].replaceAll("\n", "")
                    .replaceAll(":$", "")
                    .replaceAll("\\[", "(")
                    .replaceAll("\\]", ")")
                    .replaceAll("/", "_");

            // --- REVISAR QUÉ CADENAS DEBO REMOVER DEL STRING PARA QUE LA BASE DE DATOS LO ACEPTE ---

            dataString[i] = dataString[i].replaceAll("\n", "")
                    .replaceAll(":$", "")
                    .replaceAll("\\[", "(")
                    .replaceAll("\\]", ")")
                    .replaceAll("/", "_");

            dataMap.put(labelsDataString[i], dataString[i]);
            reference.child(currentDateString).setValue(dataMap);

        }
    }




    // ---- Info de usuario: ----
    // Método que carga la info del usuario en un string:
    private String userInfo(){

        String userInfo = null;
        String INFO_FILE_NAME = "Info_paciente.csv";
        FileInputStream fis = null;

        try {
            fis = openFileInput(INFO_FILE_NAME);
            InputStreamReader inputStreamReader = new InputStreamReader(fis);

            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();

            String lines;
            while ((lines = bufferedReader.readLine()) != null) {
                stringBuffer.append(lines + "\n");
            }

            userInfo = stringBuffer.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return userInfo;
    }





    //---- Animaciones: ----
    // Método que genera la animación con la que aparece el texto
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

    // Método que genera la animación con la que aparecen los botones:
    private void runAnimationButton(Button button, int delay, String type) {

        if(type == "in") {
            Animation a2 = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            a2.reset();
            a2.setStartOffset(delay);
            button.clearAnimation();
            button.setVisibility(View.VISIBLE);
            button.startAnimation(a2);
        }
        else if(type == "out"){
            Animation a2_2 = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
            a2_2.reset();
            a2_2.setStartOffset(delay);
            button.clearAnimation();
            button.startAnimation(a2_2);
            button.setVisibility(View.INVISIBLE);
        }
    }

    // Método que genera la animación con la que aparecen los spinners:
    private void runAnimationSpinner(Spinner spinner, int delay) {

        Animation a4 = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        a4.reset();
        a4.setStartOffset(delay);
        spinner.clearAnimation();
        spinner.setVisibility(View.VISIBLE);
        spinner.startAnimation(a4);
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

    // Método que genera la animación con la que aparecen los botones:
    private void runAnimationImageView(ImageView imageView, int delay, String type) {

        if(type == "in") {
            Animation a6 = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            a6.reset();
            a6.setStartOffset(delay);
            imageView.clearAnimation();
            imageView.setVisibility(View.VISIBLE);
            imageView.startAnimation(a6);
        }
        else if(type == "out"){
            Animation a6_2 = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
            a6_2.reset();
            a6_2.setStartOffset(delay);
            imageView.clearAnimation();
            imageView.startAnimation(a6_2);
            imageView.setVisibility(View.INVISIBLE);
        }
    }

    // Método que genera la animación con la que aparecen los botones:
    private void runAnimationView(View view, int delay, String type) {

        if(type == "in") {
            Animation a7 = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
            a7.reset();
            a7.setStartOffset(delay);
            view.clearAnimation();
            view.setVisibility(View.VISIBLE);
            view.startAnimation(a7);
        }
        else if(type == "out"){
            Animation a7_2 = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
            a7_2.reset();
            a7_2.setStartOffset(delay);
            view.clearAnimation();
            view.startAnimation(a7_2);
            view.setVisibility(View.INVISIBLE);
        }
    }


    // Método que envía al usuario a la Acticity de registro con cronómetro:
    public void backToWelcomeActivity() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
    }



    // ---- Método que guarda el estado de la interfaz para cuando se vuelva a abrir la app: ----
    protected void saveSharedPreferences(){

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

//        // Guarda shared preferences para fecha:
//        editor.putString("date1", dateMicString1);
//        editor.putString("date2", dateMicString2);
//        editor.putString("date3", dateMicString3);
//        editor.putString("date4", dateMicString4);
//        editor.putString("date5", dateMicString5);
//
//        // Guarda shared preferences para hora:
//        editor.putString("time1", timeMicString1);
//        editor.putString("time2", timeMicString2);
//        editor.putString("time3", timeMicString3);
//        editor.putString("time4", timeMicString4);
//        editor.putString("time5", timeMicString5);
//
//        // Guarda shared preferences para duración:
//        editor.putString("dur1", durMicString1);
//        editor.putString("dur2", durMicString2);
//        editor.putString("dur3", durMicString3);
//        editor.putString("dur4", durMicString4);
//        editor.putString("dur5", durMicString5);
//
//        // Guarda shared preferences para sensación de urgencia:
//        editor.putString("urg1", urgMicString1);
//        editor.putString("urg2", urgMicString2);
//        editor.putString("urg3", urgMicString3);
//        editor.putString("urg4", urgMicString4);
//        editor.putString("urg5", urgMicString5);
//
//        // Guarda shared preferences para volumen:
//        editor.putString("vol1", volMicString1);
//        editor.putString("vol2", volMicString2);
//        editor.putString("vol3", volMicString3);
//        editor.putString("vol4", volMicString4);
//        editor.putString("vol5", volMicString5);

        // Aplica cambios en el editor de shared preferences:
        editor.apply();

    }



    // ---- Método que carga el estado de la interfaz para cuando se vuelva a abrir la app: ----
    protected void loadSharedPreferences(){

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES,MODE_PRIVATE);

//        // Restaura el estado de los datos de fecha del vaciado que muestra la app:
//        dateMicString1 = sharedPreferences.getString("date1", null);
//        dateMicString2 = sharedPreferences.getString("date2", null);
//        dateMicString3 = sharedPreferences.getString("date3", null);
//        dateMicString4 = sharedPreferences.getString("date4", null);
//        dateMicString5 = sharedPreferences.getString("date5", null);
//
//        // Restaura el estado de los datos de hora del vaciado que muestra la app:
//        timeMicString1 = sharedPreferences.getString("time1", null);
//        timeMicString2 = sharedPreferences.getString("time2", null);
//        timeMicString3 = sharedPreferences.getString("time3", null);
//        timeMicString4 = sharedPreferences.getString("time4", null);
//        timeMicString5 = sharedPreferences.getString("time5", null);
//
//        // Restaura el estado de los datos de duración del vaciado que muestra la app:
//        durMicString1 = sharedPreferences.getString("dur1", null);
//        durMicString2 = sharedPreferences.getString("dur2", null);
//        durMicString3 = sharedPreferences.getString("dur3", null);
//        durMicString4 = sharedPreferences.getString("dur4", null);
//        durMicString5 = sharedPreferences.getString("dur5", null);
//
//        // Restaura el estado de los datos de urgencia del vaciado que muestra la app:
//        urgMicString1 = sharedPreferences.getString("urg1", null);
//        urgMicString2 = sharedPreferences.getString("urg2", null);
//        urgMicString3 = sharedPreferences.getString("urg3", null);
//        urgMicString4 = sharedPreferences.getString("urg4", null);
//        urgMicString5 = sharedPreferences.getString("urg5", null);
//
//        // Restaura el estado de los datos de volumen del vaciado que muestra la app:
//        volMicString1 = sharedPreferences.getString("vol1", null);
//        volMicString2 = sharedPreferences.getString("vol2", null);
//        volMicString3 = sharedPreferences.getString("vol3", null);
//        volMicString4 = sharedPreferences.getString("vol4", null);
//        volMicString5 = sharedPreferences.getString("vol5", null);
//
//
//        // ---- Valores en los TextView: ----
//
//        // Muestra en el TextView los datos de fecha del vaciado:
//        dateMic1.setText(dateMicString1);
//        dateMic2.setText(dateMicString2);
//        dateMic3.setText(dateMicString3);
//        dateMic4.setText(dateMicString4);
//        dateMic5.setText(dateMicString5);
//
//        // Muestra en el TextView los datos de hora del vaciado:
//        timeMic1.setText(timeMicString1);
//        timeMic2.setText(timeMicString2);
//        timeMic3.setText(timeMicString3);
//        timeMic4.setText(timeMicString4);
//        timeMic5.setText(timeMicString5);
//
//        // Muestra en el TextView los datos de duración del vaciado:
//        durMic1.setText(durMicString1);
//        durMic2.setText(durMicString2);
//        durMic3.setText(durMicString3);
//        durMic4.setText(durMicString4);
//        durMic5.setText(durMicString5);
//
//        // Muestra en el TextView los datos de sensación de urgencia del vaciado:
//        urgMic1.setText(urgMicString1);
//        urgMic2.setText(urgMicString2);
//        urgMic3.setText(urgMicString3);
//        urgMic4.setText(urgMicString4);
//        urgMic5.setText(urgMicString5);
//
//        // Muestra en el TextView los datos de volumen del vaciado:
//        volMic1.setText(volMicString1);
//        volMic2.setText(volMicString2);
//        volMic3.setText(volMicString3);
//        volMic4.setText(volMicString4);
//        volMic5.setText(volMicString5);

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








    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

//        // Guarda el estado de los datos de fecha que muestra la app:
//        outState.putString("date1", dateMicString1);
//        outState.putString("date2", dateMicString2);
//        outState.putString("date3", dateMicString3);
//        outState.putString("date4", dateMicString4);
//        outState.putString("date5", dateMicString5);
//        outState.putString("date6", dateMicString6);
//        outState.putString("date7", dateMicString7);
//
//        // Guarda el estado de los datos de hora que muestra la app:
//        outState.putString("time1", timeMicString1);
//        outState.putString("time2", timeMicString2);
//        outState.putString("time3", timeMicString3);
//        outState.putString("time4", timeMicString4);
//        outState.putString("time5", timeMicString5);
//        outState.putString("time6", timeMicString6);
//        outState.putString("time7", timeMicString7);
//
//        // Guarda el estado de los datos de duración del vaciado que muestra la app:
//        outState.putString("dur1", durMicString1);
//        outState.putString("dur2", durMicString2);
//        outState.putString("dur3", durMicString3);
//        outState.putString("dur4", durMicString4);
//        outState.putString("dur5", durMicString5);
//        outState.putString("dur6", durMicString6);
//        outState.putString("dur7", durMicString7);
//
//        // Guarda el estado de los datos de sensación de urgencia del vaciado que muestra la app:
//        outState.putString("urg1", urgMicString1);
//        outState.putString("urg2", urgMicString2);
//        outState.putString("urg3", urgMicString3);
//        outState.putString("urg4", urgMicString4);
//        outState.putString("urg5", urgMicString5);
//        outState.putString("urg6", urgMicString6);
//        outState.putString("urg7", urgMicString7);
//
//        // Guarda el estado de los datos de volumen del vaciado que muestra la app:
//        outState.putString("vol1", volMicString1);
//        outState.putString("vol2", volMicString2);
//        outState.putString("vol3", volMicString3);
//        outState.putString("vol4", volMicString4);
//        outState.putString("vol5", volMicString5);
//        outState.putString("vol6", volMicString6);
//        outState.putString("vol7", volMicString7);

    }



//// IGNORAR ESTO POR AHORA:
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//    }



    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

//        // Restaura el estado de los datos de fecha del vaciado que muestra la app:
//        dateMicString1 = savedInstanceState.getString("date1", null);
//        dateMicString2 = savedInstanceState.getString("date2", null);
//        dateMicString3 = savedInstanceState.getString("date3", null);
//        dateMicString4 = savedInstanceState.getString("date4", null);
//        dateMicString5 = savedInstanceState.getString("date5", null);
//
//        // Restaura el estado de los datos de hora del vaciado que muestra la app:
//        timeMicString1 = savedInstanceState.getString("time1", null);
//        timeMicString2 = savedInstanceState.getString("time2", null);
//        timeMicString3 = savedInstanceState.getString("time3", null);
//        timeMicString4 = savedInstanceState.getString("time4", null);
//        timeMicString5 = savedInstanceState.getString("time5", null);
//
//        // Restaura el estado de los datos de duración del vaciado que muestra la app:
//        durMicString1 = savedInstanceState.getString("dur1", null);
//        durMicString2 = savedInstanceState.getString("dur2", null);
//        durMicString3 = savedInstanceState.getString("dur3", null);
//        durMicString4 = savedInstanceState.getString("dur4", null);
//        durMicString5 = savedInstanceState.getString("dur5", null);
//
//        // Restaura el estado de los datos de urgencia del vaciado que muestra la app:
//        urgMicString1 = savedInstanceState.getString("urg1", null);
//        urgMicString2 = savedInstanceState.getString("urg2", null);
//        urgMicString3 = savedInstanceState.getString("urg3", null);
//        urgMicString4 = savedInstanceState.getString("urg4", null);
//        urgMicString5 = savedInstanceState.getString("urg5", null);
//
//        // Restaura el estado de los datos de volumen del vaciado que muestra la app:
//        volMicString1 = savedInstanceState.getString("vol1", null);
//        volMicString2 = savedInstanceState.getString("vol2", null);
//        volMicString3 = savedInstanceState.getString("vol3", null);
//        volMicString4 = savedInstanceState.getString("vol4", null);
//        volMicString5 = savedInstanceState.getString("vol5", null);
//
//
//        // ---- Valores en los TextView: ----
//
//        // Muestra en el TextView los datos de fecha del vaciado:
//        dateMic1.setText(dateMicString1);
//        dateMic2.setText(dateMicString2);
//        dateMic3.setText(dateMicString3);
//        dateMic4.setText(dateMicString4);
//        dateMic5.setText(dateMicString5);
//
//        // Muestra en el TextView los datos de hora del vaciado:
//        timeMic1.setText(timeMicString1);
//        timeMic2.setText(timeMicString2);
//        timeMic3.setText(timeMicString3);
//        timeMic4.setText(timeMicString4);
//        timeMic5.setText(timeMicString5);
//
//        // Muestra en el TextView los datos de duración del vaciado:
//        durMic1.setText(durMicString1);
//        durMic2.setText(durMicString2);
//        durMic3.setText(durMicString3);
//        durMic4.setText(durMicString4);
//        durMic5.setText(durMicString5);
//
//        // Muestra en el TextView los datos de sensación de urgencia del vaciado:
//        urgMic1.setText(urgMicString1);
//        urgMic2.setText(urgMicString2);
//        urgMic3.setText(urgMicString3);
//        urgMic4.setText(urgMicString4);
//        urgMic5.setText(urgMicString5);
//
//        // Muestra en el TextView los datos de volumen del vaciado:
//        volMic1.setText(volMicString1);
//        volMic2.setText(volMicString2);
//        volMic3.setText(volMicString3);
//        volMic4.setText(volMicString4);
//        volMic5.setText(volMicString5);

    }



    // Desactiva el boton de regresar para no perder registros:
    @Override
    public void onBackPressed() {
// super.onBackPressed();
// Not calling **super**, disables back button in current screen.
        Toast toast = Toast.makeText(getBaseContext(), "Mantenga el boton redondo para cancelar", Toast.LENGTH_SHORT);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 700);
    }



}