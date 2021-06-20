package com.example.diariomiccional_pd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

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

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ExportVisualizeActivity extends AppCompatActivity {

    // ---- Declaración de variables: ----

    // Declaración del string con el nombre que se le dará al archivo donde se guardan los
    // datos, que será el mismo para el archivo exportado:
    private String dateFileName = new SimpleDateFormat("yyyy_MMM", Locale.getDefault()).format(new Date()); // Fecha para el nombre del .CS
    private String FILE_NAME = "Diario_miccional.csv";

    // Declaración del arreglo que tendrá todos los registros del .CSV:
    private ArrayList<ArrayList<String>> registros;

    // Declaración de los TextViews del layout en la clase:
    private TextView textInstructions;

    // Declaración de los graphs del layout en la clase:
    private GraphView graph;

    // Declaración de la serie de volumen vs. fecha-hora que se graficará:
    private LineGraphSeries<DataPoint> seriesMiccion = new LineGraphSeries<>();
    private LineGraphSeries<DataPoint> seriesIngesta = new LineGraphSeries<>();

    // Declaración de los doubles con las series de datos que se graficarán como eje X y eje Y:
    private Double x;
    private Double y;

    // Declaración del formato de fecha:
    private Date fechaX;
    private String fechaXDisplay;

    // Declaración del formato de fecha:
    private SimpleDateFormat formatoLectura = new SimpleDateFormat("dd MMM yyyy, hh:mm:ss aa");
    private SimpleDateFormat formatoDisplay = new SimpleDateFormat("dd MMM\nyyyy");

    // Declaración de los TextView donde se muestran los detalles del registro seleccionado:
    private TextView text_date;
    private TextView text_time;
    private TextView text_typeReg;
    private TextView text_dur;
    private TextView text_flow;
    private TextView text_urg;
    private TextView text_vol;
    private TextView text_typeLiq;

    // Declaración de los TextView donde se muestran los labels de los detalles del registro seleccionado:
    private TextView text_dateLabel;
    private TextView text_timeLabel;
    private TextView text_typeRegLabel;
    private TextView text_durLabel;
    private TextView text_flowLabel;
    private TextView text_urgLabel;
    private TextView text_volLabel;
    private TextView text_typeLiqLabel;

    // Declara los botones:
    private Button b_export;
    private Button b_back;

    // Declaración de los imageView:
    private ImageView exportIcon;

    // Declaración del Request Code para exportar el .CSV:
    private static final int REQUEST_CODE = 1;

    // Inicializzación de la variable en la que se guarda la información del archivo a exportar:
    private String DATA_TO_EXPORT;

    // Declaración del int con el delay que tendrán todos los botones:
    private static final int buttonDelay = 600;





    // ---------- MÉTODO ON CREATE EN DONDE ESTAN TODOS LOS PROCESOS MANEJADOS POR EL USUARIO: ---------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_visualize);


        // Esconde la navigation bar en esta activity de visualización para no distorsionar la UI en
        // pantallas pequeñas:
        fullScreenCall();

        // Inicialiación de los TextViews del layout con variables de la clase;
        text_date = findViewById(R.id.text_dateMic_export);
        text_time = findViewById(R.id.text_timeMic_export);
        text_typeReg = findViewById(R.id.text_typeMic_export);
        text_dur = findViewById(R.id.text_durMic_export);
        text_flow = findViewById(R.id.text_flujoMic_export);
        text_urg = findViewById(R.id.text_urgMic_export);
        text_vol = findViewById(R.id.text_volMic_export);
        text_typeLiq = findViewById(R.id.text_typeLiqMic_export);


        // Inicialiación de los TextViews del layout con los labels variables de la clase;
        text_dateLabel = findViewById(R.id.text_dateMicLabel_export);
        text_timeLabel = findViewById(R.id.text_timeMicLabel_export);
        text_typeRegLabel = findViewById(R.id.text_typeMicLabel_export);
        text_durLabel = findViewById(R.id.text_durMicLabel_export);
        text_flowLabel = findViewById(R.id.text_flujoMicLabel_export);
        text_urgLabel = findViewById(R.id.text_urgMicLabel_export);
        text_volLabel = findViewById(R.id.text_volMicLabel_export);
        text_typeLiqLabel = findViewById(R.id.text_typeLiqMicLabel_export);


        // Inicialización de los botones:
        b_export = findViewById(R.id.button_exportData_export);
        b_back = findViewById(R.id.button_back_export);


        // Inicialización de los imageView:
        exportIcon = findViewById(R.id.imageView_exportIcon_export);


        // - Animaciones de entrada de los botones: -
        runAnimationButton(b_export, buttonDelay, "in"); // Exportación
        runAnimationImageView(exportIcon,  buttonDelay + buttonDelay/2); // Exportación (ícono)
        runAnimationButton(b_back, buttonDelay, "in"); // Regreso a Home


        // Inicialiación de los Graphs del layout con variables de la clase;
        graph = findViewById(R.id.graphView_vol_vs_time);


        // Animación de entrada del graphView;
        runAnimationGraphView(graph, 2 * buttonDelay);


        // Obtención de un arreglo con todos los registros del .CSV
        registros = dataArrayMaker(FILE_NAME);


        // Animaciones de entrada de los textView con los labels de los detalles:
        runAnimationText(text_dateLabel, 2 * buttonDelay + buttonDelay/2, "in", null);
        runAnimationText(text_timeLabel, 2 * buttonDelay + buttonDelay/2, "in", null);
        runAnimationText(text_typeRegLabel, 2 * buttonDelay + buttonDelay/2, "in", null);
        runAnimationText(text_durLabel, 2 * buttonDelay + buttonDelay/2, "in", null);
        runAnimationText(text_flowLabel, 2 * buttonDelay + buttonDelay/2, "in", null);
        runAnimationText(text_urgLabel, 2 * buttonDelay + buttonDelay/2, "in", null);
        runAnimationText(text_volLabel, 2 * buttonDelay + buttonDelay/2, "in", null);
        runAnimationText(text_typeLiqLabel, 2 * buttonDelay + buttonDelay/2, "in", null);

//
//        prueba.setText(registros.get(6).get(1));


        // --- Grafica los registros de volumen vs los de tiempo(fecha): ---
        graphData(registros, formatoLectura, formatoDisplay, graph);


        // Inicialización del TextView con las instrucciones del layout en la clase:
        textInstructions = findViewById(R.id.text_Instructions_export);
        textInstructions.setText("Utilice el botón para exportar sus registros..."); // Configura la primera instrucción en el TextEdit de instrucciones:
        runAnimationText(textInstructions,300,"in",null); // Animación de entrada.
        textAnimationLoop(textInstructions, 6000);




        //---------------------- BOTONES: ---------------------------------------------------------


        // --- Botón que permite exportar los datos: ---
        b_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // ---- EXPORTACIÓN DE DATOS: ----
                String dateTimeExport = new SimpleDateFormat("yyyy_MMM_dd_HHmmss", Locale.getDefault()).format(new Date());
                FileInputStream fis = null;
                File file = getFileStreamPath(FILE_NAME);
                String FILE_NAME_EXPORT = "Diario_" + dateTimeExport + ".csv";

                // Lee el .CSV si este existe (ESTO ES PARA CREAR EL ARCHIVO QUE SE EXPORTA):
                if(file.exists()) {

                    try {
                        fis = openFileInput(FILE_NAME);
                        InputStreamReader inputStreamReader = new InputStreamReader(fis);

                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        StringBuffer stringBuffer = new StringBuffer();

                        String lines;
                        while ((lines = bufferedReader.readLine()) != null) {
                            stringBuffer.append(lines + "\n");
                        }

                        DATA_TO_EXPORT = stringBuffer.toString();
//                        displayText.setText(stringBuffer.toString());
//                        urgMic7.setText(stringBuffer.toString());

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    // Método que comparte el .CSV creado:
                    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("application/csv"); //not needed, but maybe useful
                    intent.putExtra(Intent.EXTRA_TITLE, FILE_NAME_EXPORT); //not needed, but maybe useful
                    startActivityForResult(intent, REQUEST_CODE);
                }

                // Si no existen registros en la app, le pide al usuario hacer uno antes de exportar:
                else if(!file.exists()){
                    Toast.makeText(getBaseContext(), "Registre algún dato primero para exportar.", Toast.LENGTH_LONG).show();
                }
            }
        });




        // Botón que exporta todos los datos a el almacenamiento compartido:
        b_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                backToWelcomeActivity();
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




    // Método que crea el arreglo de strings con los registros que se lee del .CS:
    // Tomado de: https://stackoverflow.com/questions/5360628/get-and-parse-csv-file-in-android
    private ArrayList<ArrayList<String>> dataArrayMaker(String fileName){

        ArrayList<String> fecha = new ArrayList();
        ArrayList<String> hora = new ArrayList();
        ArrayList<String> tipoReg = new ArrayList();
        ArrayList<String> duracion = new ArrayList();
        ArrayList<String> avgFlow = new ArrayList();
        ArrayList<String> urgencia = new ArrayList();
        ArrayList<String> volumen = new ArrayList();
        ArrayList<String> tipoLiq = new ArrayList();

        FileInputStream fis = null;
        File file = getFileStreamPath(fileName);
        ArrayList<ArrayList<String>> dataArray = new ArrayList<ArrayList<String>>(7);

        if(file.exists()) {

            try {
                fis = openFileInput(fileName);
                InputStreamReader inputStreamReader = new InputStreamReader(fis);

                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuffer stringBuffer = new StringBuffer();

                try {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String [] RowData = line.split(",");

                        // do something with "data" and "value"
                        fecha.add(RowData[0]);
                        hora.add(RowData[1]);
                        tipoReg.add(RowData[2]);
                        duracion.add(RowData[3]);
                        avgFlow.add(RowData[4]);
                        urgencia.add(RowData[5]);
                        volumen.add(RowData[6]);
                        tipoLiq.add(RowData[7]);
                    }

                    dataArray.add(fecha);
                    dataArray.add(hora);
                    dataArray.add(tipoReg);
                    dataArray.add(duracion);
                    dataArray.add(avgFlow);
                    dataArray.add(urgencia);
                    dataArray.add(volumen);
                    dataArray.add(tipoLiq);


                } catch (IOException ex) {
                    // handle exception
                } finally {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        // handle exception
                    }
                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return dataArray;

    }




    // Método que grafica los datos de los registros (volumen vs. tiempo):
    private void graphData(ArrayList<ArrayList<String>> dataArray, SimpleDateFormat readFormat,
                           SimpleDateFormat graphFormat, GraphView graphView){

        // Formatea el eje x de la gráfica para que sean fechas y horas:
        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if(isValueX) {
                    return graphFormat.format(new Date((long) value));
                }
                else {
                    return super.formatLabel(value, isValueX);
                }
            }
        });

        // Genera las coordenadas y los puntos de cada dato en una serie de datos:
        for(int i = 1; i < dataArray.get(0).size(); i++){

            String fechaSerie = dataArray.get(0).get(i) + ", " + dataArray.get(1).get(i);
            String volumenSerieString = dataArray.get(6).get(i);
            Double volumenSerie = Double.parseDouble(volumenSerieString);

            try {
                fechaX = readFormat.parse(fechaSerie);
                fechaXDisplay = graphFormat.format(fechaX);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            DataPoint datapoint = new DataPoint(fechaX, volumenSerie);

            // Leva cada tipo de registro a su serie correspondiente:
            if(dataArray.get(2).get(i).equals("Ingesta liquido")) {
                seriesIngesta.appendData(datapoint, true, dataArray.size());
            }
            else if(dataArray.get(2).get(i).equals("Vaciado")){
                seriesMiccion.appendData(datapoint, true, dataArray.size());
            }
        }



        // Genera la gráfica con las series de datos creadas:
        if(seriesIngesta.isEmpty()) {
            graphView.addSeries(seriesMiccion);
        }
        else if(seriesMiccion.isEmpty()) {
            graphView.addSeries(seriesIngesta);
        }
        else{
            graphView.addSeries(seriesMiccion);
            graphView.addSeries(seriesIngesta);
        }

//        // Añade título a la gráfica:
//        graphView.setTitle("Resumen de sus registros");

        // Cuadra títulos a los ejes de la gráfica:
        GridLabelRenderer gridLabelRenderer = graphView.getGridLabelRenderer();
        gridLabelRenderer.setPadding(50);
        gridLabelRenderer.setVerticalAxisTitle("Volumen [mL]");
        graphView.getGridLabelRenderer().setHighlightZeroLines(true);

        // Añade leyendas para cada serie:
        seriesIngesta.setTitle("Ingesta");
        seriesMiccion.setTitle("Miccion");
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setTextSize(30);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.valueOf("TOP")); // Alinea las leyendas arriba

        // Permite que la gráfica sea escalable y scrolleable:
        graphView.getViewport().setScalable(true);  // activate horizontal zooming and scrolling
        graphView.getViewport().setScrollable(true);  // activate horizontal scrolling
        graphView.getViewport().setScalableY(true);  // activate horizontal and vertical zooming and scrolling
        graphView.getViewport().setScrollableY(true);  // activate vertical scrolling

//        // Ajusta el eje x de la grafica a los datos:
//        graphView.getViewport().setMaxY(series.getHighestValueX());
//        graphView.getViewport().setXAxisBoundsManual(true);

        // Fija un número de labels horizontales:
        graphView.getGridLabelRenderer().setNumHorizontalLabels(3);


        // Añade color y marcas que señalizan cada uno de los puntos graficados para ingesta:
        seriesIngesta.setDrawDataPoints(true);
        seriesIngesta.setDataPointsRadius(15);
        seriesIngesta.setThickness(12);
        seriesIngesta.setColor(getResources().getColor(android.R.color.holo_blue_dark));

        // Añade color y marcas que señalizan cada uno de los puntos graficados para micción:
        seriesMiccion.setDrawDataPoints(true);
        seriesMiccion.setDataPointsRadius(15);
        seriesMiccion.setThickness(12);
        seriesMiccion.setColor(getResources().getColor(android.R.color.holo_orange_dark));

        // Permite ver el valor del dato que se esta tocando:
        dataDetailsTouch(seriesIngesta, dataArray, readFormat);
        dataDetailsTouch(seriesMiccion, dataArray, readFormat);


    }



    // Método que muestra los detalles del dato señalado:
    private void dataDetailsTouch(LineGraphSeries<DataPoint> series,
                                      ArrayList<ArrayList<String>> dataArray, SimpleDateFormat readFormat) {
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {

//                // Valor en Y del datapoint:
//                double pointY = dataPoint.getY();

                // Valor en X del datapoint en un formato legible de fecha:
                Date dateFromDataPoint = new java.sql.Date((long) dataPoint.getX());

                // Posición del datapoint en la serie:
                String dateFromRegisterString = null;
                Date dateFromRegisterDate = null;
                int dataIndex = 0;

                // Recorre el arreglo de registros hasta encontrar la fecha buscada y extrae la posición en el arreglo:
                for (int i = 1; i < dataArray.get(0).size(); i++) {

                    dateFromRegisterString = dataArray.get(0).get(i) + ", " + dataArray.get(1).get(i);

                    try {
                        dateFromRegisterDate = readFormat.parse(dateFromRegisterString);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (dateFromDataPoint.compareTo(dateFromRegisterDate) == 0) {
                        dataIndex = i;
                    }
                }

                // Variables con los detalles del registro tocado:
                String fechaTouch = formatoDisplay.format(dateFromDataPoint);
                String horaTouch = dataArray.get(1).get(dataIndex);
                String tipoRegTouch = dataArray.get(2).get(dataIndex);
                String duracionTouch = dataArray.get(3).get(dataIndex);
                String flujoAvgTouch = dataArray.get(4).get(dataIndex);
                String urgenciaTouch = dataArray.get(5).get(dataIndex);
                String volumenTouch = dataArray.get(6).get(dataIndex);
                String tipoLiqTouch = dataArray.get(7).get(dataIndex);

                // Agrega unidades a l volumen y al flujo avg cuando si aplican:
                if (flujoAvgTouch.compareTo("N/A") != 0) {
                    flujoAvgTouch = flujoAvgTouch + "\nmL/s";
                }
                if (volumenTouch.compareTo("N/A") != 0) {
                    volumenTouch = volumenTouch + "\nmL";
                }


                // Divide los detalles de hora en dos líneas:
                if(horaTouch.contains("PM")) {
                    horaTouch = horaTouch.replace("PM", "\nPM");
                }
                else if(horaTouch.contains("AM")){
                    horaTouch = horaTouch.replace("AM", "\nAM");
                }


                // Divide los de talles de fecha en dos líneas:


//                // Marca con color el tipo de registro seleccionado en los detalles:
//                if (tipoRegTouch.compareTo("Vaciado") != 0) {
//                    text_typeReg.setBackgroundResource(R.drawable.rectangle_gray);
//                }



                // Se muestran los detalles en la tabla de detalles:
                text_date.setText(fechaTouch);
                text_time.setText(horaTouch);
                text_typeReg.setText(tipoRegTouch);
                text_dur.setText(duracionTouch);
                text_flow.setText(flujoAvgTouch);
                text_urg.setText(urgenciaTouch);
                text_vol.setText(volumenTouch);
                text_typeLiq.setText(tipoLiqTouch);

            }
        });
    }



    // Método que envía al usuario a la Acticity de registro con cronómetro:
    public void backToWelcomeActivity() {
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
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



    // Método que genera la animación con la que aparece el texto
    private void runAnimationImageView(ImageView imageView, int delay) {

        Animation a2 = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        a2.reset();
        a2.setStartOffset(delay);
        imageView.clearAnimation();
        imageView.startAnimation(a2);
    }


    // Método que genera la animación con la que aparecen los botones:
    private void runAnimationButton(Button button, int delay, String type) {

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
            button.clearAnimation();
            button.startAnimation(a3_2);
            button.setVisibility(View.INVISIBLE);
        }
    }



    // Método que genera la animación con la que aparece el graphView:
    private void runAnimationGraphView(GraphView graphView, int delay) {

        Animation a4 = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        a4.reset();
        a4.setStartOffset(delay);
        graphView.clearAnimation();
        graphView.startAnimation(a4);
    }
    



    // Método que hace un loop con las animaciones para las instrucciones:
    private void textAnimationLoop(TextView text, int delay){


        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Actions to do after 10 seconds
                runAnimationText(text, 0, "out_in", "Toque algún registro en la gráfica para ver sus detalles...");
            }
        }, delay);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Actions to do after 10 seconds
                runAnimationText(text, 0, "out_in", "Puede Hacer zoom-in a la gráfica con los dedos...");
            }
        }, 2 * delay);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Actions to do after 10 seconds
                runAnimationText(text, 0, "out_in", "Utilice el botón para exportar sus registros...");
                textAnimationLoop(text, delay);
            }
        }, 3* delay);
    }



    // Método que evita cambios en el tamaño de fuente de la aplicación generados por
    // la configuración de tamaño de fuente del celular:
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        Configuration config = new Configuration(newBase.getResources().getConfiguration());
        config.fontScale = 1.0f;
        applyOverrideConfiguration(config);
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