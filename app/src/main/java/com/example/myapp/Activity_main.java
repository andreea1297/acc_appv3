package com.example.myapp;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class Activity_main extends AppCompatActivity implements SensorEventListener {

    private static List<Float> x ;
    private static List<Float> y;
    private static List<Float> z;
    private int nr = 10;
    private SensorManager SM;
    private Sensor mySensor;

    private Button deschide_pagina;
    private Button start_button;
    private Button stop_button;

    private TextView textView_rezultat;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        x = new ArrayList<Float>();
        y = new ArrayList<Float>();
        z = new ArrayList<Float>();

        SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
       // SM.registerListener(this,mySensor,SensorManager.SENSOR_DELAY_NORMAL); //3 secunde



        setContentView(R.layout.activity_main2);
        deschide_pagina = (Button) findViewById(R.id.pagina_noua);
        deschide_pagina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_new_activity();
            }
        });

        start_button = (Button) findViewById(R.id.start_button);
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResume();

            }
        });


        stop_button = (Button) findViewById(R.id.stop_button);
        stop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPause();

            }
        });



        //citesc datele de la accelerometru


    }

        public void open_new_activity(){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //adaug datele accelerometrului intr-o lista

        float _x = event.values[0];
        float _y = event.values[1];
        float _z = event.values[2];


        x.add(_x);
        y.add(_y);
        z.add(_z);

        Log.e("x", String.valueOf(_x));
        Log.e("y", String.valueOf(_y));
        Log.e("z", String.valueOf(_z));

        prediction();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    protected void onPause(){
        super.onPause();
        SM.unregisterListener(this);
    }

    protected void onResume(){
        super.onResume();
        SM.registerListener(this,mySensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    //fac predictiile
    private void prediction() {


        ArrayList<Float> data_all = new ArrayList<>();

        if (x.size() == nr && y.size() == nr && z.size() == nr) {

            float mediaX = medie_aritmetica(x);
            float mediaY = medie_aritmetica(y);
            float mediaZ = medie_aritmetica(z);

            //creez o lista cu 3 elemente
            data_all.add(medie_aritmetica(x));
            data_all.add(medie_aritmetica(y));
            data_all.add(medie_aritmetica(z));

            x.clear();
            y.clear();
            z.clear();

            Module network = null;
            //Bitmap bitmap = null;
            //Bitmap bitmap = Bitmap.createBitmap(3, 1, Bitmap.Config.ARGB_8888);



            try {
                //loading the module
                network = Module.load(assetFilePath(
                        this,
                        "model_v7_final.pt"));



            } catch (IOException e) {
                Log.e("PytorchError", "Error reading assets", e);
                finish();
            }

            Tensor input = Tensor.fromBlob(new float[]{mediaX,mediaY,mediaZ},new long[]{1,3});
            IValue output = network.forward(IValue.from(input));
            float[] scores = output.toTensor().getDataAsFloatArray();

            //calling the forward method of the model to run our input
            //network.forward(IValue.listFrom(mediaX,mediaY,mediaZ));
//            final float[] rezultat = output.getDataAsFloatArray();

            //indexul cu valoarea cea mai mare
            float max_score = -Float.MAX_VALUE;
            int ms_ix = -1;
            for (int i = 0; i < scores.length; i++) {

                if (scores[i] > max_score) {

                    max_score = scores[i];
                    ms_ix = i;
                }
            }

            String clasa_detectata = ModelClasses.MODEL_CLASSES[ms_ix];

            //afisare  clasa detectata-> predictie
            textView_rezultat = (TextView) findViewById(R.id.textView_rezultat);
            textView_rezultat.setText(clasa_detectata);

        }
    }



    //calculeaza media aritmetica pe fiecare linie
    public float medie_aritmetica (List < Float > lista) {
        float sum = 0;
        for (int i = 0; i < lista.size(); i++) {
            sum += lista.get(i);
        }

        if(lista.size() == 0){ return 0;}
        else
        {
            return sum / (lista.size()+1);
        }
    }

    //calea fisierului .pt
    public static String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }
}
