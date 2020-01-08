package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class Activity_main extends AppCompatActivity {

    private Button deschide_pagina;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        deschide_pagina = (Button) findViewById(R.id.pagina_noua);
        deschide_pagina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_new_activity();
            }
        });
    }

        public void open_new_activity(){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        }
}
