package com.example.examenfinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class Home extends AppCompatActivity {

    Button buttonBackH, buttonRegistroH, buttonHistorialH, buttonMapaH, buttonOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        buttonBackH = findViewById(R.id.buttonBackH);
        buttonRegistroH = findViewById(R.id.buttonRegistroH);
        buttonHistorialH = findViewById(R.id.buttonHistorialH);
        buttonMapaH = findViewById(R.id.buttonMapaH);
        buttonOut = findViewById(R.id.buttonOut);

        buttonBackH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, MainLogin.class);
                startActivity(intent);
            }
        });

        buttonRegistroH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, SignRutas.class);
                startActivity(intent);
            }
        });

        buttonHistorialH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Historial.class);
                startActivity(intent);
            }
        });

        buttonMapaH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, Mapa.class);
                startActivity(intent);
            }
        });

        buttonOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(Home.this, MainLogin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}