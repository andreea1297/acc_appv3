package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


//pagina 1
public class MainActivity extends AppCompatActivity  {

    private Button deschide_pagina;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        deschide_pagina = (Button) findViewById(R.id.pagina_noua);
        deschide_pagina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_new_activity();
            }
        });
    }

    public void open_new_activity(){
        Intent intent = new Intent(this, Activity_main.class);
        startActivity(intent);

    }

}