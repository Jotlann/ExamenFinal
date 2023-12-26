package com.example.examenfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Historial extends AppCompatActivity {

    private Button buttonBackL;
    private RecyclerView viewHistorial;
    private List<HistorialRutas> historialRutasList;
    private HistorialAdapter historialAdapter;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        viewHistorial = findViewById(R.id.viewHistorial);
        buttonBackL = findViewById(R.id.buttonBackL);

        // Inicializar RecyclerView
        viewHistorial = findViewById(R.id.viewHistorial);
        viewHistorial.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar Adaptador
        historialAdapter = new HistorialAdapter(new ArrayList<>());
        viewHistorial.setAdapter(historialAdapter);

        // Configurar referencia de la base de datos (reemplaza "tu_referencia_de_base_de_datos")
        databaseReference = FirebaseDatabase.getInstance().getReference("rutas");

        // Obtener datos desde la base de datos
        fetchDataFromDatabase();

        buttonBackL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Historial.this, Home.class);
                startActivity(intent);
            }
        });

        cargarDatosDesdeFirebase();
    }

    private void fetchDataFromDatabase() {
        // Adjuntar un ValueEventListener para recuperar datos de la base de datos en tiempo real
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<HistorialRutas> listaRutas = new ArrayList<>();

                // Iterar a través de dataSnapshot y llenar la lista
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    HistorialRutas ruta = dataSnapshot.getValue(HistorialRutas.class);
                    listaRutas.add(ruta);
                }

                // Actualizar el adaptador con los nuevos datos
                historialAdapter.setRutas(listaRutas);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores si es necesario
                Toast.makeText(Historial.this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void cargarDatosDesdeFirebase() {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference referencia = database.getReference("rutas");

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference userReference = referencia.child(userId);

            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    historialRutasList.clear();

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String nombre = dataSnapshot.child("nombre").getValue(String.class);
                        String fecha = dataSnapshot.child("fecha").getValue(String.class);
                        String coordIniciales = dataSnapshot.child("coordenadasIniciales").getValue(String.class);
                        String coordFinales = dataSnapshot.child("coordenadasFinales").getValue(String.class);
                        long tiempoContador = dataSnapshot.child("tiempoContadorSegundos").getValue(Long.class);
                        float distanciaTotal = dataSnapshot.child("distanciaTotal").getValue(Float.class);

                        HistorialRutas historialRuta = new HistorialRutas(nombre, fecha, coordIniciales, coordFinales, tiempoContador, distanciaTotal);
                        historialRutasList.add(historialRuta);
                    }

                    historialAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Historial.this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
                }
            });
    }
}
