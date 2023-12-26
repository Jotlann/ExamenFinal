package com.example.examenfinal;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainLogin extends AppCompatActivity {

    private FirebaseAuth auth;
    TextView txtRegisterM;
    EditText editEmailM, editPassM;
    Button buttonLoginM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        txtRegisterM = findViewById(R.id.txtRegisterM);
        editEmailM = findViewById(R.id.editEmailM);
        editPassM = findViewById(R.id.editPassM);
        buttonLoginM = findViewById(R.id.buttonLoginM);

        buttonLoginM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmailM.getText().toString();
                String pass = editPassM.getText().toString();

                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (!pass.isEmpty()) {
                        auth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Toast.makeText(MainLogin.this, "Bienvenido.", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(MainLogin.this, Home.class));
                                finish();
                            }
                        });
                    } else {
                        Toast.makeText(MainLogin.this, "Coloque su contraseña.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


        txtRegisterM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainLogin.this, SignUsuario.class);
                startActivity(intent);
            }
        });

        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            // Si el usuario está autenticado, cargar sus datos
            startActivity(new Intent(MainLogin.this, Home.class));
        }

    }
}