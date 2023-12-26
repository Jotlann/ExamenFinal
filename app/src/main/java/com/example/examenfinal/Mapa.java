package com.example.examenfinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Mapa extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private Marker earringMarker;
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference referencia;
    private FirebaseUser user;
    private FirebaseAuth auth;
    Button buttonBackM, buttonMark, buttonChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        buttonBackM = findViewById(R.id.buttonBackM);
        buttonMark = findViewById(R.id.buttonMark);
        buttonChange = findViewById(R.id.buttonChange);
        auth = FirebaseAuth.getInstance();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        referencia = FirebaseDatabase.getInstance().getReference("ubicacion");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        startLocationUpdates();

        //Botones
        buttonBackM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Mapa.this, Home.class);
                startActivity(intent);
            }
        });


        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMapTypeDialog();
            }
        });

        buttonMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (earringMarker != null) {
                    LatLng markerPosition = earringMarker.getPosition();
                    double latitude = markerPosition.latitude;
                    double longitude = markerPosition.longitude;

                    storeLocation(latitude, longitude);

                    earringMarker.setTitle("Nuevo Marcador");
                    earringMarker = null;
                    buttonMark.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    //Tipo de mapa
    private void showMapTypeDialog() {
        final String[] mapTypes = {"Normal", "Satelital", "Híbrido", "Terreno"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar Tipo de Mapa:");
        builder.setItems(mapTypes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Cambiar el tipo de mapa según la opción seleccionada
                switch (which) {
                    case 0:
                        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    case 1:
                        gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    case 2:
                        gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;
                    case 3:
                        gMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;
                }
            }
        });
        builder.show();
    }

    //Ubicacion y marcadores
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;

        LatLng chile = new LatLng(-30.603083, -71.202988);

        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(chile, 12));
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (earringMarker != null) {
                    earringMarker.remove();
                }
                earringMarker = googleMap.addMarker(new MarkerOptions().position(latLng));
                buttonMark.setVisibility(View.VISIBLE);
            }
        });
    }

    //Solicitud de ubicacion
    private void startLocationUpdates() {
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        storeLocation(latitude, longitude);
                    }
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(createLocationRequest(), locationCallback, null);
    }

    private void updateLocationOnMap(Location location) {
        LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (earringMarker == null) {
            earringMarker = gMap.addMarker(new MarkerOptions().position(userLatLng).title("Usuario"));
        } else {
            earringMarker.setPosition(userLatLng);
        }
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));
    }

    private void storeLocation(double latitude, double longitude) {
        user = auth.getCurrentUser();
        String location = "Localizacion";
        String uid = user.getUid();
        referencia = FirebaseDatabase.getInstance().getReference("ubicacion").child(uid);
    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000); // Intervalo de actualización de ubicación en milisegundos
        locationRequest.setFastestInterval(3000); // Intervalo más rápido de actualización de ubicación en milisegundos
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

}