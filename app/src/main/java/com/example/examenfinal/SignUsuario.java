package com.example.examenfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUsuario extends AppCompatActivity {

    private FirebaseAuth Auth;
    EditText editEmailR, editPassR, editPassCR;
    Button buttonSignR, buttonBackR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        Auth = FirebaseAuth.getInstance();
        buttonBackR = findViewById(R.id.buttonBackR);
        editEmailR = findViewById(R.id.editEmailR);
        editPassR = findViewById(R.id.editPassR);
        editPassCR = findViewById(R.id.editPassCR);
        buttonSignR = findViewById(R.id.buttonSignR);

        buttonSignR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = editEmailR.getText().toString();
                String pass = editPassR.getText().toString();
                String passrc = editPassCR.getText().toString();

                if (email.equals("") || pass.equals("") || passrc.equals("")) {
                    Toast.makeText(SignUsuario.this, "Todos los campos deben ser rellenados", Toast.LENGTH_SHORT).show();
                } else if (pass.length() >= 8) {
                    if (pass.equals(passrc)) {

                        Auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUsuario.this, "Creo su cuenta con exito.", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(SignUsuario.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignUsuario.this, "La contraseña no puede tener menos de 6 caracteres", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonBackR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUsuario.this, MainLogin.class);
                startActivity(intent);
            }
        });
    }
}