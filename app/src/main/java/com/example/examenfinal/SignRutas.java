    package com.example.examenfinal;

    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.app.ActivityCompat;
    import androidx.core.content.ContextCompat;

    import android.content.Intent;
    import android.content.pm.PackageManager;
    import android.location.Location;
    import android.os.Bundle;
    import android.os.CountDownTimer;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;
    import android.widget.Chronometer;
    import android.widget.EditText;
    import android.widget.TextView;
    import android.widget.Toast;

    import com.google.android.gms.location.FusedLocationProviderClient;
    import com.google.android.gms.location.LocationServices;
    import com.google.android.gms.tasks.OnSuccessListener;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;

    import java.text.SimpleDateFormat;
    import java.util.Date;
    import java.util.Locale;
    import java.util.concurrent.TimeUnit;


    public class SignRutas extends AppCompatActivity {

        private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
        private static final long TIEMPO_TOTAL_MILLIS = 600000;
        private long tiempoRestanteMillis = TIEMPO_TOTAL_MILLIS;
        private long tiempoContador = 0;
        private CountDownTimer countDownTimer;
        private FusedLocationProviderClient fusedLocationClient;
        private String coordenadasIniciales = "";
        private String coordenadasFinales = "";
        private Chronometer chronometerDuracionRuta;
        private Location lastLocation;
        private float totalDistance = 0;
        Button buttonBackT, buttonOn, buttonOff;
        EditText editNameRuta;
        TextView viewFechaRuta, viewDistanciaRuta, viewInicioRuta, viewFinRuta;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_registro);

            buttonOn = findViewById(R.id.buttonOn);
            buttonOff = findViewById(R.id.buttonOff);
            buttonBackT = findViewById(R.id.buttonBackT);
            chronometerDuracionRuta = findViewById(R.id.chronometerDuracionRuta);
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            editNameRuta = findViewById(R.id.editNameRuta);
            viewFechaRuta = findViewById(R.id.viewFechaRuta);
            viewDistanciaRuta = findViewById(R.id.viewDistanciaRuta);
            viewInicioRuta = findViewById(R.id.viewInicioRuta);
            viewFinRuta = findViewById(R.id.viewFinRuta);

            buttonBackT.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SignRutas.this, Home.class);
                    startActivity(intent);
                }
            });

            buttonOn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(SignRutas.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (countDownTimer == null) {
                            startContador();
                            obtenerUbicacionActual();
                        } else {
                            // Reiniciar el cronómetro si ya está en ejecución
                            stopContador();
                        }
                    } else {
                        ActivityCompat.requestPermissions(SignRutas.this,
                                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                LOCATION_PERMISSION_REQUEST_CODE);
                    }
                }
            });

            buttonOff.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mostrarUltimasCoordenadasEnViewText();
                    viewDistanciaRuta.setText(String.format(Locale.getDefault(), "%.2f m", totalDistance));
                    stopContador();
                    viewFinRuta.setText(coordenadasFinales);
                    guardarDatosEnFirebase();
                }
            });

        }

        private void startContador() {
            countDownTimer = new CountDownTimer(tiempoRestanteMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    tiempoRestanteMillis = millisUntilFinished;
                    tiempoContador = (TIEMPO_TOTAL_MILLIS - millisUntilFinished) / 1000;
                    updateContador();
                }

                @Override
                public void onFinish() {
                    tiempoRestanteMillis = 0;
                    tiempoContador = TIEMPO_TOTAL_MILLIS / 1000;
                    updateContador();
                    mostrarUltimasCoordenadasEnViewText();
                    guardarDatosEnFirebase();
                    countDownTimer = null;
                }
            }.start();
        }

        private void updateContador() {
            int minutos = (int) ((TIEMPO_TOTAL_MILLIS - tiempoRestanteMillis) / 1000) / 60;
            int segundos = (int) ((TIEMPO_TOTAL_MILLIS - tiempoRestanteMillis) / 1000) % 60;
            String tiempoTranscurrido = String.format(Locale.getDefault(), "%02d:%02d", minutos, segundos);
            chronometerDuracionRuta.setText(tiempoTranscurrido);
        }

        private void stopContador() {
            if (countDownTimer != null) {
                countDownTimer.cancel();
                tiempoRestanteMillis = TIEMPO_TOTAL_MILLIS;
                updateContador();
            }
        }

        public String obtenerTiempoFormateadoPublic(long millis) {
            return obtenerTiempoFormateado(millis);
        }

        private String obtenerTiempoFormateado(long millis) {
            long minutos = TimeUnit.MILLISECONDS.toMinutes(millis);
            long segundos = TimeUnit.MILLISECONDS.toSeconds(millis)
                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));

            return String.format(Locale.getDefault(), "%02d:%02d", minutos, segundos);
        }

        private void mostrarUltimasCoordenadasEnViewText() {
            String ultimasCoordenadas = obtenerUltimasCoordenadas();
            Log.d("TAG", "Ultimas coordenadas: " + ultimasCoordenadas);
            updateContador();
        }

        private void obtenerUbicacionActual() {
            Log.d("TAG", "obtenerUbicacionActual() llamado");
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            } else {
                fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            if (lastLocation != null) {
                                // Calcular distancia entre ubicaciones sucesivas
                                float distance = lastLocation.distanceTo(location);
                                totalDistance += distance;
                                viewDistanciaRuta.setText(String.format(Locale.getDefault(), "Distancia: %.2f m", totalDistance));
                            }
                            coordenadasIniciales = location.getLatitude() + ", " + location.getLongitude();
                            lastLocation = location;

                            String fechaActual = obtenerFechaActual();

                            viewInicioRuta.setText(coordenadasIniciales);
                            viewFechaRuta.setText(fechaActual);
                            // Actualizar las coordenadas finales en cada actualización de ubicación
                            coordenadasFinales = location.getLatitude() + ", " + location.getLongitude();
                        } else {
                            Toast.makeText(SignRutas.this, "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

        private String obtenerFechaActual() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = new Date();
            return dateFormat.format(date);
        }

        private void guardarDatosEnFirebase() {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference referencia = database.getReference("rutas");

            String nombreRuta = editNameRuta.getText().toString();
            String fechaRuta = viewFechaRuta.getText().toString();

            long tiempoContadorSegundos = tiempoContador;
            HistorialRutas nuevaRuta = new HistorialRutas(nombreRuta, fechaRuta, coordenadasIniciales, coordenadasFinales, tiempoContadorSegundos, totalDistance);

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference userReference = referencia.child(userId).push();

            // Guardar el nombre ingresado por el usuario
            userReference.child("nombre").setValue(nombreRuta);

            String fechaNuevaRuta = nuevaRuta.getFecha();
            String coordInicialesNuevaRuta = nuevaRuta.getCoordenadasIniciales();
            String coordFinalesNuevaRuta = nuevaRuta.getCoordenadasFinales();

            userReference.child("fecha").setValue(fechaNuevaRuta);
            userReference.child("coordenadasIniciales").setValue(coordInicialesNuevaRuta);
            userReference.child("coordenadasFinales").setValue(coordFinalesNuevaRuta);
            userReference.child("tiempoContadorSegundos").setValue(tiempoContadorSegundos);
            userReference.child("distanciaTotal").setValue(nuevaRuta.getDistanciaTotal());

            guardarUltimasCoordenadas(coordenadasIniciales);

            Toast.makeText(SignRutas.this, "Se guardaron los datos", Toast.LENGTH_SHORT).show();
        }

        private void guardarUltimasCoordenadas(String coordinates) {
            getPreferences(MODE_PRIVATE).edit().putString("last_coordinates", coordinates).apply();
        }

        private String obtenerUltimasCoordenadas() {
            return getPreferences(MODE_PRIVATE).getString("last_coordinates", "No hay coordenadas almacenadas");
        }
    }